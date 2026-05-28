package com.smartenergymanager.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilitaire gérant la connexion à la base de données SQLite.
 * Utilise le pattern Singleton pour maintenir une connexion unique.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:smart_energy.db";
    private static Connection connection;

    /**
     * Retourne la connexion active ou en crée une nouvelle si nécessaire.
     * Active les clés étrangères SQLite (désactivées par défaut).
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    /**
     * Lit schema.sql depuis le classpath et exécute chaque instruction CREATE TABLE.
     * À appeler une seule fois au démarrage de l'application (dans App.start()).
     */
    public static void initialiserSchema() throws SQLException, IOException {
        InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql");
        String contenu = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // Découpe sur ";" pour exécuter chaque instruction séparément
        try (Statement stmt = getConnection().createStatement()) {
            for (String instruction : contenu.split(";")) {
                String sql = instruction.strip();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    stmt.execute(sql);
                }
            }
        }
    }
}
