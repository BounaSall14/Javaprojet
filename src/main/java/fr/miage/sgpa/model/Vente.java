package fr.miage.sgpa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vente {
    private int id;
    private LocalDateTime dateHeure;
    private boolean surOrdonnance;
    private BigDecimal montantTotal;
    private List<VenteLigne> lignes = new ArrayList<>();

    public Vente() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public boolean isSurOrdonnance() { return surOrdonnance; }
    public void setSurOrdonnance(boolean surOrdonnance) { this.surOrdonnance = surOrdonnance; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public List<VenteLigne> getLignes() { return lignes; }
    public void setLignes(List<VenteLigne> lignes) { this.lignes = lignes; }
}
