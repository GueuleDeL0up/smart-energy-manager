package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.repository.SQLiteBatimentRepository;
import com.smartenergymanager.service.BatimentService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

/**
 * Contrôleur de la vue BatimentsList.fxml.
 * Gère l'affichage et les actions CRUD sur les bâtiments.
 */
public class BatimentsController {

    @FXML private TableView<Batiment>           tblBatiments;
    @FXML private TableColumn<Batiment, String> colType;
    @FXML private TableColumn<Batiment, String> colNom;
    @FXML private TableColumn<Batiment, String> colAdresse;
    @FXML private TableColumn<Batiment, Double> colSurface;
    @FXML private Label                         lblStatut;

    private final BatimentService service =
            new BatimentService(new SQLiteBatimentRepository());

    private final ObservableList<Batiment> batiments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colType.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getClass().getSimpleName()));
        colNom.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNom()));
        colAdresse.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getAdresse()));
        colSurface.setCellValueFactory(
                data -> new SimpleDoubleProperty(data.getValue().getSurface()).asObject());

        tblBatiments.setItems(batiments);
        chargerBatiments();
    }

    // ── Handlers boutons ──────────────────────────────────────────────────────

    @FXML
    private void creerBatiment() {
        ouvrirFormulaire(null).ifPresent(b -> {
            service.creerBatiment(b);
            batiments.add(b);
            mettreAJourStatut();
        });
    }

    @FXML
    private void modifierBatiment() {
        Batiment selectionne = tblBatiments.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        ouvrirFormulaire(selectionne).ifPresent(b -> {
            b.setId(selectionne.getId()); // transfère l'ID pour l'UPDATE en BDD
            service.modifierBatiment(b);
            chargerBatiments();
        });
    }

    @FXML
    private void supprimerBatiment() {
        Batiment selectionne = tblBatiments.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        service.supprimerBatiment(selectionne.getId());
        batiments.remove(selectionne);
        mettreAJourStatut();
    }

    @FXML
    private void clonerBatiment() {
        Batiment selectionne = tblBatiments.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        Batiment clone = selectionne.cloner();
        clone.setNom(clone.getNom() + " (copie)");
        clone.setId(null);
        service.creerBatiment(clone); // INSERT en BDD → assigne l'ID au clone
        batiments.add(clone);
        mettreAJourStatut();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Ouvre BatimentForm dans une Dialog.
     * @param batiment null → mode création, non-null → mode édition (pré-rempli)
     * @return le Batiment construit si l'utilisateur valide, sinon Optional.empty()
     */
    private Optional<Batiment> ouvrirFormulaire(Batiment batiment) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/smartenergymanager/view/BatimentForm.fxml"));
            VBox contenu = loader.load();
            BatimentFormController formCtrl = loader.getController();
            if (batiment != null) formCtrl.remplir(batiment);

            Dialog<Batiment> dialog = new Dialog<>();
            dialog.setTitle(batiment == null ? "Nouveau bâtiment" : "Modifier le bâtiment");
            dialog.setResizable(true);
            dialog.getDialogPane().setContent(contenu);

            ButtonType btnValider = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

            dialog.setResultConverter(btn ->
                    btn == btnValider ? formCtrl.construire() : null);

            return dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void chargerBatiments() {
        batiments.setAll(service.findAll());
        mettreAJourStatut();
    }

    private void mettreAJourStatut() {
        lblStatut.setText(batiments.size() + " batiment(s) enregistre(s)");
    }
}
