package com.smartenergymanager.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

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
     * Ferme la connexion courante et en ouvre une nouvelle sur l'URL donnée.
     * Réservé aux tests d'intégration (ex: "jdbc:sqlite::memory:").
     */
    public static void reinitialiser(String url) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = DriverManager.getConnection(url);
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
    }

    /**
     * Lit schema.sql depuis le classpath et exécute chaque instruction CREATE TABLE.
     * À appeler une seule fois au démarrage de l'application (dans App.start()).
     */
    public static void initialiserSchema() throws SQLException, IOException {
        InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql");
        String contenu = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // Supprime les lignes de commentaires AVANT de découper sur ";"
        // (sinon le premier bloc commence par "--" et toute la CREATE TABLE est ignorée)
        String sqlPropre = Arrays.stream(contenu.split("\n"))
                .filter(ligne -> !ligne.strip().startsWith("--"))
                .collect(Collectors.joining("\n"));

        try (Statement stmt = getConnection().createStatement()) {
            for (String instruction : sqlPropre.split(";")) {
                String sql = instruction.strip();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }
}
