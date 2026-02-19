package fr.miage.sgpa.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {
    private static Stage primaryStage;

    /** Chemin vers la feuille de style globale */
    private static final String CSS_PATH = "/fr/miage/sgpa/view/style.css";

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("SGPA Pharmacie");
        showLogin();
    }

    /** Charge le CSS et l'applique à une scène */
    private static void applyCSS(Scene scene) {
        URL css = MainApp.class.getResource(CSS_PATH);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    public static void showLogin() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fr/miage/sgpa/view/Login.fxml"));
            Scene scene = new Scene(root);
            applyCSS(scene);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDashboard() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fr/miage/sgpa/view/Dashboard.fxml"));
            Scene scene = new Scene(root);
            applyCSS(scene);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadView(String fxml, Pane container) {
        try {
            Parent view = FXMLLoader.load(MainApp.class.getResource("/fr/miage/sgpa/view/" + fxml));
            container.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
