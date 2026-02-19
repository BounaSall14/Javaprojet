package fr.miage.sgpa.controller;

import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;
import fr.miage.sgpa.service.MedicamentService;
import fr.miage.sgpa.service.VenteService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VenteController {

    // ── Onglet Nouvelle Vente ──────────────────────────────
    @FXML private ComboBox<Medicament> medicamentCombo;
    @FXML private TextField quantiteField;
    @FXML private TableView<VenteLigne> cartTable;
    @FXML private TableColumn<VenteLigne, String>     itemNomCol;
    @FXML private TableColumn<VenteLigne, Integer>    itemQuantiteCol;
    @FXML private TableColumn<VenteLigne, BigDecimal> itemPrixCol;
    @FXML private TableColumn<VenteLigne, BigDecimal> itemTotalCol;
    @FXML private Label totalLabel;
    @FXML private CheckBox ordonnanceCheck;

    // ── Onglet Historique ──────────────────────────────────
    @FXML private TableView<Vente>         historyTable;
    @FXML private TableColumn<Vente, String>     histDateCol;
    @FXML private TableColumn<Vente, BigDecimal> histTotalCol;
    @FXML private TableColumn<Vente, Boolean>    histOrdoCol;

    // Panel détail des lignes de la vente sélectionnée
    @FXML private TableView<VenteLigne>         lignesTable;
    @FXML private TableColumn<VenteLigne, String>     ligneNomCol;
    @FXML private TableColumn<VenteLigne, Integer>    ligneQtyCol;
    @FXML private TableColumn<VenteLigne, BigDecimal> lignePrixCol;
    @FXML private TableColumn<VenteLigne, BigDecimal> ligneTotalCol;

    private final MedicamentService medicamentService = new MedicamentService();
    private final VenteService venteService = new VenteService();
    private final ObservableList<VenteLigne> cartItems = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Panier
        itemNomCol.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        itemQuantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        itemPrixCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        itemTotalCol.setCellValueFactory(cellData -> {
            VenteLigne l = cellData.getValue();
            return new SimpleObjectProperty<>(l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())));
        });
        cartTable.setItems(cartItems);
        loadMedicaments();

        // Historique — en-têtes
        histDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateHeure() != null
                        ? cellData.getValue().getDateHeure().format(FMT) : ""));
        histTotalCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        histOrdoCol.setCellValueFactory(new PropertyValueFactory<>("surOrdonnance"));

        // Détail lignes
        ligneNomCol.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        ligneQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        lignePrixCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        ligneTotalCol.setCellValueFactory(cellData -> {
            VenteLigne l = cellData.getValue();
            return new SimpleObjectProperty<>(l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())));
        });

        // Sur sélection d'une vente → charger ses lignes
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                List<VenteLigne> lignes = venteService.getLignesByVenteId(selected.getId());
                lignesTable.setItems(FXCollections.observableArrayList(lignes));
            } else {
                lignesTable.setItems(FXCollections.emptyObservableList());
            }
        });
    }

    private void loadMedicaments() {
        try {
            List<Medicament> list = medicamentService.getAllMedicaments();
            medicamentCombo.setItems(FXCollections.observableArrayList(list));
            medicamentCombo.setConverter(new StringConverter<>() {
                @Override public String toString(Medicament m) { return m == null ? "" : m.getNomCommercial(); }
                @Override public Medicament fromString(String s) { return null; }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToCart() {
        Medicament selected = medicamentCombo.getValue();
        if (selected == null) {
            showAlert("Veuillez sélectionner un médicament", Alert.AlertType.WARNING);
            return;
        }
        try {
            int quantite = Integer.parseInt(quantiteField.getText());
            if (quantite > 0) {
                VenteLigne ligne = new VenteLigne();
                ligne.setMedicamentId(selected.getId());
                ligne.setNomMedicament(selected.getNomCommercial());
                ligne.setQuantite(quantite);
                ligne.setPrixUnitaire(selected.getPrixPublic());
                cartItems.add(ligne);
                updateTotal();
            } else {
                showAlert("La quantite doit etre superieure a 0", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            showAlert("Quantite invalide", Alert.AlertType.ERROR);
        }
    }

    private void updateTotal() {
        BigDecimal total = cartItems.stream()
                .map(item -> item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText(String.format("%.2f EUR", total));
    }

    @FXML
    public void loadHistory() {
        try {
            List<Vente> history = venteService.findAll();
            if (historyTable != null) {
                historyTable.setItems(FXCollections.observableArrayList(history));
                lignesTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                .map(item -> item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vente.setMontantTotal(total);
        try {
            venteService.effectuerVente(vente);
            showAlert("Vente effectuee avec succes !", Alert.AlertType.INFORMATION);
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
