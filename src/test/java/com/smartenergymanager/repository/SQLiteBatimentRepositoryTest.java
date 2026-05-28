package com.smartenergymanager.repository;

import com.smartenergymanager.database.DatabaseConnection;
import com.smartenergymanager.model.Appartement;
import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Bureau;
import com.smartenergymanager.model.Maison;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration de SQLiteBatimentRepository.
 * Chaque test tourne sur une base SQLite en mémoire isolée (:memory:).
 */
class SQLiteBatimentRepositoryTest {

    private SQLiteBatimentRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        // Nouvelle base en mémoire pour chaque test → isolation parfaite
        DatabaseConnection.reinitialiser("jdbc:sqlite::memory:");
        DatabaseConnection.initialiserSchema();
        repository = new SQLiteBatimentRepository();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Réinitialise la connexion pour ne pas polluer les autres tests
        DatabaseConnection.reinitialiser("jdbc:sqlite::memory:");
    }

    // ── save / findById ───────────────────────────────────────────────────────

    @Test
    void save_insert_assigneUnId() {
        Maison m = new Maison("Villa Test", "1 rue A", 100.0, 4);
        repository.save(m);
        assertNotNull(m.getId());
    }

    @Test
    void save_insert_puisRecuperableParId() {
        Maison m = new Maison("Maison Dupont", "12 rue des Lilas", 120.0, 5);
        repository.save(m);

        Batiment retrouve = repository.findById(m.getId());
        assertNotNull(retrouve);
        assertEquals("Maison Dupont", retrouve.getNom());
        assertEquals("12 rue des Lilas", retrouve.getAdresse());
        assertEquals(120.0, retrouve.getSurface(), 0.001);
        assertInstanceOf(Maison.class, retrouve);
        assertEquals(5, ((Maison) retrouve).getNbPieces());
    }

    @Test
    void save_update_modifieLesDonnees() {
        Maison m = new Maison("Ancien Nom", "1 rue A", 80.0, 3);
        repository.save(m);

        m.setNom("Nouveau Nom");
        m.setSurface(95.0);
        repository.save(m);

        Batiment retrouve = repository.findById(m.getId());
        assertEquals("Nouveau Nom", retrouve.getNom());
        assertEquals(95.0, retrouve.getSurface(), 0.001);
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_retourneTousLesBatiments() {
        repository.save(new Maison("A", "Rue A", 80, 3));
        repository.save(new Bureau("B", "Rue B", 200, "Acme", 20));
        repository.save(new Appartement("C", "Rue C", 55, 3, "B12"));

        List<Batiment> liste = repository.findAll();
        assertEquals(3, liste.size());
    }

    @Test
    void findAll_baseVide_retourneListeVide() {
        assertTrue(repository.findAll().isEmpty());
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_supprimeLeRecord() {
        Maison m = new Maison("A supprimer", "Rue X", 60.0, 2);
        repository.save(m);

        repository.delete(m.getId());

        assertNull(repository.findById(m.getId()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findById_idInconnu_retourneNull() {
        assertNull(repository.findById(999L));
    }

    // ── Polymorphisme ─────────────────────────────────────────────────────────

    @Test
    void sauvegarderEtRecuperer_appartement() {
        Appartement a = new Appartement("Appart 3B", "10 av. des Fleurs", 45.0, 3, "3B");
        repository.save(a);

        Batiment retrouve = repository.findById(a.getId());
        assertInstanceOf(Appartement.class, retrouve);
        assertEquals(3, ((Appartement) retrouve).getEtage());
        assertEquals("3B", ((Appartement) retrouve).getNumAppartement());
    }

    @Test
    void sauvegarderEtRecuperer_bureau() {
        Bureau b = new Bureau("Bureau Central", "1 place de la Bourse", 300.0, "MegaCorp", 50);
        repository.save(b);

        Batiment retrouve = repository.findById(b.getId());
        assertInstanceOf(Bureau.class, retrouve);
        assertEquals("MegaCorp", ((Bureau) retrouve).getEntreprise());
        assertEquals(50, ((Bureau) retrouve).getNbPostes());
    }
}
