package fr.miage.sgpa.controller;

import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.service.AuthService;
import fr.miage.sgpa.service.MedicamentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicamentController {
    @FXML private TableView<Medicament> medicamentTable;
    @FXML private TableColumn<Medicament, String> nomCol;
    @FXML private TableColumn<Medicament, String> principeCol;
    @FXML private TableColumn<Medicament, BigDecimal> prixCol;
    @FXML private TableColumn<Medicament, Integer> stockCol;
    @FXML private TableColumn<Medicament, LocalDate> peremptionCol;

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private TextField stockField;
    @FXML private TextField seuilField;
    @FXML private DatePicker peremptionPicker;
    @FXML private GridPane editForm;

    private final MedicamentService medicamentService = new MedicamentService();

    @FXML
    public void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        principeCol.setCellValueFactory(new PropertyValueFactory<>("principeActif"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        peremptionCol.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));

        loadMedicaments();

        if (!AuthService.isAdmin()) {
            editForm.setVisible(false);
            editForm.setManaged(false);
        }

        medicamentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNomCommercial());
                prixField.setText(newSelection.getPrixPublic().toString());
                stockField.setText(String.valueOf(newSelection.getStock()));
                seuilField.setText(String.valueOf(newSelection.getSeuilMin()));
                peremptionPicker.setValue(newSelection.getDatePeremption());
            }
        });
    }

    private void loadMedicaments() {
        medicamentTable.setItems(FXCollections.observableArrayList(medicamentService.getAllMedicaments()));
    }

    @FXML
    private void handleAdd() {
        try {
            Medicament m = new Medicament();
            m.setNomCommercial(nomField.getText());
            m.setPrixPublic(new BigDecimal(prixField.getText()));
            m.setStock(Integer.parseInt(stockField.getText()));
            m.setSeuilMin(Integer.parseInt(seuilField.getText()));
            m.setDatePeremption(peremptionPicker.getValue());
            medicamentService.addMedicament(m);
            loadMedicaments();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur : Vérifiez la validité des champs.");
        }
    }

    @FXML
    private void handleUpdate() {
        Medicament selected = medicamentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setNomCommercial(nomField.getText());
                selected.setPrixPublic(new BigDecimal(prixField.getText()));
                selected.setStock(Integer.parseInt(stockField.getText()));
                selected.setSeuilMin(Integer.parseInt(seuilField.getText()));
                selected.setDatePeremption(peremptionPicker.getValue());
                medicamentService.updateMedicament(selected);
                loadMedicaments();
            } catch (Exception e) {
                showAlert("Erreur : Vérifiez la validité des champs.");
            }
        }
    }

    private void clearFields() {
        nomField.clear();
        prixField.clear();
        stockField.clear();
        seuilField.clear();
        peremptionPicker.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    @FXML
    private void handleDelete() {
        Medicament selected = medicamentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            medicamentService.deleteMedicament(selected.getId());
            loadMedicaments();
        }
    }
}
