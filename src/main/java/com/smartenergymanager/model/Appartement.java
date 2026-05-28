package com.smartenergymanager.model;

/**
 * Spécialisation pour les appartements dans des immeubles collectifs.
 */
public class Appartement extends Batiment {
    private int etage;
    private String numAppartement;

    public Appartement(String nom, String adresse, double surface, int etage, String numAppartement) {
        super(nom, adresse, surface);
        this.etage = etage;
        this.numAppartement = numAppartement;
    }

    @Override
    public Batiment cloner() {
        Appartement clone = new Appartement(this.nom, this.adresse, this.surface, this.etage, this.numAppartement);
        clone.id = this.id;
        return clone;
    }

    public int getEtage() { return etage; }
    public void setEtage(int etage) { this.etage = etage; }
    public String getNumAppartement() { return numAppartement; }
    public void setNumAppartement(String numAppartement) { this.numAppartement = numAppartement; }
}
