package fr.miage.sgpa.dao;

import fr.miage.sgpa.model.Commande;
import fr.miage.sgpa.model.CommandeLigne;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    public void save(Commande commande) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                save(commande, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(Commande commande, Connection conn) throws SQLException {
        String sqlCmd = "INSERT INTO commandes (fournisseur_id, statut) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sqlCmd)) {
            stmt.setInt(1, commande.getFournisseurId());
            stmt.setString(2, commande.getStatut().name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    commande.setId(rs.getInt(1));
                }
            }

            String sqlLigne = "INSERT INTO commande_lignes (commande_id, medicament_id, quantite) VALUES (?, ?, ?)";
            try (PreparedStatement stmtL = conn.prepareStatement(sqlLigne)) {
                for (CommandeLigne ligne : commande.getLignes()) {
                    stmtL.setInt(1, commande.getId());
                    stmtL.setInt(2, ligne.getMedicamentId());
                    stmtL.setInt(3, ligne.getQuantite());
                    stmtL.addBatch();
                }
                stmtL.executeBatch();
            }
        }
    }

    public void updateStatut(int id, Commande.Statut statut, Connection conn) throws SQLException {
        String sql = "UPDATE commandes SET statut = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public List<Commande> findAll() {
        List<Commande> list = new ArrayList<>();
        String sql = "SELECT c.*, f.nom as nom_fournisseur FROM commandes c JOIN fournisseurs f ON c.fournisseur_id = f.id ORDER BY date_creation DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCommande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CommandeLigne> findLignesByCommandeId(int commandeId) {
        List<CommandeLigne> list = new ArrayList<>();
        String sql = "SELECT cl.*, m.nom_commercial FROM commande_lignes cl JOIN medicaments m ON cl.medicament_id = m.id WHERE cl.commande_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CommandeLigne cl = new CommandeLigne();
                    cl.setCommandeId(rs.getInt("commande_id"));
                    cl.setMedicamentId(rs.getInt("medicament_id"));
                    cl.setNomMedicament(rs.getString("nom_commercial"));
                    cl.setQuantite(rs.getInt("quantite"));
                    list.add(cl);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande c = new Commande();
        c.setId(rs.getInt("id"));
        c.setFournisseurId(rs.getInt("fournisseur_id"));
        c.setNomFournisseur(rs.getString("nom_fournisseur"));
        c.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        c.setStatut(Commande.Statut.valueOf(rs.getString("statut")));
        return c;
    }
}
