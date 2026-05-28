package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Maison;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Contrôleur de la vue BatimentsList.fxml.
 * Gère l'affichage et les actions CRUD sur les bâtiments.
 */
public class BatimentsController {

    @FXML private TableView<Batiment>             tblBatiments;
    @FXML private TableColumn<Batiment, String>   colType;
    @FXML private TableColumn<Batiment, String>   colNom;
    @FXML private TableColumn<Batiment, String>   colAdresse;
    @FXML private TableColumn<Batiment, Double>   colSurface;
    @FXML private Label                           lblStatut;

    // Liste observable : tout ajout/suppression rafraîchit automatiquement le tableau.
    // TODO (BLOC 1) : alimenter cette liste depuis BatimentService.findAll()
    //                 une fois que SQLiteBatimentRepository sera implémenté.
    private final ObservableList<Batiment> batiments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Liaison colonnes ↔ propriétés du modèle
        colType.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getClass().getSimpleName())
        );
        colNom.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getNom())
        );
        colAdresse.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getAdresse())
        );
        colSurface.setCellValueFactory(
            data -> new SimpleDoubleProperty(data.getValue().getSurface()).asObject()
        );

        tblBatiments.setItems(batiments);

        // Données de test provisoires — à supprimer quand le repository sera prêt
        batiments.add(new Maison("Maison Dupont", "12 rue des Lilas, Lyon", 120.0, 5));
        batiments.add(new Maison("Villa Soleil",  "3 avenue de la Mer, Nice", 200.0, 7));

        mettreAJourStatut();
    }

    // ── Handlers des boutons de la toolbar ────────────────────────────────────

    @FXML
    private void creerBatiment() {
        // TODO (BLOC 2.4) : ouvrir BatimentForm.fxml en mode création dans une Dialog
    }

    @FXML
    private void modifierBatiment() {
        Batiment selectionne = tblBatiments.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        // TODO (BLOC 2.4) : ouvrir BatimentForm.fxml en mode édition, pré-rempli
    }

    @FXML
    private void supprimerBatiment() {
        Batiment selectionne = tblBatiments.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        batiments.remove(selectionne);
        mettreAJourStatut();
        // TODO (BLOC 1) : appeler service.supprimerBatiment(selectionne.getId())
    }

    @FXML
    private void clonerBatiment() {
        Batiment selectionne = tblBatiments.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        Batiment clone = selectionne.cloner();
        clone.setNom(clone.getNom() + " (copie)");
        clone.setId(null);
        batiments.add(clone);
        mettreAJourStatut();
        // TODO (BLOC 1) : appeler service.dupliquerBatiment(selectionne.getId())
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void mettreAJourStatut() {
        lblStatut.setText(batiments.size() + " batiment(s) enregistre(s)");
    }
}
