package ma.tp.gestionacademique.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.tp.gestionacademique.dao.DossierAdministratifDAO;
import ma.tp.gestionacademique.model.DossierAdministratif;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DossierAdministratifController {

    @FXML
    private TextField numeroField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label eleveLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Label numeroLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private ListView<DossierAdministratif> dossierListView;



    private int eleveId; // injecté depuis EleveController
    private DossierAdministratifDAO dao = new DossierAdministratifDAO();
    private DossierAdministratif dossierExistant;

    // Mode lecture seule pour le dashboard
    // Lecture seule
    // Lecture seule : pour dossier_administratif_read.fxml
    public void initForDashboard(int eleveId, String eleveNom) {
        eleveLabel.setText(eleveNom);

        try {
            List<DossierAdministratif> dossiers = dao.getAllDossiersByEleveId(eleveId);
            dossierListView.getItems().setAll(dossiers);

            if (!dossiers.isEmpty()) {
                DossierAdministratif d = dossiers.get(0); // afficher le premier dossier
                numeroLabel.setText(d.getNumeroInscription());
                dateLabel.setText(d.getDateCreation().toString());
            } else {
                numeroLabel.setText("Aucun dossier existant");
                dateLabel.setText("-");
            }

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // Formulaire : pour dossier_administratif_form.fxml
    public void initData(int eleveId, String eleveNomComplet) {
        this.eleveId = eleveId;
        eleveLabel.setText(eleveNomComplet);

        try {
            dossierExistant = dao.getDossierByEleveId(eleveId);

            eleveLabel.setDisable(true);
            datePicker.setEditable(false);

            if (dossierExistant != null) {
                numeroField.setText(dossierExistant.getNumeroInscription());
                numeroField.setEditable(false);
                datePicker.setValue(dossierExistant.getDateCreation().toLocalDate());
                saveButton.setVisible(false);
            } else {
                numeroField.clear();
                numeroField.setEditable(true);
                datePicker.setValue(LocalDate.now());
                saveButton.setVisible(true);
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }


    @FXML
    private void handleSave() {

        if (numeroField.getText().isEmpty()) {
            showError("Le numéro d'inscription est obligatoire");
            return;
        }

        try {
            if (dossierExistant == null) {
                DossierAdministratif d = new DossierAdministratif();
                d.setNumeroInscription(numeroField.getText());
                d.setDateCreation(Date.valueOf(LocalDate.now()));
                d.setEleveId(eleveId);
                dao.addDossier(d);
            } else {
                dossierExistant.setNumeroInscription(numeroField.getText());
                dao.updateDossier(dossierExistant);
            }

            showInfo("Dossier enregistré avec succès.");

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }


    @FXML
    private void handleClose() {
        // récupère n’importe quel Node du FXML pour fermer la fenêtre
        eleveLabel.getScene().getWindow().hide();
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
