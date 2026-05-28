package com.smartenergymanager.model;

/**
 * Spécialisation du bâtiment pour les habitations individuelles.
 */
public class Maison extends Batiment {
    private int nbPieces;

    public Maison(String nom, String adresse, double surface, int nbPieces) {
        super(nom, adresse, surface);
        this.nbPieces = nbPieces;
    }

    @Override
    public Batiment cloner() {
        Maison clone = new Maison(this.nom, this.adresse, this.surface, this.nbPieces);
        clone.id = this.id;
        return clone;
    }

    public int getNbPieces() { return nbPieces; }
    public void setNbPieces(int nbPieces) { this.nbPieces = nbPieces; }
}
