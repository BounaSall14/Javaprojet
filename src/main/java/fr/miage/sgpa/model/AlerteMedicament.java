package fr.miage.sgpa.model;

import java.time.LocalDate;

/**
 * DTO d'alerte — représente un médicament en alerte stock ou péremption.
 */
public class AlerteMedicament {

    public enum TypeAlerte { STOCK, PEREMPTION }

    private String    nomCommercial;
    private int       stock;
    private int       seuilMin;
    private LocalDate datePeremption;
    private TypeAlerte typeAlerte;

    public AlerteMedicament() {}

    public AlerteMedicament(String nomCommercial, int stock, int seuilMin,
                            LocalDate datePeremption, TypeAlerte typeAlerte) {
        this.nomCommercial  = nomCommercial;
        this.stock          = stock;
        this.seuilMin       = seuilMin;
        this.datePeremption = datePeremption;
        this.typeAlerte     = typeAlerte;
    }

    // ── Getters / Setters ──────────────────────────────────
    public String     getNomCommercial()  { return nomCommercial; }
    public void       setNomCommercial(String v) { this.nomCommercial = v; }

    public int        getStock()          { return stock; }
    public void       setStock(int v)     { this.stock = v; }

    public int        getSeuilMin()       { return seuilMin; }
    public void       setSeuilMin(int v)  { this.seuilMin = v; }

    public LocalDate  getDatePeremption()       { return datePeremption; }
    public void       setDatePeremption(LocalDate v) { this.datePeremption = v; }

    public TypeAlerte getTypeAlerte()           { return typeAlerte; }
    public void       setTypeAlerte(TypeAlerte v) { this.typeAlerte = v; }
}
