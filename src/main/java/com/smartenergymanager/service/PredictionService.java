package com.smartenergymanager.service;

import com.smartenergymanager.model.Releve;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Service de prédiction de consommation par moyenne mobile pondérée.
 *
 * Algorithme : on agrège les relevés par mois, on prend les 3 derniers,
 * et on calcule une moyenne pondérée (mois le plus récent = poids x3).
 *
 *   Prévision = (M_{n-3}×1 + M_{n-2}×2 + M_{n-1}×3) / 6
 */
public class PredictionService {

    private static final DateTimeFormatter FMT   = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final int   NB_MOIS           = 3;
    private static final int[] POIDS             = {1, 2, 3};
    private static final int   TOTAL_POIDS       = 6; // 1+2+3

    /**
     * Calcule la consommation prédite pour le mois suivant le dernier mois connu.
     *
     * @param releves Liste des relevés (déjà filtrés si besoin).
     * @return Valeur prédite (≥ 0), ou -1 si pas assez de données (< 3 mois distincts).
     */
    public double predireProchainMois(List<Releve> releves) {
        TreeMap<YearMonth, Double> parMois = releves.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDateHeure()),
                        TreeMap::new,
                        Collectors.summingDouble(Releve::getQuantite)));

        if (parMois.size() < NB_MOIS) return -1;

        List<Double> valeurs = List.copyOf(parMois.values());
        int n = valeurs.size();

        double prediction = 0;
        for (int i = 0; i < NB_MOIS; i++) {
            prediction += valeurs.get(n - NB_MOIS + i) * POIDS[i];
        }
        return prediction / TOTAL_POIDS;
    }

    /**
     * Retourne le dernier mois présent dans les relevés (format MM/yyyy).
     * Utilisé pour relier le graphique historique à la prévision.
     */
    public String dernierMoisConnu(List<Releve> releves) {
        return releves.stream()
                .map(r -> YearMonth.from(r.getDateHeure()))
                .max(YearMonth::compareTo)
                .map(m -> m.format(FMT))
                .orElse("");
    }

    /** Retourne le nom du prochain mois à prédire (format MM/yyyy). */
    public String nomProchainMois(List<Releve> releves) {
        return releves.stream()
                .map(r -> YearMonth.from(r.getDateHeure()))
                .max(YearMonth::compareTo)
                .map(m -> m.plusMonths(1).format(FMT))
                .orElse(YearMonth.now().plusMonths(1).format(FMT));
    }
}
