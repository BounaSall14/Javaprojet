package fr.miage.sgpa.controller;

import fr.miage.sgpa.dao.FournisseurDAO;
import fr.miage.sgpa.model.Commande;
import fr.miage.sgpa.model.CommandeLigne;
import fr.miage.sgpa.model.Fournisseur;
import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.service.AuthService;
import fr.miage.sgpa.service.CommandeService;
import fr.miage.sgpa.service.MedicamentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDateTime;

public class CommandeController {

    // ── Liste des commandes ────────────────────────────────
    @FXML private TableView<Commande>                commandeTable;
    @FXML private TableColumn<Commande, Integer>     idCol;
    @FXML private TableColumn<Commande, String>      fournisseurCol;
    @FXML private TableColumn<Commande, LocalDateTime> dateCol;
    @FXML private TableColumn<Commande, Commande.Statut> statutCol;
    @FXML private HBox adminControls;
    @FXML private Tab  newOrderTab;

    // ── Détail lignes de la commande sélectionnée ─────────
    @FXML private TableView<CommandeLigne>           lignesTable;
    @FXML private TableColumn<CommandeLigne, String>  ligneNomCol;
    @FXML private TableColumn<CommandeLigne, Integer> ligneQtyCol;

    // ── Nouvelle commande ──────────────────────────────────
    @FXML private ComboBox<Fournisseur>  fournisseurCombo;
    @FXML private ComboBox<Medicament>   medicamentCombo;
    @FXML private TextField              quantiteField;
    @FXML private TableView<CommandeLigne> newLignesTable;
    @FXML private TableColumn<CommandeLigne, String>  newMedCol;
    @FXML private TableColumn<CommandeLigne, Integer> newQtyCol;

    private final CommandeService   commandeService   = new CommandeService();
    private final MedicamentService medicamentService = new MedicamentService();
    private final FournisseurDAO    fournisseurDAO    = new FournisseurDAO();
    private final ObservableList<CommandeLigne> newLignes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Table commandes
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        fournisseurCol.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        loadCommandes();

        // Table lignes détail
        ligneNomCol.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        ligneQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        // Sur sélection d'une commande → charger ses lignes
        commandeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                lignesTable.setItems(FXCollections.observableArrayList(
                        commandeService.getLignesByCommandeId(selected.getId())));
            } else {
                lignesTable.setItems(FXCollections.emptyObservableList());
            }
        });

        // Restrictions vendeur
        if (!AuthService.isAdmin()) {
            adminControls.setVisible(false);
            adminControls.setManaged(false);
            newOrderTab.setDisable(true);
        }

        setupNewOrderForm();
    }

    private void setupNewOrderForm() {
        newMedCol.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        newQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        newLignesTable.setItems(newLignes);

        fournisseurCombo.setItems(FXCollections.observableArrayList(fournisseurDAO.findAll()));
        fournisseurCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Fournisseur f) { return f == null ? "" : f.getNom(); }
            @Override public Fournisseur fromString(String s) { return null; }
        });

        medicamentCombo.setItems(FXCollections.observableArrayList(medicamentService.getAllMedicaments()));
        medicamentCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Medicament m) { return m == null ? "" : m.getNomCommercial(); }
            @Override public Medicament fromString(String s) { return null; }
        });
    }

    @FXML
    private void addToOrder() {
        Medicament selected = medicamentCombo.getValue();
        if (selected != null) {
            try {
                int qty = Integer.parseInt(quantiteField.getText());
                CommandeLigne cl = new CommandeLigne();
                cl.setMedicamentId(selected.getId());
                cl.setNomMedicament(selected.getNomCommercial());
                cl.setQuantite(qty);
                newLignes.add(cl);
            } catch (NumberFormatException e) {
                showAlert("Quantite invalide");
            }
        }
    }

    @FXML
    private void handleCreateOrder() {
        Fournisseur f = fournisseurCombo.getValue();
        if (f == null || newLignes.isEmpty()) {
            showAlert("Veuillez selectionner un fournisseur et au moins un medicament.");
            return;
        }
        Commande c = new Commande();
        c.setFournisseurId(f.getId());
        c.getLignes().addAll(newLignes);
        commandeService.passerCommande(c);
        showAlert("Commande creee avec succes.");
        newLignes.clear();
        loadCommandes();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    private void loadCommandes() {
        commandeTable.setItems(FXCollections.observableArrayList(commandeService.getAllCommandes()));
        if (lignesTable != null) lignesTable.setItems(FXCollections.emptyObservableList());
    }

    @FXML
    private void handleReception() {
        Commande selected = commandeTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatut() == Commande.Statut.EN_ATTENTE) {
            try {
                commandeService.receptionnerCommande(selected.getId());
                loadCommandes();
                new Alert(Alert.AlertType.INFORMATION, "Commande receptionee, stock mis a jour.").showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
            }
        }
    }
}
