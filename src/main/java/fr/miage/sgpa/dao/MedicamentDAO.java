package fr.miage.sgpa.dao;

import fr.miage.sgpa.model.Medicament;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicamentDAO {
    public List<Medicament> findAll() {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM medicaments";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToMedicament(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<Medicament> findById(int id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return findById(id, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Medicament> findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM medicaments WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedicament(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void save(Medicament m) {
        String sql = "INSERT INTO medicaments (nom_commercial, principe_actif, forme_galenique, dosage, prix_public, necessite_ordonnance, date_peremption, stock, seuil_min) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNomCommercial());
            stmt.setString(2, m.getPrincipeActif());
            stmt.setString(3, m.getFormeGalenique());
            stmt.setString(4, m.getDosage());
            stmt.setBigDecimal(5, m.getPrixPublic());
            stmt.setBoolean(6, m.isNecessiteOrdonnance());
            stmt.setDate(7, Date.valueOf(m.getDatePeremption()));
            stmt.setInt(8, m.getStock());
            stmt.setInt(9, m.getSeuilMin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Medicament m) {
        String sql = "UPDATE medicaments SET nom_commercial=?, principe_actif=?, forme_galenique=?, dosage=?, prix_public=?, necessite_ordonnance=?, date_peremption=?, stock=?, seuil_min=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNomCommercial());
            stmt.setString(2, m.getPrincipeActif());
            stmt.setString(3, m.getFormeGalenique());
            stmt.setString(4, m.getDosage());
            stmt.setBigDecimal(5, m.getPrixPublic());
            stmt.setBoolean(6, m.isNecessiteOrdonnance());
            stmt.setDate(7, Date.valueOf(m.getDatePeremption()));
            stmt.setInt(8, m.getStock());
            stmt.setInt(9, m.getSeuilMin());
            stmt.setInt(10, m.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(int id, int nouveauStock, Connection conn) throws SQLException {
        String sql = "UPDATE medicaments SET stock = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nouveauStock);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM medicaments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Medicament mapResultSetToMedicament(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();
        m.setId(rs.getInt("id"));
        m.setNomCommercial(rs.getString("nom_commercial"));
        m.setPrincipeActif(rs.getString("principe_actif"));
        m.setFormeGalenique(rs.getString("forme_galenique"));
        m.setDosage(rs.getString("dosage"));
        m.setPrixPublic(rs.getBigDecimal("prix_public"));
        m.setNecessiteOrdonnance(rs.getBoolean("necessite_ordonnance"));
        m.setDatePeremption(rs.getDate("date_peremption").toLocalDate());
        m.setStock(rs.getInt("stock"));
        m.setSeuilMin(rs.getInt("seuil_min"));
        return m;
    }
}
