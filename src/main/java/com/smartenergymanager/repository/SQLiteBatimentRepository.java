package com.smartenergymanager.repository;

import com.smartenergymanager.database.DatabaseConnection;
import com.smartenergymanager.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation SQLite de BatimentRepository.
 * Utilise le pattern Single Table Inheritance : tous les sous-types
 * de Batiment sont stockés dans la même table avec un discriminateur "type".
 */
public class SQLiteBatimentRepository implements BatimentRepository {

    @Override
    public void save(Batiment b) {
        if (b.getId() == null) {
            inserer(b);
        } else {
            mettreAJour(b);
        }
    }

    @Override
    public Batiment findById(Long id) {
        String sql = "SELECT * FROM batiments WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Batiment> findAll() {
        List<Batiment> liste = new ArrayList<>();
        String sql = "SELECT * FROM batiments ORDER BY nom";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) liste.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM batiments WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private void inserer(Batiment b) {
        String sql = """
            INSERT INTO batiments
              (type, nom, adresse, surface, nb_pieces, etage, num_appartement, entreprise, nb_postes, campus)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplirStatement(stmt, b);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) b.setId(keys.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mettreAJour(Batiment b) {
        String sql = """
            UPDATE batiments SET
              type=?, nom=?, adresse=?, surface=?,
              nb_pieces=?, etage=?, num_appartement=?, entreprise=?, nb_postes=?, campus=?
            WHERE id=?
            """;
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            remplirStatement(stmt, b);
            stmt.setLong(11, b.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Remplit les paramètres communs + ceux du sous-type (colonnes nullables). */
    private void remplirStatement(PreparedStatement stmt, Batiment b) throws SQLException {
        stmt.setString(1, b.getClass().getSimpleName());
        stmt.setString(2, b.getNom());
        stmt.setString(3, b.getAdresse());
        stmt.setDouble(4, b.getSurface());

        // Valeurs par défaut : null pour toutes les colonnes spécifiques
        stmt.setNull(5, Types.INTEGER); // nb_pieces
        stmt.setNull(6, Types.INTEGER); // etage
        stmt.setNull(7, Types.VARCHAR); // num_appartement
        stmt.setNull(8, Types.VARCHAR); // entreprise
        stmt.setNull(9, Types.INTEGER); // nb_postes
        stmt.setNull(10, Types.VARCHAR); // campus

        // On écrase uniquement les colonnes du sous-type concerné
        switch (b) {
            case Maison m               -> stmt.setInt(5, m.getNbPieces());
            case Appartement a          -> { stmt.setInt(6, a.getEtage());
                                             stmt.setString(7, a.getNumAppartement()); }
            case Bureau bu              -> { stmt.setString(8, bu.getEntreprise());
                                             stmt.setInt(9, bu.getNbPostes()); }
            case BatimentUniversitaire u -> stmt.setString(10, u.getCampus());
            default                     -> { /* type inconnu, colonnes restent null */ }
        }
    }

    /** Reconstruit le bon objet Java selon le discriminateur "type". */
    private Batiment mapRow(ResultSet rs) throws SQLException {
        String type    = rs.getString("type");
        String nom     = rs.getString("nom");
        String adresse = rs.getString("adresse");
        double surface = rs.getDouble("surface");

        Batiment b = switch (type) {
            case "Maison" ->
                new Maison(nom, adresse, surface, rs.getInt("nb_pieces"));
            case "Appartement" ->
                new Appartement(nom, adresse, surface, rs.getInt("etage"), rs.getString("num_appartement"));
            case "Bureau" ->
                new Bureau(nom, adresse, surface, rs.getString("entreprise"), rs.getInt("nb_postes"));
            case "BatimentUniversitaire" ->
                new BatimentUniversitaire(nom, adresse, surface, rs.getString("campus"));
            default -> throw new IllegalStateException("Type de bâtiment inconnu : " + type);
        };
        b.setId(rs.getLong("id"));
        return b;
    }
}
