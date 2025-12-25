package ma.tp.gestionacademique.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import ma.tp.gestionacademique.dao.EleveDAO;
import ma.tp.gestionacademique.dao.FiliereDAO;
import ma.tp.gestionacademique.model.Cours;
import ma.tp.gestionacademique.model.Eleve;
import ma.tp.gestionacademique.model.Filiere;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EleveController implements Initializable {

    // ===== Table =====
    @FXML
    private TableView<Eleve> eleveTable;

    @FXML
    private TableColumn<Eleve, Integer> colId;

    @FXML
    private TableColumn<Eleve, String> colMatricule;

    @FXML
    private TableColumn<Eleve, String> colNom;

    @FXML
    private TableColumn<Eleve, String> colPrenom;

    @FXML
    private TableColumn<Eleve, String> colEmail;

    @FXML
    private TableColumn<Eleve, String> colStatut;

    @FXML
    private TableColumn<Eleve, Integer> colFiliere;

    // ===== Formulaire =====
    @FXML
    private TextField matriculeField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> statutBox;

    @FXML
    private ComboBox<String> filiereBox;


    private final EleveDAO eleveDAO = new EleveDAO();
    private final FiliereDAO filiereDAO = new FiliereDAO();
    private final ObservableList<Eleve> eleveList = FXCollections.observableArrayList();
    private final ObservableList<String> filiereNames = FXCollections.observableArrayList();

    // ===== Init =====
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Colonnes table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colFiliere.setCellValueFactory(new PropertyValueFactory<>("filiereNom"));

        // Statut
        statutBox.setItems(FXCollections.observableArrayList("ACTIF", "SUSPENDU"));
        statutBox.setValue("ACTIF");

        // Filières
        loadFilieres();

        // Élèves
        loadEleves();

        // Sélection dans la table
        eleveTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> remplirFormulaire(newVal)
        );
    }

    // ===== LOAD =====
    private void loadFilieres() {
        try {
            List<Filiere> filieres = filiereDAO.getAllFilieres();
            filiereNames.clear();
            for (Filiere f : filieres) {
                System.out.println("Filiere trouvée: " + f.getNom()); // debug
                filiereNames.add(f.getNom());
            }
            filiereBox.setItems(filiereNames);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les filières");
            e.printStackTrace();
        }
    }


    @FXML
    private void loadEleves() {
        try {
            eleveList.setAll(eleveDAO.getAllEleves());
            eleveTable.setItems(eleveList);
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement impossible");
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ma/tp/gestionacademique/main.fxml")
            );
            Parent dashboard = loader.load();
            eleveTable.getScene().setRoot(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // ===== ADD =====
    @FXML
    private void handleAdd() {
        if (matriculeField.getText().isEmpty() || nomField.getText().isEmpty() || filiereBox.getValue() == null) {
            showAlert("Validation", "Matricule, Nom et Filière obligatoires");
            return;
        }

        Eleve e = new Eleve();
        e.setMatricule(matriculeField.getText());
        e.setNom(nomField.getText());
        e.setPrenom(prenomField.getText());
        e.setEmail(emailField.getText());
        e.setStatut(statutBox.getValue() != null ? statutBox.getValue() : "ACTIF");
        e.setFiliereId(getFiliereIdByName(filiereBox.getValue()));



        try {
            // ✅ Ajouter l'élève avec tous les cours sélectionnés
            eleveDAO.addEleve(e);


            // ✅ Rafraîchir la table et vider le formulaire
            loadEleves();
            clearForm();
        } catch (SQLException ex) {
            showAlert("Erreur", "Ajout impossible : " + ex.getMessage());
        }
    }




    // ===== UPDATE =====
    @FXML
    private void handleUpdate() {
        Eleve selected = eleveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Sélectionnez un élève");
            return;
        }
        if (filiereBox.getValue() == null) {
            showAlert("Validation", "Sélectionnez une filière");
            return;
        }

        selected.setMatricule(matriculeField.getText());
        selected.setNom(nomField.getText());
        selected.setPrenom(prenomField.getText());
        selected.setEmail(emailField.getText());
        selected.setStatut(statutBox.getValue());
        selected.setFiliereId(getFiliereIdByName(filiereBox.getValue()));


        try {
            eleveDAO.updateEleve(selected);

            loadEleves();
            clearForm();
        } catch (SQLException ex) {
            showAlert("Erreur", "Modification impossible : " + ex.getMessage());
        }
    }

    // ===== DELETE =====
    @FXML
    private void handleDelete() {
        Eleve selected = eleveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Sélectionnez un élève");
            return;
        }
        if ("ACTIF".equals(selected.getStatut())) {
            showAlert("Règle métier", "Impossible de supprimer un élève ACTIF");
            return;
        }
        try {
            eleveDAO.deleteEleve(selected.getId());
            loadEleves();
            clearForm();
        } catch (SQLException e) {
            showAlert("Erreur", "Suppression impossible");
        }
    }

    // ===== Utils =====
    private void remplirFormulaire(Eleve e) {
        if (e != null) {
            matriculeField.setText(e.getMatricule());
            nomField.setText(e.getNom());
            prenomField.setText(e.getPrenom());
            emailField.setText(e.getEmail());
            statutBox.setValue(e.getStatut());
            filiereBox.setValue(getFiliereNameById(e.getFiliereId()));
        }
    }

    private void clearForm() {
        matriculeField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        statutBox.setValue("ACTIF");
        filiereBox.setValue(null);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ===== Conversion Filière =====
    private int getFiliereIdByName(String nomFiliere) {
        try {
            for (Filiere f : filiereDAO.getAllFilieres()) {
                if (f.getNom().equals(nomFiliere)) {
                    return f.getId();
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de récupérer l'ID de la filière");
        }
        return 0;
    }

    private String getFiliereNameById(int id) {
        try {
            for (Filiere f : filiereDAO.getAllFilieres()) {
                if (f.getId() == id) {
                    return f.getNom();
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de récupérer le nom de la filière");
        }
        return "";
    }

    @FXML
    private void openDossier() {
        Eleve selected = eleveTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un élève");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ma/tp/gestionacademique/dossier_administratif_form.fxml")
            );


            Stage stage = new Stage();
            stage.setTitle("Dossier Administratif");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            DossierAdministratifController controller = loader.getController();
            controller.initData(selected.getId(), selected.getNom() + " " + selected.getPrenom());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le dossier administratif");
        }
    }

    @FXML
    private void openDossierFromDashboard() {
        Eleve selected = eleveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un élève");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ma/tp/gestionacademique/dossier_administratif_read.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Dossier Administratif");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            DossierAdministratifReadController controller = loader.getController();
            controller.initForDashboard();

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le dossier administratif");
        }
    }


}
