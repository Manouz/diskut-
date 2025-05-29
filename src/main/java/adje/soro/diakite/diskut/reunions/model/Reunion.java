package adje.soro.diakite.diskut.reunions.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reunion {
    private String id;
    private String nom;
    private String sujet;
    private LocalDateTime dateDebut;
    private int duree; // en minutes
    private String ordreJour;
    private String organisateur; // login de l'organisateur
    private String animateur; // login de l'animateur
    private TypeReunion type;
    private EtatReunion etat;
    private List<String> participants; // login des participants
    private List<Intervention> interventions;

    public enum TypeReunion {
        STANDARD,
        PRIVEE,
        DEMOCRATIQUE
    }

    public enum EtatReunion {
        PLANIFIEE,
        OUVERTE,
        FERMEE
    }

    public Reunion(String id, String nom, String sujet, LocalDateTime dateDebut,
                   int duree, String ordreJour, String organisateur,
                   String animateur, TypeReunion type) {
        this.id = id;
        this.nom = nom;
        this.sujet = sujet;
        this.dateDebut = dateDebut;
        this.duree = duree;
        this.ordreJour = ordreJour;
        this.organisateur = organisateur;
        this.animateur = animateur;
        this.type = type;
        this.etat = EtatReunion.PLANIFIEE;
        this.participants = new ArrayList<>();
        this.interventions = new ArrayList<>();
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

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getOrdreJour() {
        return ordreJour;
    }

    public void setOrdreJour(String ordreJour) {
        this.ordreJour = ordreJour;
    }

    public String getOrganisateur() {
        return organisateur;
    }

    public String getAnimateur() {
        return animateur;
    }

    public void setAnimateur(String animateur) {
        this.animateur = animateur;
    }

    public TypeReunion getType() {
        return type;
    }

    public void setType(TypeReunion type) {
        this.type = type;
    }

    public EtatReunion getEtat() {
        return etat;
    }

    public void setEtat(EtatReunion etat) {
        this.etat = etat;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void ajouterParticipant(String login) {
        if (!participants.contains(login)) {
            participants.add(login);
        }
    }

    public void retirerParticipant(String login) {
        participants.remove(login);
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }

    public void ajouterIntervention(Intervention intervention) {
        interventions.add(intervention);
    }

    public boolean estParticipant(String login) {
        return participants.contains(login);
    }

    public boolean estAutorise(String login) {
        // Pour une réunion privée, vérifier si la personne est autorisée
        // Pour l'instant, on autorise tout le monde
        return true;
    }
}

