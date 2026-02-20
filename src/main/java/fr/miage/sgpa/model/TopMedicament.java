package fr.miage.sgpa.model;

import java.math.BigDecimal;

/**
 * DTO — Top médicament vendu sur une période.
 * Calculé via agrégation SQL sur vente_lignes JOIN medicaments.
 */
public class TopMedicament {

    private String     nomCommercial;
    private int        quantiteTotale;
    private BigDecimal caGenere;

    public TopMedicament() {}

    public TopMedicament(String nomCommercial, int quantiteTotale, BigDecimal caGenere) {
        this.nomCommercial  = nomCommercial;
        this.quantiteTotale = quantiteTotale;
        this.caGenere       = caGenere;
    }

    public String     getNomCommercial()              { return nomCommercial; }
    public void       setNomCommercial(String v)      { this.nomCommercial = v; }
    public int        getQuantiteTotale()              { return quantiteTotale; }
    public void       setQuantiteTotale(int v)         { this.quantiteTotale = v; }
    public BigDecimal getCaGenere()                    { return caGenere; }
    public void       setCaGenere(BigDecimal v)        { this.caGenere = v; }
}
