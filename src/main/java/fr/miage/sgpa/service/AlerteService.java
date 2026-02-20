package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.MedicamentDAO;
import fr.miage.sgpa.model.AlerteMedicament;
import fr.miage.sgpa.model.Medicament;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service des alertes : stock bas et péremption proche (< 3 mois).
 * Retourne une liste unifiée d'AlerteMedicament avec un TypeAlerte.
 */
public class AlerteService {

    private final MedicamentDAO medicamentDAO;

    public AlerteService() {
        this.medicamentDAO = new MedicamentDAO();
    }

    /**
     * Retourne tous les médicaments en alerte stock (stock <= seuilMin).
     * Pour compatibilité avec le HomeController.
     */
    public List<Medicament> getAlertesStock() {
        return medicamentDAO.findAll().stream()
                .filter(m -> m.getStock() <= m.getSeuilMin())
                .collect(Collectors.toList());
    }

    /** Médicaments PÉRIMÉS : date_peremption strict < aujourd'hui. */
    public List<Medicament> getMedicamentsPerimes() {
        LocalDate today = LocalDate.now();
        return medicamentDAO.findAll().stream()
                .filter(m -> m.getDatePeremption() != null && m.getDatePeremption().isBefore(today))
                .collect(Collectors.toList());
    }

    /**
     * Retourne tous les médicaments dont la péremption est dans < 3 mois.
     * Pour compatibilité avec le HomeController.
     */
    public List<Medicament> getAlertesPeremption() {
        LocalDate limite = LocalDate.now().plusMonths(3);
        return medicamentDAO.findAll().stream()
                .filter(m -> m.getDatePeremption() != null && m.getDatePeremption().isBefore(limite))
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste UNIFIÉE de toutes les alertes :
     *  - Type STOCK    : stock <= seuilMin
     *  - Type PEREMPTION : datePeremption dans moins de 3 mois
     * Triée par type puis par nom commercial.
     */
    public List<AlerteMedicament> getAllAlertes() {
        LocalDate limite = LocalDate.now().plusMonths(3);
        List<AlerteMedicament> result = new ArrayList<>();

        for (Medicament m : medicamentDAO.findAll()) {
            if (m.getStock() <= m.getSeuilMin()) {
                result.add(new AlerteMedicament(
                        m.getNomCommercial(),
                        m.getStock(),
                        m.getSeuilMin(),
                        m.getDatePeremption(),
                        AlerteMedicament.TypeAlerte.STOCK));
            }
            if (m.getDatePeremption() != null && m.getDatePeremption().isBefore(limite)) {
                result.add(new AlerteMedicament(
                        m.getNomCommercial(),
                        m.getStock(),
                        m.getSeuilMin(),
                        m.getDatePeremption(),
                        AlerteMedicament.TypeAlerte.PEREMPTION));
            }
        }

        result.sort(Comparator
                .comparing(AlerteMedicament::getTypeAlerte)
                .thenComparing(AlerteMedicament::getNomCommercial));
        return result;
    }
}
