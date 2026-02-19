package fr.miage.sgpa.controller;

import fr.miage.sgpa.dao.UserDAO;
import fr.miage.sgpa.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class UserController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, User.Role> roleCol;
    @FXML private TableColumn<User, LocalDateTime> dateCol;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<User.Role> roleCombo;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        roleCombo.setItems(FXCollections.observableArrayList(User.Role.values()));
        loadUsers();
    }

    private void loadUsers() {
        userTable.setItems(FXCollections.observableArrayList(userDAO.findAll()));
    }

    @FXML
    private void handleAdd() {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setPasswordHash(passwordField.getText());
        user.setRole(roleCombo.getValue());
        userDAO.save(user);
        loadUsers();
    }

    @FXML
    private void handleDelete() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userDAO.delete(selected.getId());
            loadUsers();
        }
    }
}
