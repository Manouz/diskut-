package adje.soro.diakite.diskut;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {






    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/adje/soro/diakite/diskut/LoginView.fxml"));

            // Vérifier si le fichier FXML existe
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("Fichier FXML 'LoginView.fxml' non trouvé");
            }

            Parent root = fxmlLoader.load();

            primaryStage.setTitle("Serveur de Réunions Virtuelles");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier FXML: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    //@Override
    //public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("/adje/soro/diakite/diskut/LoginView.fxml"));
        //primaryStage.setTitle("Serveur de Réunions Virtuelles");
        //primaryStage.setScene(new Scene(root, 600, 400));
        //primaryStage.setMinWidth(600);
        //primaryStage.setMinHeight(400);
        //primaryStage.show();
    //}
    public static void main(String[] args) {
        launch();
    }
}