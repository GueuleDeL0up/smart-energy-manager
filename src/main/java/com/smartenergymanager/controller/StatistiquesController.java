package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.model.TypeEnergie;
import com.smartenergymanager.repository.SQLiteBatimentRepository;
import com.smartenergymanager.repository.SQLiteReleveRepository;
import com.smartenergymanager.service.BatimentService;
import com.smartenergymanager.service.ReleveService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur de la vue StatistiquesView.fxml.
 * Agrège les données de relevés et alimente les trois graphiques JavaFX.
 */
public class StatistiquesController {

    @FXML private ComboBox<Batiment> cbxFiltreBatiment;
    @FXML private Label              lblTotalConso;

    @FXML private LineChart<String, Number>  chartEvolution;
    @FXML private PieChart                   chartRepartition;
    @FXML private BarChart<String, Number>   chartComparatif;

    private final ReleveService   releveService   = new ReleveService(new SQLiteReleveRepository());
    private final BatimentService batimentService = new BatimentService(new SQLiteBatimentRepository());

    private Map<Long, String> nomsBatiments;
    private static final DateTimeFormatter FMT_MOIS = DateTimeFormatter.ofPattern("MM/yyyy");

    @FXML
    public void initialize() {
        chargerBatiments();
        rafraichir(null); // charge tout au démarrage

        cbxFiltreBatiment.getSelectionModel().selectedItemProperty()
                .addListener((obs, ancien, nouveau) -> rafraichir(nouveau));
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    private void chargerBatiments() {
        List<Batiment> batiments = batimentService.findAll();
        nomsBatiments = batiments.stream()
                .collect(Collectors.toMap(Batiment::getId, Batiment::getNom));

        ObservableList<Batiment> options = FXCollections.observableArrayList();
        options.add(null); // "Tous les bâtiments"
        options.addAll(batiments);
        cbxFiltreBatiment.setItems(options);
        cbxFiltreBatiment.setCellFactory(lv -> celleBatiment());
        cbxFiltreBatiment.setButtonCell(celleBatiment());
        cbxFiltreBatiment.getSelectionModel().selectFirst();
    }

    private void rafraichir(Batiment filtre) {
        List<Releve> releves = filtre == null
                ? releveService.findAll()
                : releveService.findByBatiment(filtre.getId());

        peuplerChartEvolution(releves);
        peuplerPieChart(releves);
        peuplerChartComparatif(releves);
        mettreAJourKPI(releves);
    }

    // ── Alimentation des graphiques ───────────────────────────────────────────

    /**
     * LineChart : courbe de la consommation totale par mois (ordre chronologique).
     */
    private void peuplerChartEvolution(List<Releve> releves) {
        Map<String, Double> parMois = releves.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDateHeure().format(FMT_MOIS),
                        Collectors.summingDouble(Releve::getQuantite)));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Consommation");

        parMois.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // ordre MM/yyyy croissant
                .forEach(e -> serie.getData().add(
                        new XYChart.Data<>(e.getKey(), e.getValue())));

        chartEvolution.getData().setAll(List.of(serie));
    }

    /**
     * PieChart : part de chaque type d'énergie dans la consommation totale.
     */
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

    /**
     * BarChart : consommation totale par bâtiment (comparatif).
     */
    private void peuplerChartComparatif(List<Releve> releves) {
        Map<Long, Double> parBatiment = releves.stream()
                .collect(Collectors.groupingBy(
                        Releve::getBatimentId,
                        Collectors.summingDouble(Releve::getQuantite)));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Consommation totale");

        parBatiment.forEach((id, total) -> {
            String nom = nomsBatiments.getOrDefault(id, "Batiment #" + id);
            serie.getData().add(new XYChart.Data<>(nom, total));
        });

        chartComparatif.getData().setAll(List.of(serie));
    }

    // ── KPI ───────────────────────────────────────────────────────────────────

    private void mettreAJourKPI(List<Releve> releves) {
        double total = releves.stream().mapToDouble(Releve::getQuantite).sum();
        double cout  = releves.stream().mapToDouble(Releve::getCoutEstime).sum();
        lblTotalConso.setText(String.format(
                "Total : %.1f unites  |  Cout : %.2f euro  |  %d releve(s)",
                total, cout, releves.size()));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

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
