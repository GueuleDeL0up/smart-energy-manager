package com.smartenergymanager.model;

import java.time.LocalDateTime;

/**
 * Représente une notification ou une alerte générée par le système suite à une anomalie.
 */
public class Alerte {
    private Long id;
    private String message;
    private LocalDateTime date;
    private String severite; // Ex: FAIBLE, MOYENNE, CRITIQUE

    /**
     * Constructeur d'une alerte.
     * @param message Description du problème.
     * @param date Date de détection.
     * @param severite Niveau d'importance.
     */
    public Alerte(String message, LocalDateTime date, String severite) {
        this.message = message;
        this.date = date;
        this.severite = severite;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getSeverite() { return severite; }
    public void setSeverite(String severite) { this.severite = severite; }
}
