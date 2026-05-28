package com.smartenergymanager.service;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Maison;
import com.smartenergymanager.repository.BatimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de BatimentService.
 * Utilise un repository en mémoire pour rester isolé de la base de données.
 */
class BatimentServiceTest {

    private BatimentService service;

    @BeforeEach
    void setUp() {
        service = new BatimentService(new InMemoryBatimentRepository());
    }

    @Test
    void creerBatiment_assigneUnId() {
        Maison m = new Maison("Villa Test", "1 rue A", 100.0, 4);
        service.creerBatiment(m);
        assertNotNull(m.getId(), "L'id doit être assigné après save()");
    }

    @Test
    void findAll_retourneTousLesBatiments() {
        service.creerBatiment(new Maison("A", "Rue A", 80, 3));
        service.creerBatiment(new Maison("B", "Rue B", 90, 4));
        assertEquals(2, service.findAll().size());
    }

    @Test
    void findById_retourneLeBonBatiment() {
        Maison m = new Maison("Casa", "2 rue B", 120.0, 5);
        service.creerBatiment(m);
        Batiment retrouve = service.findById(m.getId());
        assertNotNull(retrouve);
        assertEquals("Casa", retrouve.getNom());
    }

    @Test
    void supprimerBatiment_retireDelaListe() {
        Maison m = new Maison("A supprimer", "3 rue C", 60.0, 2);
        service.creerBatiment(m);
        service.supprimerBatiment(m.getId());
        assertNull(service.findById(m.getId()));
        assertEquals(0, service.findAll().size());
    }

    @Test
    void modifierBatiment_metsAJourLeNom() {
        Maison m = new Maison("Ancien nom", "4 rue D", 75.0, 3);
        service.creerBatiment(m);
        m.setNom("Nouveau nom");
        service.modifierBatiment(m);
        assertEquals("Nouveau nom", service.findById(m.getId()).getNom());
    }

    @Test
    void dupliquerBatiment_creerUneCopiePersistee() {
        Maison m = new Maison("Original", "5 rue E", 110.0, 4);
        service.creerBatiment(m);

        Batiment clone = service.dupliquerBatiment(m.getId());

        assertNotNull(clone);
        assertNotEquals(m.getId(), clone.getId(), "Le clone doit avoir un ID différent");
        assertEquals(m.getNom(), clone.getNom());
        assertEquals(2, service.findAll().size());
    }

    @Test
    void dupliquerBatiment_retourneNullSiIdInconnu() {
        assertNull(service.dupliquerBatiment(999L));
    }

    // ── Repository en mémoire ─────────────────────────────────────────────────

    static class InMemoryBatimentRepository implements BatimentRepository {
        private final Map<Long, Batiment> store = new HashMap<>();
        private long nextId = 1L;

        @Override
        public void save(Batiment b) {
            if (b.getId() == null) b.setId(nextId++);
            store.put(b.getId(), b);
        }

        @Override
        public Batiment findById(Long id) { return store.get(id); }

        @Override
        public List<Batiment> findAll() { return new ArrayList<>(store.values()); }

        @Override
        public void delete(Long id) { store.remove(id); }
    }
}
