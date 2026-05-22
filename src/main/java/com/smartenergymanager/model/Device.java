package com.smartenergymanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un équipement ou appareil connecté capable de mesurer sa propre consommation.
 */
public class Device {
    private Long id;
    private String name;
    private String type;
    private List<Consumption> consumptions;

    public Device(String name, String type) {
        this.name = name;
        this.type = type;
        this.consumptions = new ArrayList<>();
    }

    /**
     * Enregistre une nouvelle mesure de consommation pour cet appareil.
     */
    public void addConsumption(Consumption consumption) {
        this.consumptions.add(consumption);
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<Consumption> getConsumptions() { return consumptions; }
}
