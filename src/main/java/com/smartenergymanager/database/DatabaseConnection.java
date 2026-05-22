package com.smartenergymanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitaire gérant la connexion à la base de données SQLite.
 * Utilise le pattern Singleton pour maintenir une connexion unique.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:smart_energy.db";
    private static Connection connection;

    /**
     * Retourne la connexion active ou en crée une nouvelle si nécessaire.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }
}
