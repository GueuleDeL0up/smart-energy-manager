package com.smartenergymanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur principal de l'interface graphique.
 * Gère la navigation entre les vues chargées dans contentArea.
 */
public class MainController {

    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnBatiments;
    @FXML private Button btnConsommations;
    @FXML private Button btnStatistiques;

    private List<Button> navButtons;

    @FXML
    public void initialize() {
        navButtons = List.of(btnDashboard, btnBatiments, btnConsommations, btnStatistiques);
        naviguerVersDashboard();
    }

    @FXML
    private void naviguerVersDashboard() {
        chargerVue("/com/smartenergymanager/view/DashboardView.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void naviguerVersBatiments() {
        chargerVue("/com/smartenergymanager/view/BatimentsList.fxml");
        setActiveButton(btnBatiments);
    }

    @FXML
    private void naviguerVersConsommations() {
        chargerVue("/com/smartenergymanager/view/ConsommationsList.fxml");
        setActiveButton(btnConsommations);
    }

    @FXML
    private void naviguerVersStatistiques() {
        chargerVue("/com/smartenergymanager/view/StatistiquesView.fxml");
        setActiveButton(btnStatistiques);
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private void chargerVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node vue = loader.load();
            contentArea.getChildren().setAll(vue);
        } catch (IOException e) {
            System.err.println("Impossible de charger la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button actif) {
        navButtons.forEach(b -> b.getStyleClass().remove("nav-button-active"));
        actif.getStyleClass().add("nav-button-active");
    }
}
