package com.smartenergymanager.service;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;
import com.smartenergymanager.repository.ReleveRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Service dédié à l'importation et à la génération de données de test.
 */
public class ImportExportService {

    private final ReleveRepository releveRepository;
    private final Random rng = new Random();

    /**
     * Plage quantité [min, max] et tarif €/unité pour chaque type d'énergie.
     * Utilisé pour générer des valeurs réalistes.
     */
    private record ConfigEnergie(double minQte, double maxQte, double tarifParUnite) {}

    private static final Map<TypeEnergie, ConfigEnergie> CONFIG = Map.of(
            TypeEnergie.ELECTRICITE,        new ConfigEnergie(10,  50,  0.18),
            TypeEnergie.EAU,                new ConfigEnergie(1,   8,   4.50),
            TypeEnergie.GAZ,                new ConfigEnergie(5,   30,  0.95),
            TypeEnergie.CHAUFFAGE,          new ConfigEnergie(20,  80,  0.07),
            TypeEnergie.CLIMATISATION,      new ConfigEnergie(5,   25,  0.18),
            TypeEnergie.PRODUCTION_SOLAIRE, new ConfigEnergie(2,   15,  0.0)
    );

    // Nombre de relevés générés par type d'énergie et par mois
    private static final int RELEVES_PAR_MOIS = 3;
    // Nombre de mois en arrière à couvrir
    private static final int MOIS_HISTORIQUE  = 6;

    public ImportExportService(ReleveRepository releveRepository) {
        this.releveRepository = releveRepository;
    }

    /**
     * Génère des relevés fictifs réalistes pour chaque bâtiment fourni,
     * sur les {@value #MOIS_HISTORIQUE} derniers mois.
     *
     * @param batiments Liste des bâtiments pour lesquels générer des données.
     * @return Nombre total de relevés insérés en base.
     */
    public int genererDonneesTest(List<Batiment> batiments) {
        if (batiments.isEmpty()) return 0;

        List<Releve> aInserer = new ArrayList<>();
        YearMonth moisCourant = YearMonth.now();

        for (Batiment batiment : batiments) {
            for (int delta = MOIS_HISTORIQUE; delta >= 0; delta--) {
                YearMonth mois = moisCourant.minusMonths(delta);
                for (TypeEnergie type : TypeEnergie.values()) {
                    for (int i = 0; i < RELEVES_PAR_MOIS; i++) {
                        aInserer.add(genererReleve(batiment.getId(), mois, type));
                    }
                }
            }
        }

        aInserer.forEach(releveRepository::save);
        return aInserer.size();
    }

    /**
     * Importe des relevés depuis un fichier CSV.
     * TODO (BLOC 4.7) : implémenter le parsing CSV
     */
    public List<Releve> importCSV(String filePath) {
        return new ArrayList<>();
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private Releve genererReleve(Long batimentId, YearMonth mois, TypeEnergie type) {
        ConfigEnergie cfg = CONFIG.get(type);

        double quantite = cfg.minQte() + rng.nextDouble() * (cfg.maxQte() - cfg.minQte());
        double cout     = quantite * cfg.tarifParUnite();

        Releve r = new Releve(dateAleatoire(mois), type,
                Math.round(quantite * 10.0) / 10.0,
                Math.round(cout     * 100.0) / 100.0);
        r.setBatimentId(batimentId);
        return r;
    }

    /** Choisit une date/heure aléatoire dans le mois donné. */
    private LocalDateTime dateAleatoire(YearMonth mois) {
        int jour   = rng.nextInt(mois.lengthOfMonth()) + 1;
        int heure  = rng.nextInt(24);
        int minute = rng.nextInt(60);
        return LocalDateTime.of(mois.getYear(), mois.getMonth(), jour, heure, minute);
    }
}
