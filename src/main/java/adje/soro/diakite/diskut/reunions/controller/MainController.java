package adje.soro.diakite.diskut.reunions.controller;

import adje.soro.diakite.diskut.reunions.model.Personne;
import adje.soro.diakite.diskut.reunions.model.Reunion;
import adje.soro.diakite.diskut.reunions.service.ReunionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label userLabel;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private TableView<Reunion> reunionsTable;

    @FXML
    private TableColumn<Reunion, String> nomColumn;

    @FXML
    private TableColumn<Reunion, String> sujetColumn;

    @FXML
    private TableColumn<Reunion, String> dateColumn;

    @FXML
    private TableColumn<Reunion, String> typeColumn;

    @FXML
    private TableColumn<Reunion, String> etatColumn;

    @FXML
    private Button ouvrirButton;

    @FXML
    private Button fermerButton;

    @FXML
    private Button entrerButton;

    @FXML
    private Button sortirButton;

    @FXML
    private Button detailsButton;

    @FXML
    private TextField nomReunionField;

    @FXML
    private TextField sujetReunionField;

    @FXML
    private DatePicker dateReunionPicker;

    @FXML
    private TextField heureReunionField;

    @FXML
    private TextField dureeReunionField;

    @FXML
    private ComboBox<String> typeReunionCombo;

    @FXML
    private TextArea ordreJourArea;

    @FXML
    private Button planifierButton;

    private ReunionService reunionService = ReunionService.getInstance();
    private ObservableList<Reunion> reunionsObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Personne utilisateurConnecte = reunionService.getUtilisateurConnecte();
        if (utilisateurConnecte != null) {
            userLabel.setText(utilisateurConnecte.getNom() + " (" + utilisateurConnecte.getEtat() + ")");
        }

        // Configuration des colonnes de la table des réunions
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        sujetColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSujet()));
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDateDebut();
            return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().toString()));
        etatColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEtat().toString()));

        // Chargement des réunions
        chargerReunions();

        // Configuration des boutons d'action
        configurerBoutons();

        // Configuration du combobox des types de réunion
        typeReunionCombo.getItems().addAll("STANDARD", "PRIVEE", "DEMOCRATIQUE");
        typeReunionCombo.setValue("STANDARD");
    }

    private void chargerReunions() {
        reunionsObservable.clear();
        reunionsObservable.addAll(reunionService.getReunions());
        reunionsTable.setItems(reunionsObservable);
    }

    private void configurerBoutons() {
        // Désactiver les boutons par défaut
        ouvrirButton.setDisable(true);
        fermerButton.setDisable(true);
        entrerButton.setDisable(true);
        sortirButton.setDisable(true);
        detailsButton.setDisable(true);

        // Activer les boutons en fonction de la sélection
        reunionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Personne utilisateur = reunionService.getUtilisateurConnecte();
                Reunion reunion = newSelection;

                detailsButton.setDisable(false);

                boolean estAnimateur = reunion.getAnimateur().equals(utilisateur.getLogin());
                boolean estParticipant = reunion.estParticipant(utilisateur.getLogin());

                ouvrirButton.setDisable(!(estAnimateur && reunion.getEtat() == Reunion.EtatReunion.PLANIFIEE));
                fermerButton.setDisable(!(estAnimateur && reunion.getEtat() == Reunion.EtatReunion.OUVERTE));
                entrerButton.setDisable(!(reunion.getEtat() == Reunion.EtatReunion.OUVERTE && !estParticipant));
                sortirButton.setDisable(!estParticipant);
            } else {
                ouvrirButton.setDisable(true);
                fermerButton.setDisable(true);
                entrerButton.setDisable(true);
                sortirButton.setDisable(true);
                detailsButton.setDisable(true);
            }
        });
    }

    @FXML
    private void handleOuvrirButton() {
        Reunion reunion = reunionsTable.getSelectionModel().getSelectedItem();
        if (reunion != null) {
            boolean success = reunionService.ouvrirReunion(reunion.getId());
            if (success) {
                chargerReunions();
                afficherMessage("Succès", "La réunion a été ouverte avec succès.");
            } else {
                afficherErreur("Erreur", "Impossible d'ouvrir la réunion.");
            }
        }
    }

    @FXML
    private void handleFermerButton() {
        Reunion reunion = reunionsTable.getSelectionModel().getSelectedItem();
        if (reunion != null) {
            boolean success = reunionService.fermerReunion(reunion.getId());
            if (success) {
                chargerReunions();
                afficherMessage("Succès", "La réunion a été fermée avec succès.");
            } else {
                afficherErreur("Erreur", "Impossible de fermer la réunion.");
            }
        }
    }

    @FXML
    private void handleEntrerButton() {
        Reunion reunion = reunionsTable.getSelectionModel().getSelectedItem();
        if (reunion != null) {
            boolean success = reunionService.entrerReunion(reunion.getId());
            if (success) {
                chargerReunions();
                userLabel.setText(reunionService.getUtilisateurConnecte().getNom() +
                        " (" + reunionService.getUtilisateurConnecte().getEtat() + ")");

                // Ouvrir la fenêtre de la réunion
                ouvrirFenetreReunion(reunion);
            } else {
                afficherErreur("Erreur", "Impossible d'entrer dans la réunion.");
            }
        }
    }

    @FXML
    private void handleSortirButton() {
        Reunion reunion = reunionsTable.getSelectionModel().getSelectedItem();
        if (reunion != null) {
            boolean success = reunionService.sortirReunion(reunion.getId());
            if (success) {
                chargerReunions();
                userLabel.setText(reunionService.getUtilisateurConnecte().getNom() +
                        " (" + reunionService.getUtilisateurConnecte().getEtat() + ")");
                afficherMessage("Succès", "Vous avez quitté la réunion.");
            } else {
                afficherErreur("Erreur", "Impossible de quitter la réunion.");
            }
        }
    }

    @FXML
    private void handleDetailsButton() {
        Reunion reunion = reunionsTable.getSelectionModel().getSelectedItem();
        if (reunion != null) {
            afficherDetailsReunion(reunion);
        }
    }

    @FXML
    private void handlePlanifierButton() {
        String nom = nomReunionField.getText();
        String sujet = sujetReunionField.getText();
        String heure = heureReunionField.getText();
        String dureeStr = dureeReunionField.getText();
        String ordreJour = ordreJourArea.getText();
        String typeStr = typeReunionCombo.getValue();

        if (nom.isEmpty() || sujet.isEmpty() || dateReunionPicker.getValue() == null ||
                heure.isEmpty() || dureeStr.isEmpty()) {
            afficherErreur("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        try {
            int duree = Integer.parseInt(dureeStr);
            if (duree <= 0) {
                afficherErreur("Erreur", "La durée doit être un nombre positif.");
                return;
            }

            // Parsing de l'heure (format HH:mm)
            String[] heureMinute = heure.split(":");
            if (heureMinute.length != 2) {
                afficherErreur("Erreur", "Format d'heure invalide. Utilisez HH:MM.");
                return;
            }

            int heureVal = Integer.parseInt(heureMinute[0]);
            int minuteVal = Integer.parseInt(heureMinute[1]);

            if (heureVal < 0 || heureVal > 23 || minuteVal < 0 || minuteVal > 59) {
                afficherErreur("Erreur", "Valeurs d'heure ou de minute invalides.");
                return;
            }

            LocalDateTime dateDebut = LocalDateTime.of(
                    dateReunionPicker.getValue().getYear(),
                    dateReunionPicker.getValue().getMonth(),
                    dateReunionPicker.getValue().getDayOfMonth(),
                    heureVal,
                    minuteVal
            );

            Reunion.TypeReunion type = Reunion.TypeReunion.valueOf(typeStr);

            Reunion nouvelleReunion = reunionService.creerReunion(nom, sujet, dateDebut, duree, ordreJour, type);

            if (nouvelleReunion != null) {
                chargerReunions();
                afficherMessage("Succès", "La réunion a été planifiée avec succès.");

                // Réinitialiser les champs
                nomReunionField.clear();
                sujetReunionField.clear();
                dateReunionPicker.setValue(null);
                heureReunionField.clear();
                dureeReunionField.clear();
                ordreJourArea.clear();
                typeReunionCombo.setValue("STANDARD");
            } else {
                afficherErreur("Erreur", "Impossible de planifier la réunion.");
            }

        } catch (NumberFormatException e) {
            afficherErreur("Erreur", "La durée doit être un nombre entier.");
        } catch (Exception e) {
            afficherErreur("Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeconnexionButton(ActionEvent event) {
        reunionService.deconnecter();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Serveur de Réunions Virtuelles");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger l'écran de connexion.");
        }
    }

    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherDetailsReunion(Reunion reunion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la réunion");
        alert.setHeaderText(reunion.getNom());

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Sujet: " + reunion.getSujet()),
                new Label("Date: " + reunion.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                new Label("Durée: " + reunion.getDuree() + " minutes"),
                new Label("Type: " + reunion.getType()),
                new Label("État: " + reunion.getEtat()),
                new Label("Organisateur: " + reunion.getOrganisateur()),
                new Label("Animateur: " + reunion.getAnimateur()),
                new Label("Participants: " + String.join(", ", reunion.getParticipants())),
                new Label("Ordre du jour:"),
                new TextArea(reunion.getOrdreJour())
        );

        ((TextArea) content.getChildren().get(9)).setEditable(false);
        ((TextArea) content.getChildren().get(9)).setPrefHeight(100);

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }

    private void ouvrirFenetreReunion(Reunion reunion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReunionView.fxml"));
            Parent root = loader.load();

            ReunionController controller = loader.getController();
            controller.initialiserReunion(reunion);

            Stage stage = new Stage();
            stage.setTitle("Réunion: " + reunion.getNom());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de réunion.");
        }
    }
}
