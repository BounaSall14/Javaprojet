package fr.miage.sgpa.model;

import java.math.BigDecimal;

/**
 * DTO représentant le rapport financier d'un mois.
 * Mois au format "YYYY-MM" (ex: "2026-01").
 */
public class RapportMensuel {

    private String     mois;         // "YYYY-MM"
    private int        nbVentes;
    private BigDecimal totalEuros;

    public RapportMensuel() {}

    public RapportMensuel(String mois, int nbVentes, BigDecimal totalEuros) {
        this.mois       = mois;
        this.nbVentes   = nbVentes;
        this.totalEuros = totalEuros;
    }

    // ── Getters / Setters ──────────────────────────────────
    public String     getMois()                 { return mois; }
    public void       setMois(String v)         { this.mois = v; }

    public int        getNbVentes()             { return nbVentes; }
    public void       setNbVentes(int v)        { this.nbVentes = v; }

    public BigDecimal getTotalEuros()           { return totalEuros; }
    public void       setTotalEuros(BigDecimal v){ this.totalEuros = v; }
}
