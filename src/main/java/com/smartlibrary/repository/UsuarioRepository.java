package com.smartlibrary.repository;

import com.smartlibrary.db.DatabaseConnection;
import com.smartlibrary.model.Librarian;
import com.smartlibrary.model.Student;
import com.smartlibrary.model.Teacher;
import com.smartlibrary.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    /** Busca un usuario por username y password para autenticacion. */
    public Optional<User> findByCredentials(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error autenticacion: " + e.getMessage());
        }
        return Optional.empty();
    }

    /** Obtiene todos los usuarios. */
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DB] Error listar usuarios: " + e.getMessage());
        }
        return list;
    }

    /** Inserta un nuevo usuario. */
    public boolean save(User user, String username, String password) {
        String sql = "INSERT INTO usuarios (nombre, username, password, tipo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, tipoFromUser(user));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) user.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Error guardar usuario: " + e.getMessage());
            return false;
        }
    }

    /** Verifica si el username ya existe. */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Error verificar username: " + e.getMessage());
        }
        return false;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int    id     = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String tipo   = rs.getString("tipo");
        User user = switch (tipo) {
            case "Profesor"      -> new Teacher(nombre);
            case "Bibliotecario" -> new Librarian(nombre);
            default              -> new Student(nombre);
        };
        user.setId(id);
        return user;
    }

    private String tipoFromUser(User user) {
        if (user instanceof Teacher)   return "Profesor";
        if (user instanceof Librarian) return "Bibliotecario";
        return "Estudiante";
    }
}
