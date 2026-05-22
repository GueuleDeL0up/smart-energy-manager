package com.smartenergymanager.service;

import com.smartenergymanager.model.Alerte;
import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.TypeEnergie;
import java.util.ArrayList;
import java.util.List;

/**
 * Service chargé de l'analyse des données de consommation.
 */
public class AnalyseService {
    
    /**
     * Analyse un bâtiment pour détecter des consommations anormales.
     * @return Une liste d'alertes si des anomalies sont trouvées.
     */
    public List<Alerte> detecterAnomalies(Batiment b) {
        // Logique à implémenter : comparaison avec des seuils, etc.
        return new ArrayList<>();
    }

    /**
     * Estime le coût de la facture pour un mois donné.
     */
    public double estimerFactureMensuelle(Batiment b, int mois) {
        // Logique à implémenter : somme des consos * tarifs
        return 0.0;
    }

    /**
     * Compare les bâtiments pour trouver celui qui consomme le plus.
     */
    public Batiment identifierBatimentPlusConsommateur(List<Batiment> batiments) {
        return null;
    }

    /**
     * Calcule le pourcentage d'évolution de la consommation.
     */
    public double calculerEvolution(Batiment b, TypeEnergie type) {
        return 0.0;
    }
}
