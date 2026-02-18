package fr.miage.sgpa.controller;

import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;
import fr.miage.sgpa.service.MedicamentService;
import fr.miage.sgpa.service.VenteService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VenteController {
    @FXML private ComboBox<Medicament> medicamentCombo;
    @FXML private TextField quantiteField;
    @FXML private TableView<VenteLigne> cartTable;
    @FXML private TableColumn<VenteLigne, String> itemNomCol;
    @FXML private TableColumn<VenteLigne, Integer> itemQuantiteCol;
    @FXML private TableColumn<VenteLigne, BigDecimal> itemPrixCol;
    @FXML private TableColumn<VenteLigne, BigDecimal> itemTotalCol;
    @FXML private Label totalLabel;
    @FXML private CheckBox ordonnanceCheck;

    // History fields
    @FXML private TableView<Vente> historyTable;
    @FXML private TableColumn<Vente, LocalDateTime> histDateCol;
    @FXML private TableColumn<Vente, BigDecimal> histTotalCol;
    @FXML private TableColumn<Vente, Boolean> histOrdoCol;

    private final MedicamentService medicamentService = new MedicamentService();
    private final VenteService venteService = new VenteService();
    private final ObservableList<VenteLigne> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        itemNomCol.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        itemQuantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        itemPrixCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        itemTotalCol.setCellValueFactory(cellData -> {
            VenteLigne ligne = cellData.getValue();
            return new SimpleObjectProperty<>(ligne.getPrixUnitaire().multiply(new BigDecimal(ligne.getQuantite())));
        });

        cartTable.setItems(cartItems);
        loadMedicaments();

        histDateCol.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        histTotalCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        histOrdoCol.setCellValueFactory(new PropertyValueFactory<>("surOrdonnance"));
    }

    private void loadMedicaments() {
        medicamentCombo.setItems(FXCollections.observableArrayList(medicamentService.getAllMedicaments()));
        medicamentCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Medicament m) { return m == null ? "" : m.getNomCommercial(); }
            @Override public Medicament fromString(String string) { return null; }
        });
    }

    @FXML
    private void addToCart() {
        Medicament selected = medicamentCombo.getValue();
        if (selected == null) {
            showAlert("Veuillez sélectionner un médicament", Alert.AlertType.WARNING);
            return;
        }
        int quantite;
        try {
            quantite = Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException e) {
            showAlert("Quantité invalide", Alert.AlertType.ERROR);
            return;
        }

        if (quantite > 0) {
            VenteLigne ligne = new VenteLigne();
            ligne.setMedicamentId(selected.getId());
            ligne.setNomMedicament(selected.getNomCommercial());
            ligne.setQuantite(quantite);
            ligne.setPrixUnitaire(selected.getPrixPublic());
            cartItems.add(ligne);
            updateTotal();
        }
    }

    private void updateTotal() {
        BigDecimal total = cartItems.stream()
                .map(item -> item.getPrixUnitaire().multiply(new BigDecimal(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText(String.format("%.2f €", total));
    }

    @FXML
    public void loadHistory() {
        historyTable.setItems(FXCollections.observableArrayList(venteService.getAllVentes()));
    }

    @FXML
    private void finalizeSale() {
        if (cartItems.isEmpty()) {
            showAlert("Le panier est vide", Alert.AlertType.WARNING);
            return;
        }

        Vente vente = new Vente();
        vente.setSurOrdonnance(ordonnanceCheck.isSelected());
        vente.getLignes().addAll(cartItems);

        BigDecimal total = cartItems.stream()
                .map(item -> item.getPrixUnitaire().multiply(new BigDecimal(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vente.setMontantTotal(total);

        try {
            venteService.effectuerVente(vente);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vente effectuée avec succès !");
            alert.showAndWait();
            cartItems.clear();
            updateTotal();
        } catch (Exception e) {
            showAlert("Erreur lors de la vente : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}
