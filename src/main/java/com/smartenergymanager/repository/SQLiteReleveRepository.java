package com.smartenergymanager.repository;

import com.smartenergymanager.database.DatabaseConnection;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation SQLite de ReleveRepository.
 * Les dates sont stockées en TEXT au format ISO-8601 (LocalDateTime.toString()).
 */
public class SQLiteReleveRepository implements ReleveRepository {

    @Override
    public void save(Releve r) {
        if (r.getId() == null) {
            inserer(r);
        } else {
            mettreAJour(r);
        }
    }

    @Override
    public Releve findById(Long id) {
        String sql = "SELECT * FROM releves WHERE id = ?";
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
    public List<Releve> findByBatiment(Long batimentId) {
        List<Releve> liste = new ArrayList<>();
        String sql = "SELECT * FROM releves WHERE batiment_id = ? ORDER BY date_heure DESC";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, batimentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) liste.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    @Override
    public List<Releve> findAll() {
        List<Releve> liste = new ArrayList<>();
        String sql = "SELECT * FROM releves ORDER BY date_heure DESC";
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
        String sql = "DELETE FROM releves WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private void inserer(Releve r) {
        String sql = """
            INSERT INTO releves (batiment_id, date_heure, type_energie, quantite, cout_estime)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplirStatement(stmt, r);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) r.setId(keys.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mettreAJour(Releve r) {
        String sql = """
            UPDATE releves SET
              batiment_id=?, date_heure=?, type_energie=?, quantite=?, cout_estime=?
            WHERE id=?
            """;
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            remplirStatement(stmt, r);
            stmt.setLong(6, r.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void remplirStatement(PreparedStatement stmt, Releve r) throws SQLException {
        stmt.setLong(1, r.getBatimentId());
        stmt.setString(2, r.getDateHeure().toString()); // ISO-8601 automatique
        stmt.setString(3, r.getType().name());          // nom de l'enum
        stmt.setDouble(4, r.getQuantite());
        stmt.setDouble(5, r.getCoutEstime());
    }

    /** Reconstruit un objet Releve depuis un ResultSet. */
    private Releve mapRow(ResultSet rs) throws SQLException {
        LocalDateTime dateHeure = LocalDateTime.parse(rs.getString("date_heure"));
        TypeEnergie type        = TypeEnergie.valueOf(rs.getString("type_energie"));
        Releve r = new Releve(dateHeure, type, rs.getDouble("quantite"), rs.getDouble("cout_estime"));
        r.setId(rs.getLong("id"));
        r.setBatimentId(rs.getLong("batiment_id"));
        return r;
    }
}
