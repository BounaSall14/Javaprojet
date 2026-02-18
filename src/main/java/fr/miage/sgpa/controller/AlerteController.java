package fr.miage.sgpa.controller;

import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.service.AlerteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class AlerteController {
    @FXML private TableView<Medicament> stockAlertTable;
    @FXML private TableColumn<Medicament, String> stockNomCol;
    @FXML private TableColumn<Medicament, Integer> stockActuelCol;
    @FXML private TableColumn<Medicament, Integer> stockSeuilCol;

    @FXML private TableView<Medicament> peremptionAlertTable;
    @FXML private TableColumn<Medicament, String> peremptionNomCol;
    @FXML private TableColumn<Medicament, LocalDate> peremptionDateCol;

    private final AlerteService alerteService = new AlerteService();

    @FXML
    public void initialize() {
        stockNomCol.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        stockActuelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockSeuilCol.setCellValueFactory(new PropertyValueFactory<>("seuilMin"));

        peremptionNomCol.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        peremptionDateCol.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));

        loadAlerts();
    }

    private void loadAlerts() {
        stockAlertTable.setItems(FXCollections.observableArrayList(alerteService.getAlertesStock()));
        peremptionAlertTable.setItems(FXCollections.observableArrayList(alerteService.getAlertesPeremption()));
    }
}
