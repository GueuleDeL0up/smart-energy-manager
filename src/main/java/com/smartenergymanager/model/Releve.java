package com.smartenergymanager.model;

import java.time.LocalDateTime;

/**
 * Représente une mesure ponctuelle de consommation d'énergie.
 */
public class Releve {
    private Long id;
    private Long batimentId;   // clé étrangère vers la table batiments
    private LocalDateTime dateHeure;
    private TypeEnergie type;
    private double quantite;
    private double coutEstime;

    /**
     * Constructeur d'un relevé.
     * @param date Date et heure de la mesure.
     * @param type Type d'énergie concerné.
     * @param quantite Valeur mesurée.
     * @param cout Coût estimé associé à cette consommation.
     */
    public Releve(LocalDateTime date, TypeEnergie type, double quantite, double cout) {
        this.dateHeure = date;
        this.type = type;
        this.quantite = quantite;
        this.coutEstime = cout;
    }

    // Getters et Setters pour l'accès aux données
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBatimentId() { return batimentId; }
    public void setBatimentId(Long batimentId) { this.batimentId = batimentId; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public TypeEnergie getType() { return type; }
    public void setType(TypeEnergie type) { this.type = type; }
    public double getQuantite() { return quantite; }
    public void setQuantite(double quantite) { this.quantite = quantite; }
    public double getCoutEstime() { return coutEstime; }
    public void setCoutEstime(double coutEstime) { this.coutEstime = coutEstime; }
}
