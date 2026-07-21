package com.smartlibrary.repository;

import com.smartlibrary.db.DatabaseConnection;
import com.smartlibrary.model.Book;
import com.smartlibrary.state.AvailableState;
import com.smartlibrary.state.BorrowedState;
import com.smartlibrary.state.ReservedState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository {

    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error listar libros: " + e.getMessage());
        }
        return list;
    }

    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error buscar libro: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean save(Book book) {
        String sql = "INSERT INTO libros (titulo, autor, estado) VALUES (?, ?, 'Disponible')";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) book.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Error guardar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean titleAuthorExists(String titulo, String autor) {
        String sql = "SELECT COUNT(*) FROM libros WHERE titulo = ? AND autor = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Error verificar libro: " + e.getMessage());
        }
        return false;
    }

    public void updateEstado(int libroId, String estado) {
        String sql = "UPDATE libros SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, libroId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Error actualizar estado libro: " + e.getMessage());
        }
    }

    /**
     * Elimina un libro por id. Como la tabla puede tener historial de
     * prestamos/reservas YA CERRADOS (devueltos/cancelados) que referencian
     * al libro por llave foranea, se borran primero esas filas historicas
     * dentro de la misma transaccion y luego se borra el libro. Si el libro
     * tuviera un prestamo o reserva ACTIVO, eso lo bloquea antes el Facade
     * (LibraryFacade.deleteBook), no este metodo.
     */
    public boolean delete(int id) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement psPrestamos = conn.prepareStatement("DELETE FROM prestamos WHERE libro_id = ?");
                 PreparedStatement psReservas  = conn.prepareStatement("DELETE FROM reservas  WHERE libro_id = ?");
                 PreparedStatement psLibro     = conn.prepareStatement("DELETE FROM libros    WHERE id = ?")) {

                psPrestamos.setInt(1, id);
                psPrestamos.executeUpdate();

                psReservas.setInt(1, id);
                psReservas.executeUpdate();

                psLibro.setInt(1, id);
                int rows = psLibro.executeUpdate();

                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[DB] Error eliminar libro: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error de transaccion al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book(rs.getString("titulo"), rs.getString("autor"));
        book.setId(rs.getInt("id"));
        String estado = rs.getString("estado");
        book.setState(switch (estado) {
            case "Prestado"  -> new BorrowedState();
            case "Reservado" -> new ReservedState();
            default          -> new AvailableState();
        });
        return book;
    }
}
