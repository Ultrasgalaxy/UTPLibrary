package com.smartlibrary.repository;

import com.smartlibrary.db.DatabaseConnection;
import com.smartlibrary.model.Book;
import com.smartlibrary.model.Reservation;
import com.smartlibrary.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservaRepository {

    private final LibroRepository libroRepo = new LibroRepository();

    public boolean save(Reservation reservation) {
        String sql = """
            INSERT INTO reservas (usuario_id, libro_id, fecha_recojo, hora_recojo, estado)
            VALUES (?, ?, ?, ?, 'Activa')
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    reservation.getUser().getId());
            ps.setInt(2,    reservation.getBook().getId());
            ps.setDate(3,   Date.valueOf(reservation.getPickupDate()));
            ps.setString(4, reservation.getPickupSlot());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) reservation.setId(keys.getInt(1));
            libroRepo.updateEstado(reservation.getBook().getId(), "Reservado");
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Error guardar reserva: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> findActivas() {
        List<Reservation> list = new ArrayList<>();
        String sql = """
            SELECT r.*, l.titulo, l.autor,
                   u.nombre as usuario_nombre, u.tipo as usuario_tipo
            FROM reservas r
            JOIN libros l ON r.libro_id = l.id
            JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.estado = 'Activa'
            ORDER BY r.fecha_recojo DESC
            """;
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error listar reservas: " + e.getMessage());
        }
        return list;
    }

    /** Solo las reservas activas del usuario logueado (autoservicio: "Mis Reservas"). */
    public List<Reservation> findActivasPorUsuario(int usuarioId) {
        List<Reservation> list = new ArrayList<>();
        String sql = """
            SELECT r.*, l.titulo, l.autor,
                   u.nombre as usuario_nombre, u.tipo as usuario_tipo
            FROM reservas r
            JOIN libros l ON r.libro_id = l.id
            JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.estado = 'Activa' AND r.usuario_id = ?
            ORDER BY r.fecha_recojo DESC
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error listar reservas del usuario: " + e.getMessage());
        }
        return list;
    }

    public boolean libroTieneReservaActiva(int libroId) {
        String sql = "SELECT COUNT(*) FROM reservas WHERE libro_id=? AND estado='Activa'";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Error verificar reserva: " + e.getMessage());
        }
        return false;
    }

    /** Busca la reserva activa de un usuario para un libro especifico (para poder cancelarla). */
    public Optional<Reservation> findActivaByUsuarioYLibro(int usuarioId, int libroId) {
        String sql = """
            SELECT r.*, l.titulo, l.autor,
                   u.nombre as usuario_nombre, u.tipo as usuario_tipo
            FROM reservas r
            JOIN libros l ON r.libro_id = l.id
            JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.usuario_id = ? AND r.libro_id = ? AND r.estado = 'Activa'
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error buscar reserva activa: " + e.getMessage());
        }
        return Optional.empty();
    }

    /** Marca una reserva como cancelada. No toca el estado del libro (lo hace el Facade). */
    public boolean cancelar(int reservaId) {
        String sql = "UPDATE reservas SET estado='Cancelada' WHERE id=? AND estado='Activa'";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Error cancelar reserva: " + e.getMessage());
            return false;
        }
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Book book = new Book(rs.getString("titulo"), rs.getString("autor"));
        book.setId(rs.getInt("libro_id"));

        String tipo   = rs.getString("usuario_tipo");
        String nombre = rs.getString("usuario_nombre");
        User user = switch (tipo) {
            case "Profesor"      -> new com.smartlibrary.model.Teacher(nombre);
            case "Bibliotecario" -> new com.smartlibrary.model.Librarian(nombre);
            default              -> new com.smartlibrary.model.Student(nombre);
        };
        user.setId(rs.getInt("usuario_id"));

        Reservation res = new Reservation(
            user, book,
            rs.getDate("fecha_recojo").toLocalDate(),
            rs.getString("hora_recojo")
        );
        res.setId(rs.getInt("id"));
        if ("Cancelada".equals(rs.getString("estado"))) res.cancel();
        return res;
    }
}
