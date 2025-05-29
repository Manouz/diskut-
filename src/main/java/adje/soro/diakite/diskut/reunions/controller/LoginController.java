package adje.soro.diakite.diskut.reunions.controller;
import adje.soro.diakite.diskut.reunions.model.Personne;
import adje.soro.diakite.diskut.reunions.service.ReunionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private ReunionService reunionService = ReunionService.getInstance();

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Veuillez remplir tous les champs.");
            return;
        }

        Personne utilisateur = reunionService.connecter(login, password);
        if (utilisateur != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Serveur de Réunions Virtuelles - " + utilisateur.getNom());
                stage.setScene(new Scene(root, 900, 600));
                stage.setMinWidth(900);
                stage.setMinHeight(600);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface principale.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Login ou mot de passe incorrect.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
