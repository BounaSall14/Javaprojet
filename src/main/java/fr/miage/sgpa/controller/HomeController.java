package fr.miage.sgpa.controller;

import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.service.AlerteService;
import fr.miage.sgpa.service.MedicamentService;
import fr.miage.sgpa.service.VenteService;
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

public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    // ── KPI labels (fx:id existants) ──────────────────────────────────────
    @FXML
    private Label statMedicamentsLabel;
    @FXML
    private Label statVentesLabel;
    @FXML
    private Label statStockLabel;
    @FXML
    private Label statPeremptionLabel;

    // ── Nouveaux éléments : section médicaments périmés ───────────────────
    @FXML
    private Label perimesCountLabel;
    @FXML
    private TableView<Medicament> perimesTable;
    @FXML
    private TableColumn<Medicament, String> perimesNomCol;
    @FXML
    private TableColumn<Medicament, LocalDate> perimesDateCol;
    @FXML
    private TableColumn<Medicament, Integer> perimesStockCol;

    private final MedicamentService medicamentService = new MedicamentService();
    private final VenteService venteService = new VenteService();
    private final AlerteService alerteService = new AlerteService();

    @FXML
    public void initialize() {
        // ── KPIs ─────────────────────────────────────────────────────────
        safeSet(() -> statMedicamentsLabel.setText(
                String.valueOf(medicamentService.getAllMedicaments().size())),
                statMedicamentsLabel, "erreur chargement medicaments");

        safeSet(() -> statVentesLabel.setText(
                String.valueOf(venteService.findAll().size())),
                statVentesLabel, "erreur chargement ventes");

        safeSet(() -> statStockLabel.setText(
                String.valueOf(alerteService.getAlertesStock().size())),
                statStockLabel, "erreur chargement stock");

        safeSet(() -> statPeremptionLabel.setText(
                String.valueOf(alerteService.getAlertesPeremption().size())),
                statPeremptionLabel, "erreur chargement peremption");

        // ── Section médicaments périmés ───────────────────────────────────
        try {
            perimesNomCol.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
            perimesDateCol.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));
            perimesStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

            List<Medicament> perimes = alerteService.getMedicamentsPerimes();
            perimesTable.setItems(FXCollections.observableArrayList(perimes));
            perimesCountLabel.setText(perimes.size() + " medicament(s) perime(s) a retirer du stock");
        } catch (Exception e) {
            logger.error("Erreur chargement medicaments perimes", e);
            perimesCountLabel.setText("Erreur de chargement");
        }
    }

    /**
     * Helper pour éviter la répétition try/catch ─ le label affiche "—" en cas
     * d'erreur
     */
    private void safeSet(Runnable action, Label label, String msg) {
        try {
            action.run();
        } catch (Exception e) {
            logger.error(msg, e);
            label.setText("—");
        }
    }
}
