package com.smartlibrary.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexion unica a la base de datos MySQL (Singleton).
 * Base de datos: utp_library — Servidor local MySQL 8
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/utp_library?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "Ultrasgalaxy.@";

    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Conexion establecida con utp_library");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error de conexion: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a la base de datos.", e);
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Conexion cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error al cerrar: " + e.getMessage());
        }
    }
}
