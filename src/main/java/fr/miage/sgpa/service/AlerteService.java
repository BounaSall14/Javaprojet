package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.MedicamentDAO;
import fr.miage.sgpa.model.Medicament;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AlerteService {
    private final MedicamentDAO medicamentDAO;

    public AlerteService() {
        this.medicamentDAO = new MedicamentDAO();
    }

    public List<Medicament> getAlertesStock() {
        return medicamentDAO.findAll().stream()
                .filter(m -> m.getStock() <= m.getSeuilMin())
                .collect(Collectors.toList());
    }

    public List<Medicament> getAlertesPeremption() {
        LocalDate limite = LocalDate.now().plusMonths(3);
        return medicamentDAO.findAll().stream()
                .filter(m -> m.getDatePeremption().isBefore(limite))
                .collect(Collectors.toList());
    }
}
