package fr.miage.sgpa.controller;

import fr.miage.sgpa.app.MainApp;
import fr.miage.sgpa.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label welcomeLabel;
    @FXML private Button usersButton;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Connecté en tant que : " + AuthService.getCurrentUser().getUsername() + " (" + AuthService.getCurrentUser().getRole() + ")");

        // Restriction admin
        if (!AuthService.isAdmin()) {
            usersButton.setVisible(false);
            usersButton.setManaged(false);
        }
    }

    @FXML private void showHome() { MainApp.loadView("Home.fxml", contentArea); }
    @FXML private void showMedicaments() { MainApp.loadView("Medicament.fxml", contentArea); }
    @FXML private void showFournisseurs() { MainApp.loadView("Fournisseur.fxml", contentArea); }
    @FXML private void showVentes() { MainApp.loadView("Vente.fxml", contentArea); }
    @FXML private void showCommandes() { MainApp.loadView("Commande.fxml", contentArea); }
    @FXML private void showAlertes() { MainApp.loadView("Alerte.fxml", contentArea); }
    @FXML private void showRapports() { MainApp.loadView("Rapport.fxml", contentArea); }
    @FXML private void showUsers() { MainApp.loadView("User.fxml", contentArea); }

    @FXML
    private void handleLogout() {
        AuthService.logout();
        MainApp.showLogin();
    }
}
