package fr.miage.sgpa.controller;

import fr.miage.sgpa.service.AuthService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import fr.miage.sgpa.app.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator progressIndicator;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // UI Feedback
        loginButton.setDisable(true);
        progressIndicator.setVisible(true);
        errorLabel.setText("");

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                logger.info("Attempting login for user: {}", username);
                return authService.login(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            progressIndicator.setVisible(false);
            loginButton.setDisable(false);
            if (loginTask.getValue()) {
                logger.info("Login successful for user: {}", username);
                MainApp.showDashboard();
            } else {
                logger.warn("Login failed for user: {}", username);
                errorLabel.setText("Identifiants incorrects ou utilisateur inexistant.");
            }
        });

        loginTask.setOnFailed(e -> {
            progressIndicator.setVisible(false);
            loginButton.setDisable(false);
            Throwable ex = loginTask.getException();
            logger.error("Login attempt failed with error: {}", ex.getMessage(), ex);
            errorLabel.setText("Erreur de connexion : " + ex.getMessage());
        });

        new Thread(loginTask).start();
    }
}
