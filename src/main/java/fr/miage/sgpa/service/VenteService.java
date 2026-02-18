package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.DatabaseConfig;
import fr.miage.sgpa.dao.MedicamentDAO;
import fr.miage.sgpa.dao.VenteDAO;
import fr.miage.sgpa.model.Medicament;
import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import fr.miage.sgpa.dao.ConnectionProvider;
import java.util.List;

public class VenteService {
    private final VenteDAO venteDAO;
    private final MedicamentDAO medicamentDAO;
    private final ConnectionProvider connectionProvider;

    public VenteService() {
        this(new VenteDAO(), new MedicamentDAO(), DatabaseConfig::getConnection);
    }

    public VenteService(VenteDAO venteDAO, MedicamentDAO medicamentDAO, ConnectionProvider connectionProvider) {
        this.venteDAO = venteDAO;
        this.medicamentDAO = medicamentDAO;
        this.connectionProvider = connectionProvider;
    }

    public List<Vente> getAllVentes() {
        return venteDAO.findAll();
    }

    public void effectuerVente(Vente vente) throws SQLException, Exception {
        try (Connection conn = connectionProvider.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (VenteLigne ligne : vente.getLignes()) {
                    Optional<Medicament> mOpt = medicamentDAO.findById(ligne.getMedicamentId(), conn);
                    if (mOpt.isEmpty()) {
                        throw new Exception("Médicament non trouvé: " + ligne.getMedicamentId());
                    }
                    Medicament m = mOpt.get();
                    if (m.getStock() < ligne.getQuantite()) {
                        throw new Exception("Stock insuffisant pour " + m.getNomCommercial());
                    }
                    if (m.isNecessiteOrdonnance() && !vente.isSurOrdonnance()) {
                        throw new Exception("Ordonnance requise pour " + m.getNomCommercial());
                    }

                    // Décrémenter stock
                    medicamentDAO.updateStock(m.getId(), m.getStock() - ligne.getQuantite(), conn);
                }

                venteDAO.save(vente, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
