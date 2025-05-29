package adje.soro.diakite.diskut.reunions.service;
import adje.soro.diakite.diskut.reunions.model.Intervention;
import adje.soro.diakite.diskut.reunions.model.Personne;
import adje.soro.diakite.diskut.reunions.model.Reunion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReunionService {
    private static ReunionService instance;
    private List<Reunion> reunions;
    private List<Personne> utilisateurs;
    private Personne utilisateurConnecte;

    private ReunionService() {
        this.reunions = new ArrayList<>();
        this.utilisateurs = new ArrayList<>();

        // Initialisation des utilisateurs de test
        utilisateurs.add(new Personne("1", "Alice Martin", "alice", "password123"));
        utilisateurs.add(new Personne("2", "Bob Dupont", "bob", "password123"));
        utilisateurs.add(new Personne("3", "Charlie Durand", "charlie", "password123"));

        // Initialisation des réunions de test
        Reunion reunion1 = new Reunion(
                "1",
                "Réunion Projet Alpha",
                "Planification Sprint 1",
                LocalDateTime.now().plusDays(1),
                120,
                "1. État d'avancement\n2. Prochaines étapes\n3. Questions diverses",
                "alice",
                "alice",
                Reunion.TypeReunion.STANDARD
        );

        Reunion reunion2 = new Reunion(
                "2",
                "Réunion Équipe Dev",
                "Revue de code",
                LocalDateTime.now().plusHours(2),
                90,
                "1. Revue PR\n2. Standards de code\n3. Outils",
                "bob",
                "bob",
                Reunion.TypeReunion.PRIVEE
        );
        reunion2.setEtat(Reunion.EtatReunion.OUVERTE);
        reunion2.ajouterParticipant("alice");
        reunion2.ajouterParticipant("charlie");
        reunion2.ajouterIntervention(new Intervention("1", "bob", "Bienvenue dans cette réunion de revue de code"));

        reunions.add(reunion1);
        reunions.add(reunion2);
    }

    public static ReunionService getInstance() {
        if (instance == null) {
            instance = new ReunionService();
        }
        return instance;
    }

    public Personne connecter(String login, String motDePasse) {
        for (Personne utilisateur : utilisateurs) {
            if (utilisateur.getLogin().equals(login) && utilisateur.getMotDePasse().equals(motDePasse)) {
                utilisateur.setEtat(Personne.EtatPersonne.CONNECTE);
                utilisateurConnecte = utilisateur;
                return utilisateur;
            }
        }
        return null;
    }

    public void deconnecter() {
        if (utilisateurConnecte != null) {
            utilisateurConnecte.setEtat(Personne.EtatPersonne.DECONNECTE);
            utilisateurConnecte = null;
        }
    }

    public Personne getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public List<Reunion> getReunions() {
        return new ArrayList<>(reunions);
    }

    public Reunion creerReunion(String nom, String sujet, LocalDateTime dateDebut,
                                int duree, String ordreJour, Reunion.TypeReunion type) {
        if (utilisateurConnecte == null) {
            return null;
        }

        String id = UUID.randomUUID().toString();
        Reunion nouvelleReunion = new Reunion(
                id, nom, sujet, dateDebut, duree, ordreJour,
                utilisateurConnecte.getLogin(), utilisateurConnecte.getLogin(), type
        );

        reunions.add(nouvelleReunion);
        return nouvelleReunion;
    }

    public boolean ouvrirReunion(String reunionId) {
        if (utilisateurConnecte == null) {
            return false;
        }

        for (Reunion reunion : reunions) {
            if (reunion.getId().equals(reunionId) &&
                    reunion.getAnimateur().equals(utilisateurConnecte.getLogin()) &&
                    reunion.getEtat() == Reunion.EtatReunion.PLANIFIEE) {
                reunion.setEtat(Reunion.EtatReunion.OUVERTE);
                return true;
            }
        }
        return false;
    }

    public boolean fermerReunion(String reunionId) {
        if (utilisateurConnecte == null) {
            return false;
        }

        for (Reunion reunion : reunions) {
            if (reunion.getId().equals(reunionId) &&
                    reunion.getAnimateur().equals(utilisateurConnecte.getLogin()) &&
                    reunion.getEtat() == Reunion.EtatReunion.OUVERTE) {
                reunion.setEtat(Reunion.EtatReunion.FERMEE);
                return true;
            }
        }
        return false;
    }

    public boolean entrerReunion(String reunionId) {
        if (utilisateurConnecte == null) {
            return false;
        }

        for (Reunion reunion : reunions) {
            if (reunion.getId().equals(reunionId) &&
                    reunion.getEtat() == Reunion.EtatReunion.OUVERTE &&
                    reunion.estAutorise(utilisateurConnecte.getLogin())) {
                reunion.ajouterParticipant(utilisateurConnecte.getLogin());
                utilisateurConnecte.setEtat(Personne.EtatPersonne.EN_REUNION);
                return true;
            }
        }
        return false;
    }

    public boolean sortirReunion(String reunionId) {
        if (utilisateurConnecte == null) {
            return false;
        }

        for (Reunion reunion : reunions) {
            if (reunion.getId().equals(reunionId) &&
                    reunion.estParticipant(utilisateurConnecte.getLogin())) {
                reunion.retirerParticipant(utilisateurConnecte.getLogin());
                utilisateurConnecte.setEtat(Personne.EtatPersonne.CONNECTE);
                return true;
            }
        }
        return false;
    }

    public boolean ajouterIntervention(String reunionId, String contenu) {
        if (utilisateurConnecte == null) {
            return false;
        }

        for (Reunion reunion : reunions) {
            if (reunion.getId().equals(reunionId) &&
                    reunion.estParticipant(utilisateurConnecte.getLogin())) {
                String id = UUID.randomUUID().toString();
                Intervention intervention = new Intervention(id, utilisateurConnecte.getLogin(), contenu);
                reunion.ajouterIntervention(intervention);
                return true;
            }
        }
        return false;
    }

    public Reunion getReunion(String reunionId) {
        for (Reunion reunion : reunions) {
            if (reunion.getId().equals(reunionId)) {
                return reunion;
            }
        }
        return null;
    }
}
