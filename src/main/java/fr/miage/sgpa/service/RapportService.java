package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.RapportDAO;
import fr.miage.sgpa.dao.VenteDAO;
import fr.miage.sgpa.model.RapportMensuel;
import fr.miage.sgpa.model.TopMedicament;
import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service pour les rapports financiers.
 * Délègue au RapportDAO et VenteDAO.
 */
public class RapportService {

    private final RapportDAO rapportDAO = new RapportDAO();
    private final VenteDAO venteDAO = new VenteDAO();

    // ── Rapport mensuel — toutes périodes (compat existant) ──────────────
    public List<RapportMensuel> getRapportMensuel() {
        return rapportDAO.findRapportMensuel();
    }

    // ── Rapport mensuel filtré ────────────────────────────────────────────
    public List<RapportMensuel> getRapportMensuelPeriode(LocalDate debut, LocalDate fin) {
        return rapportDAO.findRapportMensuelPeriode(debut, fin);
    }

    // ── Top médicaments vendus ────────────────────────────────────────────
    public List<TopMedicament> getTopMedicaments(LocalDate debut, LocalDate fin) {
        return rapportDAO.findTopMedicaments(debut, fin);
    }

    // ── Ventes sur la période ─────────────────────────────────────────────
    public List<Vente> getVentesPeriode(LocalDate debut, LocalDate fin) {
        return rapportDAO.findVentesPeriode(debut, fin);
    }

    // ── Lignes d'une vente (délègue à VenteDAO existant) ─────────────────
    public List<VenteLigne> getLignesVente(int venteId) {
        return venteDAO.findLignesByVenteId(venteId);
    }

    // ── KPIs globaux : [0]=nb, [1]=CA total, [2]=panier moyen ────────────
    public BigDecimal[] getKpis(LocalDate debut, LocalDate fin) {
        return rapportDAO.findKpis(debut, fin);
    }
}
