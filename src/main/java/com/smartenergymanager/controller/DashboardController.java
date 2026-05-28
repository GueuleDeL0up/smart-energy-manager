package com.smartenergymanager.controller;

import com.smartenergymanager.model.Batiment;
import com.smartenergymanager.model.Releve;
import com.smartenergymanager.repository.SQLiteBatimentRepository;
import com.smartenergymanager.repository.SQLiteReleveRepository;
import com.smartenergymanager.service.BatimentService;
import com.smartenergymanager.service.ReleveService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur du tableau de bord.
 * Calcule les KPI à partir des relevés en base et alimente la vue.
 */
public class DashboardController {

    @FXML private Label           lblDate;
    @FXML private Label           lblConsoJour;
    @FXML private Label           lblConsoMois;
    @FXML private Label           lblCoutMois;
    @FXML private Label           lblTopBatiment;
    @FXML private Label           lblTopConso;
    @FXML private ListView<String> lstDerniersReleves;

    private final ReleveService   releveService   = new ReleveService(new SQLiteReleveRepository());
    private final BatimentService batimentService = new BatimentService(new SQLiteBatimentRepository());

    private static final DateTimeFormatter FMT_DATE   =
            DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
    private static final DateTimeFormatter FMT_RELEVE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        lblDate.setText(LocalDate.now().format(FMT_DATE));

        List<Releve> tous = releveService.findAll();
        Map<Long, String> nomsBatiments = batimentService.findAll().stream()
                .collect(Collectors.toMap(Batiment::getId, Batiment::getNom));

        calculerKPI(tous, nomsBatiments);
        chargerDerniersReleves(tous, nomsBatiments);
    }

    // ── KPI ───────────────────────────────────────────────────────────────────

    private void calculerKPI(List<Releve> tous, Map<Long, String> nomsBatiments) {
        LocalDate aujourd_hui = LocalDate.now();
        LocalDate debutMois   = aujourd_hui.withDayOfMonth(1);

        double consoJour = tous.stream()
                .filter(r -> r.getDateHeure().toLocalDate().equals(aujourd_hui))
                .mapToDouble(Releve::getQuantite).sum();

        double consoMois = tous.stream()
                .filter(r -> !r.getDateHeure().toLocalDate().isBefore(debutMois))
                .mapToDouble(Releve::getQuantite).sum();

        double coutMois = tous.stream()
                .filter(r -> !r.getDateHeure().toLocalDate().isBefore(debutMois))
                .mapToDouble(Releve::getCoutEstime).sum();

        lblConsoJour.setText(String.format("%.1f", consoJour));
        lblConsoMois.setText(String.format("%.1f", consoMois));
        lblCoutMois.setText(String.format("%.2f", coutMois));

        // Bâtiment le plus consommateur (toutes périodes confondues)
        tous.stream()
                .collect(Collectors.groupingBy(
                        Releve::getBatimentId,
                        Collectors.summingDouble(Releve::getQuantite)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresentOrElse(
                        e -> {
                            lblTopBatiment.setText(nomsBatiments.getOrDefault(e.getKey(), "?"));
                            lblTopConso.setText(String.format("%.1f unites au total", e.getValue()));
                        },
                        () -> lblTopBatiment.setText("Aucune donnee")
                );
    }

    // ── Derniers relevés ─────────────────────────────────────────────────────

    private void chargerDerniersReleves(List<Releve> tous, Map<Long, String> nomsBatiments) {
        ObservableList<String> lignes = tous.stream()
                .limit(15)
                .map(r -> String.format("%-17s  %-22s  %-20s  %.1f u.  %.2f €",
                        r.getDateHeure().format(FMT_RELEVE),
                        nomsBatiments.getOrDefault(r.getBatimentId(), "?"),
                        r.getType().name(),
                        r.getQuantite(),
                        r.getCoutEstime()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        lstDerniersReleves.setItems(lignes);

        if (lignes.isEmpty()) {
            lstDerniersReleves.setPlaceholder(
                    new Label("Aucun releve. Ajoutez-en dans la section Consommations."));
        }
    }
}
