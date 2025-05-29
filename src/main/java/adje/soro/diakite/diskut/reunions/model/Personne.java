package adje.soro.diakite.diskut.reunions.model;

import java.util.Objects;

public class Personne {
    private String id;
    private String nom;
    private String login;
    private String motDePasse;
    private EtatPersonne etat;

    public enum EtatPersonne {
        CONNECTE,
        DECONNECTE,
        EN_REUNION
    }

    public Personne(String id, String nom, String login, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.login = login;
        this.motDePasse = motDePasse;
        this.etat = EtatPersonne.DECONNECTE;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLogin() {
        return login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public EtatPersonne getEtat() {
        return etat;
    }

    public void setEtat(EtatPersonne etat) {
        this.etat = etat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return Objects.equals(id, personne.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nom + " (" + login + ")";
    }
}

