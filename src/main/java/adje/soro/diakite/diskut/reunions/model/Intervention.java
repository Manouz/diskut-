package adje.soro.diakite.diskut.reunions.model;

import java.time.LocalDateTime;

public class Intervention {
    private String id;
    private String auteur; // login de l'auteur
    private String contenu;
    private LocalDateTime timestamp;

    public Intervention(String id, String auteur, String contenu) {
        this.id = id;
        this.auteur = auteur;
        this.contenu = contenu;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getContenu() {
        return contenu;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

