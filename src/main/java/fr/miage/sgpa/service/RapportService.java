package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.RapportDAO;
import fr.miage.sgpa.model.RapportMensuel;

import java.util.List;

/**
 * Service pour les rapports financiers mensuels.
 */
public class RapportService {

    private final RapportDAO rapportDAO;

    public RapportService() {
        this.rapportDAO = new RapportDAO();
    }

    /**
     * Retourne les rapports mensuels (mois, nb ventes, total €),
     * triés du plus ancien au plus récent.
     */
    public List<RapportMensuel> getRapportMensuel() {
        return rapportDAO.findRapportMensuel();
    }
}
