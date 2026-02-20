package fr.miage.sgpa.dao;

import fr.miage.sgpa.model.RapportMensuel;
import fr.miage.sgpa.model.TopMedicament;
import fr.miage.sgpa.model.Vente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les rapports financiers enrichis :
 * - rapport mensuel (toutes périodes ou filtré)
 * - top 10 médicaments vendus
 * - liste des ventes sur une période
 * - KPIs globaux (nb ventes, CA, panier moyen)
 */
public class RapportDAO {

    // ── Rapport mensuel (toutes périodes, compatibilité existante) ────────
    public List<RapportMensuel> findRapportMensuel() {
        return findRapportMensuelPeriode(null, null);
    }

    // ── Rapport mensuel filtré par période ────────────────────────────────
    public List<RapportMensuel> findRapportMensuelPeriode(LocalDate debut, LocalDate fin) {
        List<RapportMensuel> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT TO_CHAR(DATE_TRUNC('month', date_heure), 'YYYY-MM') AS mois, " +
                        "       COUNT(*)           AS nb_ventes, " +
                        "       SUM(montant_total) AS total_euros " +
                        "FROM ventes WHERE 1=1 ");
        if (debut != null)
            sql.append("AND date_heure >= ? ");
        if (fin != null)
            sql.append("AND date_heure <  ? ");
        sql.append("GROUP BY DATE_TRUNC('month', date_heure) ORDER BY DATE_TRUNC('month', date_heure)");

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (debut != null)
                ps.setTimestamp(idx++, Timestamp.valueOf(debut.atStartOfDay()));
            if (fin != null)
                ps.setTimestamp(idx, Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RapportMensuel(
                            rs.getString("mois"),
                            rs.getInt("nb_ventes"),
                            rs.getBigDecimal("total_euros")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Top 10 médicaments vendus sur la période ──────────────────────────
    /**
     * SQL : JOIN vente_lignes + medicaments + ventes
     * GROUP BY nom_commercial, ORDER BY ca desc, LIMIT 10
     */
    public List<TopMedicament> findTopMedicaments(LocalDate debut, LocalDate fin) {
        List<TopMedicament> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT m.nom_commercial, " +
                        "       SUM(vl.quantite)                    AS qte_totale, " +
                        "       SUM(vl.quantite * vl.prix_unitaire) AS ca_genere " +
                        "FROM vente_lignes vl " +
                        "JOIN medicaments m ON m.id = vl.medicament_id " +
                        "JOIN ventes v      ON v.id = vl.vente_id " +
                        "WHERE 1=1 ");
        if (debut != null)
            sql.append("AND v.date_heure >= ? ");
        if (fin != null)
            sql.append("AND v.date_heure <  ? ");
        sql.append("GROUP BY m.nom_commercial ORDER BY ca_genere DESC LIMIT 10");

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (debut != null)
                ps.setTimestamp(idx++, Timestamp.valueOf(debut.atStartOfDay()));
            if (fin != null)
                ps.setTimestamp(idx, Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TopMedicament(
                            rs.getString("nom_commercial"),
                            rs.getInt("qte_totale"),
                            rs.getBigDecimal("ca_genere")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Liste des ventes sur la période ───────────────────────────────────
    public List<Vente> findVentesPeriode(LocalDate debut, LocalDate fin) {
        List<Vente> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, date_heure, sur_ordonnance, montant_total FROM ventes WHERE 1=1 ");
        if (debut != null)
            sql.append("AND date_heure >= ? ");
        if (fin != null)
            sql.append("AND date_heure <  ? ");
        sql.append("ORDER BY date_heure DESC");

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (debut != null)
                ps.setTimestamp(idx++, Timestamp.valueOf(debut.atStartOfDay()));
            if (fin != null)
                ps.setTimestamp(idx, Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vente v = new Vente();
                    v.setId(rs.getInt("id"));
                    Timestamp ts = rs.getTimestamp("date_heure");
                    if (ts != null)
                        v.setDateHeure(ts.toLocalDateTime());
                    v.setSurOrdonnance(rs.getBoolean("sur_ordonnance"));
                    v.setMontantTotal(rs.getBigDecimal("montant_total"));
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── KPIs globaux : [0]=nb ventes, [1]=CA total, [2]=panier moyen ──────
    public BigDecimal[] findKpis(LocalDate debut, LocalDate fin) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS nb, COALESCE(SUM(montant_total), 0) AS ca FROM ventes WHERE 1=1 ");
        if (debut != null)
            sql.append("AND date_heure >= ? ");
        if (fin != null)
            sql.append("AND date_heure <  ? ");

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (debut != null)
                ps.setTimestamp(idx++, Timestamp.valueOf(debut.atStartOfDay()));
            if (fin != null)
                ps.setTimestamp(idx, Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int nb = rs.getInt("nb");
                    BigDecimal ca = rs.getBigDecimal("ca");
                    if (ca == null)
                        ca = BigDecimal.ZERO;
                    BigDecimal panier = nb > 0
                            ? ca.divide(BigDecimal.valueOf(nb), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new BigDecimal[] { BigDecimal.valueOf(nb), ca, panier };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
    }
}
