package fr.miage.sgpa.controller;

import fr.miage.sgpa.model.AlerteMedicament;
import fr.miage.sgpa.service.AlerteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur de la vue Alertes.
 * Affiche une table unifiée : stock bas + péremption proche.
 */
public class AlerteController {

    private static final Logger logger = LoggerFactory.getLogger(AlerteController.class);

    @FXML private Label totalLabel;

    @FXML private TableView<AlerteMedicament>                     alerteTable;
    @FXML private TableColumn<AlerteMedicament, String>           alerteNomCol;
    @FXML private TableColumn<AlerteMedicament, Integer>          alerteStockCol;
    @FXML private TableColumn<AlerteMedicament, Integer>          alerteSeuilCol;
    @FXML private TableColumn<AlerteMedicament, LocalDate>        alerteDateCol;
    @FXML private TableColumn<AlerteMedicament, AlerteMedicament.TypeAlerte> alerteTypeCol;

    private final AlerteService alerteService = new AlerteService();

    @FXML
    public void initialize() {
        logger.info("Chargement de l'onglet Alertes...");

        alerteNomCol.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        alerteStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        alerteSeuilCol.setCellValueFactory(new PropertyValueFactory<>("seuilMin"));
        alerteDateCol.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));
        alerteTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeAlerte"));

        loadAlertes();
    }

    private void loadAlertes() {
        try {
            List<AlerteMedicament> alertes = alerteService.getAllAlertes();
            alerteTable.setItems(FXCollections.observableArrayList(alertes));
            totalLabel.setText(alertes.size() + " alerte(s) active(s)");
            logger.info("{} alertes chargees", alertes.size());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des alertes", e);
            totalLabel.setText("Erreur de chargement");
        }
    }
}
