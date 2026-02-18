package fr.miage.sgpa.dao;

import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {
    public void save(Vente vente, Connection conn) throws SQLException {
        String sqlVente = "INSERT INTO ventes (sur_ordonnance, montant_total) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sqlVente)) {
            stmt.setBoolean(1, vente.isSurOrdonnance());
            stmt.setBigDecimal(2, vente.getMontantTotal());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vente.setId(rs.getInt(1));
                }
            }
        }

        String sqlLigne = "INSERT INTO vente_lignes (vente_id, medicament_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sqlLigne)) {
            for (VenteLigne ligne : vente.getLignes()) {
                stmt.setInt(1, vente.getId());
                stmt.setInt(2, ligne.getMedicamentId());
                stmt.setInt(3, ligne.getQuantite());
                stmt.setBigDecimal(4, ligne.getPrixUnitaire());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public List<Vente> findAll() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes ORDER BY date_heure DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ventes.add(mapResultSetToVente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventes;
    }

    private Vente mapResultSetToVente(ResultSet rs) throws SQLException {
        Vente v = new Vente();
        v.setId(rs.getInt("id"));
        v.setDateHeure(rs.getTimestamp("date_heure").toLocalDateTime());
        v.setSurOrdonnance(rs.getBoolean("sur_ordonnance"));
        v.setMontantTotal(rs.getBigDecimal("montant_total"));
        return v;
    }
}
