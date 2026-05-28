package com.smartenergymanager.model;

/**
 * Spécialisation pour les bâtiments de campus universitaires.
 */
public class BatimentUniversitaire extends Batiment {
    private String campus;

    public BatimentUniversitaire(String nom, String adresse, double surface, String campus) {
        super(nom, adresse, surface);
        this.campus = campus;
    }

    @Override
    public Batiment cloner() {
        BatimentUniversitaire clone = new BatimentUniversitaire(this.nom, this.adresse, this.surface, this.campus);
        clone.id = this.id;
        return clone;
    }

    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
}
