package fr.miage.sgpa.dao;

import fr.miage.sgpa.model.RapportMensuel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les rapports financiers.
 * Requête : agrégation mensuelle des ventes.
 */
public class RapportDAO {

    /**
     * Retourne les ventes agrégées par mois, du plus ancien au plus récent.
     * SQL :
     *   SELECT TO_CHAR(DATE_TRUNC('month', date_heure), 'YYYY-MM') AS mois,
     *          COUNT(*)                                             AS nb_ventes,
     *          SUM(montant_total)                                   AS total_euros
     *   FROM ventes
     *   GROUP BY DATE_TRUNC('month', date_heure)
     *   ORDER BY DATE_TRUNC('month', date_heure)
     */
    public List<RapportMensuel> findRapportMensuel() {
        List<RapportMensuel> list = new ArrayList<>();
        String sql =
            "SELECT TO_CHAR(DATE_TRUNC('month', date_heure), 'YYYY-MM') AS mois, " +
            "       COUNT(*)           AS nb_ventes, " +
            "       SUM(montant_total) AS total_euros " +
            "FROM ventes " +
            "GROUP BY DATE_TRUNC('month', date_heure) " +
            "ORDER BY DATE_TRUNC('month', date_heure)";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RapportMensuel r = new RapportMensuel(
                    rs.getString("mois"),
                    rs.getInt("nb_ventes"),
                    rs.getBigDecimal("total_euros")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
