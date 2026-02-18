package fr.miage.sgpa.controller;

import fr.miage.sgpa.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import fr.miage.sgpa.app.MainApp;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authService.login(username, password)) {
            MainApp.showDashboard();
        } else {
            errorLabel.setText("Identifiants incorrects");
        }
    }
}
