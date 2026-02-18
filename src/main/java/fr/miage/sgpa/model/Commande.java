package fr.miage.sgpa.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private int fournisseurId;
    private String nomFournisseur; // for display
    private LocalDateTime dateCreation;
    private Statut statut;
    private List<CommandeLigne> lignes = new ArrayList<>();

    public enum Statut {
        EN_ATTENTE, RECUE, ANNULEE
    }

    public Commande() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFournisseurId() { return fournisseurId; }
    public void setFournisseurId(int fournisseurId) { this.fournisseurId = fournisseurId; }
    public String getNomFournisseur() { return nomFournisseur; }
    public void setNomFournisseur(String nomFournisseur) { this.nomFournisseur = nomFournisseur; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public List<CommandeLigne> getLignes() { return lignes; }
    public void setLignes(List<CommandeLigne> lignes) { this.lignes = lignes; }
}
