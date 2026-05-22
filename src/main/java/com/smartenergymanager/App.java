package com.smartenergymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX.
 * Responsable du lancement de la fenêtre et du chargement de la vue.
 */
public class App extends Application {

    /**
     * Configure et affiche la fenêtre principale.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartenergymanager/view/MainView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Smart Energy Manager");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    /** Point d'entrée standard Java. */
    public static void main(String[] args) {
        launch(args);
    }
}
