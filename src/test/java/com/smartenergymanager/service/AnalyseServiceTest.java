package com.smartenergymanager.service;

import com.smartenergymanager.model.Maison;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires d'AnalyseService.
 * Opère sur des objets en mémoire — aucune base de données requise.
 */
class AnalyseServiceTest {

    private AnalyseService service;
    private Maison batiment;

    @BeforeEach
    void setUp() {
        service  = new AnalyseService();
        batiment = new Maison("Test", "1 rue A", 100.0, 4);
    }

    // ── estimerFactureMensuelle ───────────────────────────────────────────────

    @Test
    void estimerFactureMensuelle_sansReleve_retourneZero() {
        assertEquals(0.0, service.estimerFactureMensuelle(batiment, 5));
    }

    @Test
    void estimerFactureMensuelle_sommeLesCoutsduMois() {
        batiment.ajouterReleve(releve(LocalDateTime.of(2025, 5, 10, 10, 0), 50.0, 9.0));
        batiment.ajouterReleve(releve(LocalDateTime.of(2025, 5, 20, 14, 0), 30.0, 5.4));
        batiment.ajouterReleve(releve(LocalDateTime.of(2025, 6,  1, 8,  0), 40.0, 7.2)); // autre mois

        assertEquals(14.4, service.estimerFactureMensuelle(batiment, 5), 0.001);
    }

    // ── identifierBatimentPlusConsommateur ────────────────────────────────────

    @Test
    void identifierBatimentPlusConsommateur_listeVide_retourneNull() {
        assertNull(service.identifierBatimentPlusConsommateur(List.of()));
    }

    @Test
    void identifierBatimentPlusConsommateur_sansReleves_retourneNull() {
        Maison m1 = new Maison("A", "Rue A", 80, 3);
        Maison m2 = new Maison("B", "Rue B", 90, 4);
        // Aucun relevé ajouté
        assertNull(service.identifierBatimentPlusConsommateur(List.of(m1, m2)));
    }

    @Test
    void identifierBatimentPlusConsommateur_retourneLePlusGros() {
        Maison petit  = new Maison("Petit", "Rue A", 50, 2);
        Maison grand  = new Maison("Grand", "Rue B", 200, 8);

        petit.ajouterReleve(releve(LocalDateTime.now(), 10.0, 1.8));
        grand.ajouterReleve(releve(LocalDateTime.now(), 80.0, 14.4));
        grand.ajouterReleve(releve(LocalDateTime.now(), 60.0, 10.8));

        assertEquals("Grand",
                service.identifierBatimentPlusConsommateur(List.of(petit, grand)).getNom());
    }

    // ── detecterAnomalies ────────────────────────────────────────────────────

    @Test
    void detecterAnomalies_moinsDe2Releves_retourneListeVide() {
        batiment.ajouterReleve(releve(LocalDateTime.now(), 100.0, 18.0));
        assertTrue(service.detecterAnomalies(batiment).isEmpty());
    }

    @Test
    void detecterAnomalies_sansAnomalies_retourneListeVide() {
        // Consommations homogènes → pas d'anomalie
        batiment.ajouterReleve(releve(LocalDateTime.now(), 10.0, 1.8));
        batiment.ajouterReleve(releve(LocalDateTime.now(), 11.0, 1.98));
        batiment.ajouterReleve(releve(LocalDateTime.now(), 10.5, 1.89));
        assertTrue(service.detecterAnomalies(batiment).isEmpty());
    }

    @Test
    void detecterAnomalies_avecValeurExtreme_detecteAlerte() {
        // 10 valeurs normales stables
        for (int i = 0; i < 10; i++) {
            batiment.ajouterReleve(releve(LocalDateTime.now(), 10.0, 1.8));
        }
        // 1 valeur très éloignée de la moyenne → dépasse moyenne + 2σ
        batiment.ajouterReleve(releve(LocalDateTime.now(), 1000.0, 180.0));

        assertFalse(service.detecterAnomalies(batiment).isEmpty());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Releve releve(LocalDateTime dateHeure, double quantite, double cout) {
        return new Releve(dateHeure, TypeEnergie.ELECTRICITE, quantite, cout);
    }
}
