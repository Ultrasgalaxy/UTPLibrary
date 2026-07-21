package com.smartlibrary.repository;

import com.smartlibrary.db.DatabaseConnection;
import com.smartlibrary.model.Book;
import com.smartlibrary.model.Loan;
import com.smartlibrary.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoRepository {

    private final LibroRepository libroRepo = new LibroRepository();

    public boolean save(Loan loan) {
        String sql = """
            INSERT INTO prestamos
            (usuario_id, libro_id, fecha_recojo, hora_recojo, fecha_limite, estado)
            VALUES (?, ?, ?, ?, ?, 'Activo')
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    loan.getUser().getId());
            ps.setInt(2,    loan.getBook().getId());
            ps.setDate(3,   Date.valueOf(loan.getPickupDate()));
            ps.setString(4, loan.getPickupSlot());
            ps.setDate(5,   Date.valueOf(loan.getDueDate()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) loan.setId(keys.getInt(1));
            // Actualizar estado del libro en BD
            libroRepo.updateEstado(loan.getBook().getId(), "Prestado");
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Error guardar prestamo: " + e.getMessage());
            return false;
        }
    }

    public boolean marcarDevuelto(int prestamoId, int libroId) {
        String sql = "UPDATE prestamos SET estado='Devuelto', fecha_devolucion=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, prestamoId);
            ps.executeUpdate();
            libroRepo.updateEstado(libroId, "Disponible");
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Error devolver prestamo: " + e.getMessage());
            return false;
        }
    }

    public List<Loan> findActivos() {
        return findByEstado("Activo");
    }

    public List<Loan> findByUsuario(int usuarioId) {
        List<Loan> list = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo, l.autor, l.estado as libro_estado,
                   u.nombre as usuario_nombre, u.tipo as usuario_tipo
            FROM prestamos p
            JOIN libros l ON p.libro_id = l.id
            JOIN usuarios u ON p.usuario_id = u.id
            WHERE p.usuario_id = ? AND p.estado = 'Activo'
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error buscar prestamos usuario: " + e.getMessage());
        }
        return list;
    }

    public boolean tieneVencidos(int usuarioId) {
        String sql = """
            SELECT COUNT(*) FROM prestamos
            WHERE usuario_id = ? AND estado = 'Activo' AND fecha_limite < CURDATE()
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Error verificar vencidos: " + e.getMessage());
        }
        return false;
    }

    public int countActivosByUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE usuario_id=? AND estado='Activo'";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Error contar prestamos: " + e.getMessage());
        }
        return 0;
    }

    public List<Loan> findVencidos() {
        // Actualizar estado en BD primero
        try (Statement st = DatabaseConnection.getConnection().createStatement()) {
            st.executeUpdate(
                "UPDATE prestamos SET estado='Vencido' WHERE estado='Activo' AND fecha_limite < CURDATE()");
        } catch (SQLException e) {
            System.err.println("[DB] Error actualizar vencidos: " + e.getMessage());
        }
        return findByEstado("Vencido");
    }

    private List<Loan> findByEstado(String estado) {
        List<Loan> list = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo, l.autor, l.estado as libro_estado,
                   u.nombre as usuario_nombre, u.tipo as usuario_tipo
            FROM prestamos p
            JOIN libros l ON p.libro_id = l.id
            JOIN usuarios u ON p.usuario_id = u.id
            WHERE p.estado = ?
            ORDER BY p.fecha_recojo DESC
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error listar prestamos: " + e.getMessage());
        }
        return list;
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Book book = new Book(rs.getString("titulo"), rs.getString("autor"));
        book.setId(rs.getInt("libro_id"));

        User user;
        String tipo = rs.getString("usuario_tipo");
        String nombre = rs.getString("usuario_nombre");
        user = switch (tipo) {
            case "Profesor"      -> new com.smartlibrary.model.Teacher(nombre);
            case "Bibliotecario" -> new com.smartlibrary.model.Librarian(nombre);
            default              -> new com.smartlibrary.model.Student(nombre);
        };
        user.setId(rs.getInt("usuario_id"));

        LocalDate pickupDate = rs.getDate("fecha_recojo").toLocalDate();
        String    pickupSlot = rs.getString("hora_recojo");

        Loan loan = new Loan(user, book, pickupDate, pickupSlot);
        loan.setId(rs.getInt("id"));

        if (rs.getDate("fecha_devolucion") != null) loan.markReturned();
        return loan;
    }
}
