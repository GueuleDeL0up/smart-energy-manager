package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;
import com.smartenergymanager.repository.SQLiteBatimentRepository;
import com.smartenergymanager.repository.SQLiteReleveRepository;
import com.smartenergymanager.service.BatimentService;
import com.smartenergymanager.service.PredictionService;
import com.smartenergymanager.service.ReleveService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur de la vue StatistiquesView.fxml.
 * Agrège les relevés et alimente les graphiques + la prédiction.
 */
public class StatistiquesController {

    @FXML private ComboBox<Batiment>        cbxFiltreBatiment;
    @FXML private CheckBox                  chkPrevision;
    @FXML private Label                     lblPrevision;
    @FXML private Label                     lblTotalConso;

    @FXML private LineChart<String, Number> chartEvolution;
    @FXML private PieChart                  chartRepartition;
    @FXML private BarChart<String, Number>  chartComparatif;

    private final ReleveService    releveService    = new ReleveService(new SQLiteReleveRepository());
    private final BatimentService  batimentService  = new BatimentService(new SQLiteBatimentRepository());
    private final PredictionService predictionService = new PredictionService();

    private Map<Long, String> nomsBatiments;
    private List<Releve>      relevesCourants = List.of();

    private static final DateTimeFormatter FMT_MOIS = DateTimeFormatter.ofPattern("MM/yyyy");

    @FXML
    public void initialize() {
        chargerBatiments();
        rafraichir(null);

        cbxFiltreBatiment.getSelectionModel().selectedItemProperty()
                .addListener((obs, ancien, nouveau) -> rafraichir(nouveau));
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    private void chargerBatiments() {
        List<Batiment> batiments = batimentService.findAll();
        nomsBatiments = batiments.stream()
                .collect(Collectors.toMap(Batiment::getId, Batiment::getNom));

        ObservableList<Batiment> options = FXCollections.observableArrayList();
        options.add(null);
        options.addAll(batiments);
        cbxFiltreBatiment.setItems(options);
        cbxFiltreBatiment.setCellFactory(lv -> celleBatiment());
        cbxFiltreBatiment.setButtonCell(celleBatiment());
        cbxFiltreBatiment.getSelectionModel().selectFirst();
    }

    private void rafraichir(Batiment filtre) {
        relevesCourants = filtre == null
                ? releveService.findAll()
                : releveService.findByBatiment(filtre.getId());

        peuplerChartEvolution(relevesCourants);
        peuplerPieChart(relevesCourants);
        peuplerChartComparatif(relevesCourants);
        mettreAJourKPI(relevesCourants);
    }

    // ── Handler checkbox prévision ────────────────────────────────────────────

    @FXML
    private void togglePrevision() {
        peuplerChartEvolution(relevesCourants);
    }

    // ── Graphiques ────────────────────────────────────────────────────────────

    private void peuplerChartEvolution(List<Releve> releves) {
        // Agrégation mensuelle dans un TreeMap trié
        TreeMap<YearMonth, Double> parMois = releves.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDateHeure()),
                        TreeMap::new,
                        Collectors.summingDouble(Releve::getQuantite)));

        XYChart.Series<String, Number> serieHisto = new XYChart.Series<>();
        serieHisto.setName("Consommation");
        parMois.forEach((mois, val) ->
                serieHisto.getData().add(new XYChart.Data<>(mois.format(FMT_MOIS), val)));

        // Prévision : on relie le dernier mois connu au mois prédit
        if (chkPrevision.isSelected() && !parMois.isEmpty()) {
            double valeurPredite = predictionService.predireProchainMois(releves);

            if (valeurPredite >= 0) {
                YearMonth dernierMois   = parMois.lastKey();
                double    valeurDernier = parMois.get(dernierMois);
                String    nomProchain   = predictionService.nomProchainMois(releves);

                XYChart.Series<String, Number> seriePrevision = new XYChart.Series<>();
                seriePrevision.setName("Prevision");
                // Point de connexion au dernier mois réel
                seriePrevision.getData().add(
                        new XYChart.Data<>(dernierMois.format(FMT_MOIS), valeurDernier));
                // Point prédit
                seriePrevision.getData().add(
                        new XYChart.Data<>(nomProchain, valeurPredite));

                chartEvolution.getData().setAll(List.of(serieHisto, seriePrevision));

                lblPrevision.setText(String.format(
                        "Prevision %s : %.1f unites", nomProchain, valeurPredite));
                lblPrevision.setVisible(true);
                lblPrevision.setManaged(true);
                return;
            }
        }

        chartEvolution.getData().setAll(List.of(serieHisto));
        lblPrevision.setVisible(false);
        lblPrevision.setManaged(false);
    }

    private void peuplerPieChart(List<Releve> releves) {
        Map<TypeEnergie, Double> parType = releves.stream()
                .collect(Collectors.groupingBy(
                        Releve::getType,
                        Collectors.summingDouble(Releve::getQuantite)));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        parType.forEach((type, total) ->
                data.add(new PieChart.Data(type.name(), total)));
        chartRepartition.setData(data);
    }

    private void peuplerChartComparatif(List<Releve> releves) {
        Map<Long, Double> parBatiment = releves.stream()
                .collect(Collectors.groupingBy(
                        Releve::getBatimentId,
                        Collectors.summingDouble(Releve::getQuantite)));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Consommation totale");
        parBatiment.forEach((id, total) ->
                serie.getData().add(new XYChart.Data<>(
                        nomsBatiments.getOrDefault(id, "Batiment #" + id), total)));
        chartComparatif.getData().setAll(List.of(serie));
    }

    private void mettreAJourKPI(List<Releve> releves) {
        double total = releves.stream().mapToDouble(Releve::getQuantite).sum();
        double cout  = releves.stream().mapToDouble(Releve::getCoutEstime).sum();
        lblTotalConso.setText(String.format(
                "Total : %.1f unites  |  Cout : %.2f euro  |  %d releve(s)",
                total, cout, releves.size()));
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
}
