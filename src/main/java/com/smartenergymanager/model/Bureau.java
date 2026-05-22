package com.smartenergymanager.model;

/**
 * Spécialisation pour les bâtiments professionnels.
 */
public class Bureau extends Batiment {
    private String entreprise;
    private int nbPostes;

    public Bureau(String nom, String adresse, double surface, String entreprise, int nbPostes) {
        super(nom, adresse, surface);
        this.entreprise = entreprise;
        this.nbPostes = nbPostes;
    }

    @Override
    public Batiment cloner() {
        Bureau clone = new Bureau(this.nom, this.adresse, this.surface, this.entreprise, this.nbPostes);
        clone.id = this.id;
        return clone;
    }

    public String getEntreprise() { return entreprise; }
    public void setEntreprise(String entreprise) { this.entreprise = entreprise; }
    public int getNbPostes() { return nbPostes; }
    public void setNbPostes(int nbPostes) { this.nbPostes = nbPostes; }
}
