package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Contrôleur du formulaire de saisie d'un relevé de consommation.
 */
public class ConsommationFormController {

    @FXML private ComboBox<Batiment>   cbxBatiment;
    @FXML private DatePicker           dpDate;
    @FXML private TextField            txtHeure;
    @FXML private ComboBox<TypeEnergie> cbxTypeEnergie;
    @FXML private TextField            txtQuantite;
    @FXML private TextField            txtCout;

    @FXML
    public void initialize() {
        cbxTypeEnergie.setItems(FXCollections.observableArrayList(TypeEnergie.values()));
        dpDate.setValue(LocalDate.now());
        txtHeure.setText("00:00");
    }

    /**
     * Appelé par ConsommationsController pour peupler le ComboBox des bâtiments.
     */
    public void remplirBatiments(List<Batiment> batiments) {
        cbxBatiment.setItems(FXCollections.observableArrayList(batiments));
        cbxBatiment.setCellFactory(lv -> celleBatiment());
        cbxBatiment.setButtonCell(celleBatiment());
        if (!batiments.isEmpty()) cbxBatiment.getSelectionModel().selectFirst();
    }

    /**
     * Construit et retourne le Releve saisi. Retourne null si la validation échoue.
     */
    public Releve construire() {
        Batiment batiment = cbxBatiment.getValue();
        LocalDate date    = dpDate.getValue();
        TypeEnergie type  = cbxTypeEnergie.getValue();

        if (batiment == null || date == null || type == null) return null;

        LocalTime heure;
        try {
            heure = LocalTime.parse(txtHeure.getText().trim());
        } catch (DateTimeParseException e) {
            return null; // format HH:mm invalide
        }

        double quantite, cout;
        try {
            quantite = Double.parseDouble(txtQuantite.getText().trim().replace(",", "."));
            cout     = Double.parseDouble(txtCout.getText().trim().replace(",", "."));
            if (quantite < 0 || cout < 0) return null;
        } catch (NumberFormatException e) {
            return null;
        }

        Releve r = new Releve(LocalDateTime.of(date, heure), type, quantite, cout);
        r.setBatimentId(batiment.getId());
        return r;
    }

    private ListCell<Batiment> celleBatiment() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Batiment b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null : b.getNom());
            }
        };
    }
}
