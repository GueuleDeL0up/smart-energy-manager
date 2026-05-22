package com.smartenergymanager.model;

import java.time.LocalDateTime;

/**
 * Représente une donnée brute de consommation liée à un appareil spécifique.
 */
public class Consumption {
    private Long id;
    private LocalDateTime timestamp;
    private double value;
    private String unit;

    public Consumption(LocalDateTime timestamp, double value, String unit) {
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
