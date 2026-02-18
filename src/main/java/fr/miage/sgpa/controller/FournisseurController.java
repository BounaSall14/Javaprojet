package fr.miage.sgpa.controller;

import fr.miage.sgpa.dao.FournisseurDAO;
import fr.miage.sgpa.model.Fournisseur;
import fr.miage.sgpa.service.AuthService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class FournisseurController {
    @FXML private TableView<Fournisseur> fournisseurTable;
    @FXML private TableColumn<Fournisseur, String> nomCol;
    @FXML private TableColumn<Fournisseur, String> contactCol;
    @FXML private TableColumn<Fournisseur, String> adresseCol;

    @FXML private TextField nomField;
    @FXML private TextField contactField;
    @FXML private TextArea adresseArea;
    @FXML private GridPane editForm;

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @FXML
    public void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        loadFournisseurs();

        if (!AuthService.isAdmin()) {
            editForm.setVisible(false);
            editForm.setManaged(false);
        }

        fournisseurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNom());
                contactField.setText(newSelection.getContact());
                adresseArea.setText(newSelection.getAdresse());
            }
        });
    }

    private void loadFournisseurs() {
        fournisseurTable.setItems(FXCollections.observableArrayList(fournisseurDAO.findAll()));
    }

    @FXML
    private void handleAdd() {
        if (nomField.getText().isEmpty()) {
            showAlert("Le nom est obligatoire");
            return;
        }
        Fournisseur f = new Fournisseur();
        f.setNom(nomField.getText());
        f.setContact(contactField.getText());
        f.setAdresse(adresseArea.getText());
        fournisseurDAO.save(f);
        loadFournisseurs();
        clearFields();
    }

    @FXML
    private void handleUpdate() {
        Fournisseur selected = fournisseurTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(nomField.getText());
            selected.setContact(contactField.getText());
            selected.setAdresse(adresseArea.getText());
            fournisseurDAO.update(selected);
            loadFournisseurs();
        }
    }

    @FXML
    private void handleDelete() {
        Fournisseur selected = fournisseurTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            fournisseurDAO.delete(selected.getId());
            loadFournisseurs();
            clearFields();
        }
    }

    private void clearFields() {
        nomField.clear();
        contactField.clear();
        adresseArea.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }
}
