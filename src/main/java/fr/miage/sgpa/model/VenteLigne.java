package fr.miage.sgpa.model;

import java.math.BigDecimal;

public class VenteLigne {
    private int venteId;
    private int medicamentId;
    private String nomMedicament; // for display
    private int quantite;
    private BigDecimal prixUnitaire;

    public VenteLigne() {}

    // Getters and Setters
    public int getVenteId() { return venteId; }
    public void setVenteId(int venteId) { this.venteId = venteId; }
    public int getMedicamentId() { return medicamentId; }
    public void setMedicamentId(int medicamentId) { this.medicamentId = medicamentId; }
    public String getNomMedicament() { return nomMedicament; }
    public void setNomMedicament(String nomMedicament) { this.nomMedicament = nomMedicament; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}
