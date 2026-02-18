package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.MedicamentDAO;
import fr.miage.sgpa.model.Medicament;

import java.util.List;
import java.util.Optional;

public class MedicamentService {
    private final MedicamentDAO medicamentDAO;

    public MedicamentService() {
        this.medicamentDAO = new MedicamentDAO();
    }

    public List<Medicament> getAllMedicaments() {
        return medicamentDAO.findAll();
    }

    public Optional<Medicament> getMedicamentById(int id) {
        return medicamentDAO.findById(id);
    }

    public void addMedicament(Medicament m) {
        medicamentDAO.save(m);
    }

    public void updateMedicament(Medicament m) {
        medicamentDAO.update(m);
    }

    public void deleteMedicament(int id) {
        medicamentDAO.delete(id);
    }
}
