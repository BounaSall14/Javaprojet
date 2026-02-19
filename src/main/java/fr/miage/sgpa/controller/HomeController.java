package fr.miage.sgpa.controller;

import fr.miage.sgpa.service.AlerteService;
import fr.miage.sgpa.service.MedicamentService;
import fr.miage.sgpa.service.VenteService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @FXML private Label statMedicamentsLabel;
    @FXML private Label statVentesLabel;
    @FXML private Label statStockLabel;
    @FXML private Label statPeremptionLabel;

    private final MedicamentService medicamentService = new MedicamentService();
    private final VenteService venteService = new VenteService();
    private final AlerteService alerteService = new AlerteService();

    @FXML
    public void initialize() {
        try {
            int nbMedicaments = medicamentService.getAllMedicaments().size();
            statMedicamentsLabel.setText(String.valueOf(nbMedicaments));
        } catch (Exception e) {
            logger.error("Erreur chargement médicaments", e);
            statMedicamentsLabel.setText("—");
        }

        try {
            int nbVentes = venteService.findAll().size();
            statVentesLabel.setText(String.valueOf(nbVentes));
        } catch (Exception e) {
            logger.error("Erreur chargement ventes", e);
            statVentesLabel.setText("—");
        }

        try {
            int nbStockBas = alerteService.getAlertesStock().size();
            statStockLabel.setText(String.valueOf(nbStockBas));
        } catch (Exception e) {
            logger.error("Erreur chargement alertes stock", e);
            statStockLabel.setText("—");
        }

        try {
            int nbPeremption = alerteService.getAlertesPeremption().size();
            statPeremptionLabel.setText(String.valueOf(nbPeremption));
        } catch (Exception e) {
            logger.error("Erreur chargement alertes péremption", e);
            statPeremptionLabel.setText("—");
        }
    }
}
