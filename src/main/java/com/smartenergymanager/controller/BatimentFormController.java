package com.smartenergymanager.controller;

import com.smartenergymanager.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

/**
 * Contrôleur du formulaire de création / édition d'un bâtiment.
 * Utilisé dans une Dialog ouverte par BatimentsController.
 */
public class BatimentFormController {

    // ── Champs communs ────────────────────────────────────────────────────────
    @FXML private ComboBox<String> cbxType;
    @FXML private TextField        txtNom;
    @FXML private TextField        txtAdresse;
    @FXML private TextField        txtSurface;

    // ── Sections spécifiques ──────────────────────────────────────────────────
    @FXML private Separator  separateurSpecifique;

    @FXML private GridPane   sectionMaison;
    @FXML private Spinner<Integer> spnNbPieces;

    @FXML private GridPane   sectionAppartement;
    @FXML private Spinner<Integer> spnEtage;
    @FXML private TextField  txtNumAppartement;

    @FXML private GridPane   sectionBureau;
    @FXML private TextField  txtEntreprise;
    @FXML private Spinner<Integer> spnNbPostes;

    @FXML private GridPane   sectionUniversitaire;
    @FXML private TextField  txtCampus;

    @FXML
    public void initialize() {
        cbxType.setItems(FXCollections.observableArrayList(
                "Maison", "Appartement", "Bureau", "BatimentUniversitaire"));

        // Initialisation des Spinners
        spnNbPieces.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        spnEtage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1));
        spnNbPostes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 10));

        // Listener : affiche la section du type sélectionné
        cbxType.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancien, nouveau) -> afficherSectionSpecifique(nouveau));
    }

    /**
     * Construit et retourne le Batiment à partir des valeurs saisies.
     * Retourne null si la validation échoue.
     */
    public Batiment construire() {
        String type    = cbxType.getValue();
        String nom     = txtNom.getText().trim();
        String adresse = txtAdresse.getText().trim();

        if (type == null || nom.isEmpty() || adresse.isEmpty()) return null;

        double surface;
        try {
            surface = Double.parseDouble(txtSurface.getText().trim().replace(",", "."));
            if (surface <= 0) return null;
        } catch (NumberFormatException e) {
            return null;
        }

        return switch (type) {
            case "Maison" ->
                new Maison(nom, adresse, surface, spnNbPieces.getValue());
            case "Appartement" ->
                new Appartement(nom, adresse, surface,
                        spnEtage.getValue(), txtNumAppartement.getText().trim());
            case "Bureau" ->
                new Bureau(nom, adresse, surface,
                        txtEntreprise.getText().trim(), spnNbPostes.getValue());
            case "BatimentUniversitaire" ->
                new BatimentUniversitaire(nom, adresse, surface, txtCampus.getText().trim());
            default -> null;
        };
    }

    /**
     * Pré-remplit le formulaire pour le mode édition.
     * @param b Le bâtiment existant à modifier.
     */
    public void remplir(Batiment b) {
        cbxType.setValue(b.getClass().getSimpleName());
        txtNom.setText(b.getNom());
        txtAdresse.setText(b.getAdresse());
        txtSurface.setText(String.valueOf(b.getSurface()));

        switch (b) {
            case Maison m ->
                spnNbPieces.getValueFactory().setValue(m.getNbPieces());
            case Appartement a -> {
                spnEtage.getValueFactory().setValue(a.getEtage());
                txtNumAppartement.setText(a.getNumAppartement());
            }
            case Bureau bu -> {
                txtEntreprise.setText(bu.getEntreprise());
                spnNbPostes.getValueFactory().setValue(bu.getNbPostes());
            }
            case BatimentUniversitaire u ->
                txtCampus.setText(u.getCampus());
            default -> {}
        }
    }

    // ── Helper : show/hide sections ───────────────────────────────────────────

    private void afficherSectionSpecifique(String type) {
        // Cache tout
        setVisible(sectionMaison, false);
        setVisible(sectionAppartement, false);
        setVisible(sectionBureau, false);
        setVisible(sectionUniversitaire, false);
        setVisible(separateurSpecifique, false);

        if (type == null) return;

        setVisible(separateurSpecifique, true);
        switch (type) {
            case "Maison"               -> setVisible(sectionMaison, true);
            case "Appartement"          -> setVisible(sectionAppartement, true);
            case "Bureau"               -> setVisible(sectionBureau, true);
            case "BatimentUniversitaire"-> setVisible(sectionUniversitaire, true);
        }
    }

    // visible + managed doivent être synchronisés pour ne pas laisser d'espace vide
    private void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
