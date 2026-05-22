package com.smartenergymanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de base abstraite représentant un bâtiment géré dans le système.
 * Contient la logique commune de gestion de la consommation.
 */
public abstract class Batiment {
    protected Long id;
    protected String nom;
    protected String adresse;
    protected double surface;
    protected List<Releve> releves;

    /**
     * Initialise un bâtiment avec ses informations de base.
     */
    public Batiment(String nom, String adresse, double surface) {
        this.nom = nom;
        this.adresse = adresse;
        this.surface = surface;
        this.releves = new ArrayList<>();
    }

    /**
     * Crée une copie du bâtiment (Pattern Prototype).
     * @return Une nouvelle instance clonée.
     */
    public abstract Batiment cloner();

    /**
     * Ajoute un nouveau relevé de consommation au bâtiment.
     */
    public void ajouterReleve(Releve releve) {
        this.releves.add(releve);
    }

    /**
     * Calcule la consommation totale sur une période donnée.
     * @param debut Date de début.
     * @param fin Date de fin.
     * @return Somme des quantités relevées dans l'intervalle.
     */
    public double getConsommationTotale(LocalDate debut, LocalDate fin) {
        return releves.stream()
                .filter(r -> !r.getDateHeure().toLocalDate().isBefore(debut) && !r.getDateHeure().toLocalDate().isAfter(fin))
                .mapToDouble(Releve::getQuantite)
                .sum();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public double getSurface() { return surface; }
    public void setSurface(double surface) { this.surface = surface; }
    public List<Releve> getReleves() { return releves; }
}
