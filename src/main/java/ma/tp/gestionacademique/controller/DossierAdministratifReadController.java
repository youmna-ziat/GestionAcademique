package ma.tp.gestionacademique.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ma.tp.gestionacademique.dao.DossierAdministratifDAO;
import ma.tp.gestionacademique.model.DossierAdministratif;

import java.sql.SQLException;
import java.util.List;

public class DossierAdministratifReadController {

    @FXML
    private TableView<DossierAdministratif> dossierTable;

    @FXML
    private TableColumn<DossierAdministratif, String> numeroColumn;

    @FXML
    private TableColumn<DossierAdministratif, String> dateColumn;

    @FXML
    private TableColumn<DossierAdministratif, String> eleveColumn;

    private DossierAdministratifDAO dao = new DossierAdministratifDAO();

    public void initForDashboard() {
        try {
            List<DossierAdministratif> dossiers = dao.getAllDossiers(); // récupère tous les dossiers

            // Initialisation des colonnes
            numeroColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNumeroInscription()));
            dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDateCreation().toString()));

            // Pour afficher le nom de l'élève, il faut ajouter une méthode dans DAO pour récupérer nom+prenom par id
            eleveColumn.setCellValueFactory(data -> {
                try {
                    String nomEleve = dao.getNomEleveById(data.getValue().getEleveId());
                    return new javafx.beans.property.SimpleStringProperty(nomEleve);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new javafx.beans.property.SimpleStringProperty("Erreur");
                }
            });

            dossierTable.setItems(FXCollections.observableArrayList(dossiers));

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) dossierTable.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}