package com.smartenergymanager.service;

import com.smartenergymanager.model.Alerte;
import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service chargé de l'analyse des données de consommation.
 * Opère sur la liste en mémoire des relevés d'un bâtiment (Batiment.getReleves()).
 */
public class AnalyseService {

    /**
     * Détecte les relevés anormaux : valeur > moyenne + 2 × écart-type.
     */
    public List<Alerte> detecterAnomalies(Batiment b) {
        List<Releve> releves = b.getReleves();
        List<Alerte> alertes = new ArrayList<>();
        if (releves.size() < 2) return alertes;

        double moyenne = releves.stream().mapToDouble(Releve::getQuantite).average().orElse(0);
        double variance = releves.stream()
                .mapToDouble(r -> Math.pow(r.getQuantite() - moyenne, 2))
                .average().orElse(0);
        double ecartType = Math.sqrt(variance);
        double seuil = moyenne + 2 * ecartType;

        for (Releve r : releves) {
            if (r.getQuantite() > seuil) {
                alertes.add(new Alerte(
                        "Consommation anormale : " + r.getQuantite() + " (seuil " + String.format("%.1f", seuil) + ")",
                        r.getDateHeure(),
                        "HAUTE"));
            }
        }
        return alertes;
    }

    /**
     * Somme des coûts estimés pour un mois donné (1=janvier … 12=décembre).
     */
    public double estimerFactureMensuelle(Batiment b, int mois) {
        return b.getReleves().stream()
                .filter(r -> r.getDateHeure().getMonthValue() == mois)
                .mapToDouble(Releve::getCoutEstime)
                .sum();
    }

    /**
     * Retourne le bâtiment dont la somme des relevés est la plus élevée.
     * Retourne null si la liste est vide ou si aucun bâtiment n'a de relevé.
     */
    public Batiment identifierBatimentPlusConsommateur(List<Batiment> batiments) {
        return batiments.stream()
                .filter(b -> !b.getReleves().isEmpty())
                .max(Comparator.comparingDouble(b ->
                        b.getReleves().stream().mapToDouble(Releve::getQuantite).sum()))
                .orElse(null);
    }

    /**
     * Calcule l'évolution en % entre le mois courant et le mois précédent
     * pour un type d'énergie donné.
     * Retourne 0 si le mois précédent n'a aucun relevé.
     */
    public double calculerEvolution(Batiment b, TypeEnergie type) {
        int moisCourant   = LocalDate.now().getMonthValue();
        int moisPrecedent = LocalDate.now().minusMonths(1).getMonthValue();

        double consoN = b.getReleves().stream()
                .filter(r -> r.getType() == type && r.getDateHeure().getMonthValue() == moisCourant)
                .mapToDouble(Releve::getQuantite).sum();

        double consoN1 = b.getReleves().stream()
                .filter(r -> r.getType() == type && r.getDateHeure().getMonthValue() == moisPrecedent)
                .mapToDouble(Releve::getQuantite).sum();

        if (consoN1 == 0) return 0.0;
        return ((consoN - consoN1) / consoN1) * 100.0;
    }
}
