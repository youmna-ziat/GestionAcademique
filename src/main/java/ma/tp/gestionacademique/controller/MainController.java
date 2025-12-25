package ma.tp.gestionacademique.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ma.tp.gestionacademique.MainApp;

public class MainController {

    // ===== NAVIGATION SIMPLE =====
    @FXML
    private void goToEleve(ActionEvent event) {
        MainApp.setScene("/ma/tp/gestionacademique/eleve.fxml");
    }

    @FXML
    private void goToCours(ActionEvent event) {
        MainApp.setScene("/ma/tp/gestionacademique/cours.fxml");
    }

    @FXML
    private void goToFiliere(ActionEvent event) {
        MainApp.setScene("/ma/tp/gestionacademique/filiere.fxml");
    }

    // ===== OUVRIR DOSSIER ADMINISTRATIF DANS UNE NOUVELLE FENÊTRE =====
    @FXML
    private void goToDossier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/ma/tp/gestionacademique/dossier_administratif_read.fxml"
            ));
            Stage stage = new Stage();
            stage.setTitle("Dossiers Administratifs");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            // Init controller si nécessaire
            DossierAdministratifReadController controller = loader.getController();
            controller.initForDashboard();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
