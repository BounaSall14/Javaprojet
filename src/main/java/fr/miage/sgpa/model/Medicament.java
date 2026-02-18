package fr.miage.sgpa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Medicament {
    private int id;
    private String nomCommercial;
    private String principeActif;
    private String formeGalenique;
    private String dosage;
    private BigDecimal prixPublic;
    private boolean necessiteOrdonnance;
    private LocalDate datePeremption;
    private int stock;
    private int seuilMin;

    public Medicament() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomCommercial() { return nomCommercial; }
    public void setNomCommercial(String nomCommercial) { this.nomCommercial = nomCommercial; }
    public String getPrincipeActif() { return principeActif; }
    public void setPrincipeActif(String principeActif) { this.principeActif = principeActif; }
    public String getFormeGalenique() { return formeGalenique; }
    public void setFormeGalenique(String formeGalenique) { this.formeGalenique = formeGalenique; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public BigDecimal getPrixPublic() { return prixPublic; }
    public void setPrixPublic(BigDecimal prixPublic) { this.prixPublic = prixPublic; }
    public boolean isNecessiteOrdonnance() { return necessiteOrdonnance; }
    public void setNecessiteOrdonnance(boolean necessiteOrdonnance) { this.necessiteOrdonnance = necessiteOrdonnance; }
    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getSeuilMin() { return seuilMin; }
    public void setSeuilMin(int seuilMin) { this.seuilMin = seuilMin; }
}
