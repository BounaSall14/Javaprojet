package fr.miage.sgpa.dao;

import fr.miage.sgpa.model.Fournisseur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FournisseurDAO {
    public List<Fournisseur> findAll() {
        List<Fournisseur> list = new ArrayList<>();
        String sql = "SELECT * FROM fournisseurs";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<Fournisseur> findById(int id) {
        String sql = "SELECT * FROM fournisseurs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFournisseur(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(Fournisseur f) {
        String sql = "INSERT INTO fournisseurs (nom, contact, adresse) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getContact());
            stmt.setString(3, f.getAdresse());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Fournisseur f) {
        String sql = "UPDATE fournisseurs SET nom=?, contact=?, adresse=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getContact());
            stmt.setString(3, f.getAdresse());
            stmt.setInt(4, f.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM fournisseurs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Fournisseur mapResultSetToFournisseur(ResultSet rs) throws SQLException {
        Fournisseur f = new Fournisseur();
        f.setId(rs.getInt("id"));
        f.setNom(rs.getString("nom"));
        f.setContact(rs.getString("contact"));
        f.setAdresse(rs.getString("adresse"));
        return f;
    }
}
