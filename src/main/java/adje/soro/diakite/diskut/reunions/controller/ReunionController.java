package adje.soro.diakite.diskut.reunions.controller;

import adje.soro.diakite.diskut.reunions.model.Intervention;
import adje.soro.diakite.diskut.reunions.model.Personne;
import adje.soro.diakite.diskut.reunions.model.Reunion;
import adje.soro.diakite.diskut.reunions.service.ReunionService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class ReunionController {

    @FXML
    private Label titreLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button envoyerButton;

    @FXML
    private Button demanderParoleButton;

    @FXML
    private Button relacherParoleButton;

    @FXML
    private ListView<String> participantsListView;

    @FXML
    private ListView<String> demandesParoleListView;

    @FXML
    private Button accorderParoleButton;

    private ReunionService reunionService = ReunionService.getInstance();
    private Reunion reunion;
    private Timer refreshTimer;
    private boolean aLaParole = false;

    public void initialiserReunion(Reunion reunion) {
        this.reunion = reunion;

        titreLabel.setText(reunion.getNom());
        infoLabel.setText("Sujet: " + reunion.getSujet() + " | Animateur: " + reunion.getAnimateur());

        // Afficher les interventions existantes
        afficherInterventions();

        // Afficher les participants
        mettreAJourParticipants();

        // Configuration des boutons
        Personne utilisateur = reunionService.getUtilisateurConnecte();
        boolean estAnimateur = reunion.getAnimateur().equals(utilisateur.getLogin());

        accorderParoleButton.setVisible(estAnimateur);
        accorderParoleButton.setManaged(estAnimateur);

        // Désactiver le champ de message par défaut (jusqu'à avoir la parole)
        messageField.setDisable(true);
        envoyerButton.setDisable(true);
        relacherParoleButton.setDisable(true);

        // Démarrer le timer de rafraîchissement
        demarrerTimerRafraichissement();
    }

    private void demarrerTimerRafraichissement() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // Rafraîchir les données depuis le service
                    Reunion reunionMiseAJour = reunionService.getReunion(reunion.getId());
                    if (reunionMiseAJour != null) {
                        reunion = reunionMiseAJour;
                        afficherInterventions();
                        mettreAJourParticipants();

                        // Vérifier si la réunion est fermée
                        if (reunion.getEtat() == Reunion.EtatReunion.FERMEE) {
                            arreterTimerRafraichissement();
                            afficherMessageFermeture();
                        }
                    }
                });
            }
        }, 0, 2000); // Rafraîchir toutes les 2 secondes
    }

    private void arreterTimerRafraichissement() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }

    private void afficherMessageFermeture() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réunion fermée");
        alert.setHeaderText(null);
        alert.setContentText("Cette réunion a été fermée par l'animateur.");
        alert.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) titreLabel.getScene().getWindow();
        stage.close();
    }

    private void afficherInterventions() {
        chatArea.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (Intervention intervention : reunion.getInterventions()) {
            String timestamp = intervention.getTimestamp().format(formatter);
            chatArea.appendText("[" + timestamp + "] " + intervention.getAuteur() + ": " + intervention.getContenu() + "\n");
        }

        // Défiler vers le bas
        chatArea.setScrollTop(Double.MAX_VALUE);
    }

    private void mettreAJourParticipants() {
        participantsListView.getItems().clear();
        for (String participant : reunion.getParticipants()) {
            participantsListView.getItems().add(participant);
        }
    }

    @FXML
    private void handleEnvoyerButton() {
        if (!aLaParole) {
            afficherErreur("Erreur", "Vous n'avez pas la parole.");
            return;
        }

        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            boolean success = reunionService.ajouterIntervention(reunion.getId(), message);
            if (success) {
                messageField.clear();
                afficherInterventions();
            } else {
                afficherErreur("Erreur", "Impossible d'envoyer le message.");
            }
        }
    }

    @FXML
    private void handleDemanderParoleButton() {
        // Dans une implémentation réelle, cela enverrait une demande au serveur
        // Pour l'instant, on simule en donnant directement la parole
        aLaParole = true;
        messageField.setDisable(false);
        envoyerButton.setDisable(false);
        demanderParoleButton.setDisable(true);
        relacherParoleButton.setDisable(false);
    }

    @FXML
    private void handleRelacherParoleButton() {
        aLaParole = false;
        messageField.setDisable(true);
        envoyerButton.setDisable(true);
        demanderParoleButton.setDisable(false);
        relacherParoleButton.setDisable(true);
    }

    @FXML
    private void handleAccorderParoleButton() {
        String participant = demandesParoleListView.getSelectionModel().getSelectedItem();
        if (participant != null) {
            // Dans une implémentation réelle, cela enverrait une notification au participant
            demandesParoleListView.getItems().remove(participant);
        }
    }

    @FXML
    private void handleQuitterButton() {
        reunionService.sortirReunion(reunion.getId());
        arreterTimerRafraichissement();

        // Fermer la fenêtre
        Stage stage = (Stage) titreLabel.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
