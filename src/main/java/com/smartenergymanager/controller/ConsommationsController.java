package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.repository.SQLiteBatimentRepository;
import com.smartenergymanager.repository.SQLiteReleveRepository;
import com.smartenergymanager.service.BatimentService;
import com.smartenergymanager.service.ReleveService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contrôleur de la vue ConsommationsList.fxml.
 */
public class ConsommationsController {

    @FXML private ComboBox<Batiment>          cbxFiltreBatiment;
    @FXML private TableView<Releve>           tblReleves;
    @FXML private TableColumn<Releve, String> colDate;
    @FXML private TableColumn<Releve, String> colHeure;
    @FXML private TableColumn<Releve, String> colBatiment;
    @FXML private TableColumn<Releve, String> colType;
    @FXML private TableColumn<Releve, String> colQuantite;
    @FXML private TableColumn<Releve, String> colCout;
    @FXML private Label                       lblStatut;

    private final ReleveService   releveService   = new ReleveService(new SQLiteReleveRepository());
    private final BatimentService batimentService = new BatimentService(new SQLiteBatimentRepository());

    private final ObservableList<Releve> releves = FXCollections.observableArrayList();
    private final DateTimeFormatter fmtDate  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter fmtHeure = DateTimeFormatter.ofPattern("HH:mm");

    // Cache id → nom du bâtiment pour la colonne Bâtiment
    private Map<Long, String> nomsBatiments;

    @FXML
    public void initialize() {
        chargerBatiments();
        configurerColonnes();
        tblReleves.setItems(releves);
        filtrerReleves(null); // charge tous les relevés au démarrage
    }

    // ── Handlers boutons ──────────────────────────────────────────────────────

    @FXML
    private void ajouterReleve() {
        ouvrirFormulaire().ifPresent(r -> {
            releveService.ajouterReleve(r);
            releves.add(0, r); // ajoute en tête (ordre chronologique inverse)
            mettreAJourStatut();
        });
    }

    @FXML
    private void supprimerReleve() {
        Releve selectionne = tblReleves.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        releveService.supprimerReleve(selectionne.getId());
        releves.remove(selectionne);
        mettreAJourStatut();
    }

    @FXML
    private void importerCSV() {
        // TODO (BLOC 4.7) : ouvrir un FileChooser, lire le CSV, insérer les relevés
        new Alert(Alert.AlertType.INFORMATION,
                "Import CSV non encore implémenté.", ButtonType.OK).showAndWait();
    }

    @FXML
    private void genererDonnees() {
        // TODO (BLOC 4.8) : générer N relevés aléatoires pour chaque bâtiment
        new Alert(Alert.AlertType.INFORMATION,
                "Génération de données test non encore implémentée.", ButtonType.OK).showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void chargerBatiments() {
        List<Batiment> batiments = batimentService.findAll();

        nomsBatiments = batiments.stream()
                .collect(Collectors.toMap(Batiment::getId, Batiment::getNom));

        ObservableList<Batiment> options = FXCollections.observableArrayList();
        options.add(null); // représente "Tous les bâtiments"
        options.addAll(batiments);
        cbxFiltreBatiment.setItems(options);

        // Affichage personnalisé dans le ComboBox
        cbxFiltreBatiment.setCellFactory(lv -> celleBatiment());
        cbxFiltreBatiment.setButtonCell(celleBatiment());
        cbxFiltreBatiment.getSelectionModel().selectFirst();

        cbxFiltreBatiment.getSelectionModel().selectedItemProperty()
                .addListener((obs, ancien, nouveau) -> filtrerReleves(nouveau));
    }

    private ListCell<Batiment> celleBatiment() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Batiment b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty ? null : (b == null ? "Tous les batiments" : b.getNom()));
            }
        };
    }

    private void configurerColonnes() {
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateHeure().format(fmtDate)));
        colHeure.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateHeure().format(fmtHeure)));
        colBatiment.setCellValueFactory(data ->
                new SimpleStringProperty(
                        nomsBatiments.getOrDefault(data.getValue().getBatimentId(), "?")));
        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().name()));
        colQuantite.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().getQuantite())));
        colCout.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().getCoutEstime())));
    }

    private void filtrerReleves(Batiment filtre) {
        if (filtre == null) {
            releves.setAll(releveService.findAll());
        } else {
            releves.setAll(releveService.findByBatiment(filtre.getId()));
        }
        mettreAJourStatut();
    }

    private Optional<Releve> ouvrirFormulaire() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/smartenergymanager/view/ConsommationForm.fxml"));
            VBox contenu = loader.load();
            ConsommationFormController formCtrl = loader.getController();
            formCtrl.remplirBatiments(batimentService.findAll());

            Dialog<Releve> dialog = new Dialog<>();
            dialog.setTitle("Nouveau relevé de consommation");
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

    private void mettreAJourStatut() {
        lblStatut.setText(releves.size() + " releve(s) affiche(s)");
    }
}
