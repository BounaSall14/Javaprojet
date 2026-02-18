package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.CommandeDAO;
import fr.miage.sgpa.dao.MedicamentDAO;
import fr.miage.sgpa.model.Commande;
import fr.miage.sgpa.model.CommandeLigne;
import fr.miage.sgpa.model.Medicament;

import java.util.List;
import java.util.Optional;

public class CommandeService {
    private final CommandeDAO commandeDAO;
    private final MedicamentDAO medicamentDAO;

    public CommandeService() {
        this.commandeDAO = new CommandeDAO();
        this.medicamentDAO = new MedicamentDAO();
    }

    public void passerCommande(Commande commande) {
        commande.setStatut(Commande.Statut.EN_ATTENTE);
        commandeDAO.save(commande);
    }

    public void receptionnerCommande(int commandeId) throws Exception {
        try (java.sql.Connection conn = fr.miage.sgpa.dao.DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<CommandeLigne> lignes = commandeDAO.findLignesByCommandeId(commandeId);
                for (CommandeLigne ligne : lignes) {
                    Optional<Medicament> mOpt = medicamentDAO.findById(ligne.getMedicamentId(), conn);
                    if (mOpt.isPresent()) {
                        Medicament m = mOpt.get();
                        medicamentDAO.updateStock(m.getId(), m.getStock() + ligne.getQuantite(), conn);
                    }
                }
                commandeDAO.updateStatut(commandeId, Commande.Statut.RECUE, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Commande> getAllCommandes() {
        return commandeDAO.findAll();
    }
}
