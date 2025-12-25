package ma.tp.gestionacademique.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.stream.Collectors;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.tp.gestionacademique.dao.FiliereDAO;
import ma.tp.gestionacademique.model.Filiere;
import ma.tp.gestionacademique.dao.CoursDAO;
import ma.tp.gestionacademique.model.Cours;

import java.sql.SQLException;
import java.util.List;


public class FiliereController {

    @FXML private TextField codeField;
    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private ListView<Cours> coursListView;

    @FXML private TableView<Filiere> filiereTable;
    @FXML private TableColumn<Filiere, Integer> colId;
    @FXML private TableColumn<Filiere, String> colCode;
    @FXML private TableColumn<Filiere, String> colNom;
    @FXML private TableColumn<Filiere, String> colDescription;
    @FXML private TableColumn<Filiere, String> colCours;

    private final FiliereDAO filiereDAO = new FiliereDAO();
    private final CoursDAO coursDAO = new CoursDAO();
    private final ObservableList<Filiere> filiereList = FXCollections.observableArrayList();
    private final ObservableList<Cours> coursList = FXCollections.observableArrayList();

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCours.setCellValueFactory(cellData -> {

            Filiere filiere = cellData.getValue();

            List<Cours> cours = filiereDAO.getCoursByFiliereId(filiere.getId());

            String coursText = cours.stream()
                    .map(Cours::getIntitule)
                    .collect(Collectors.joining(", "));

            return new SimpleStringProperty(coursText);
        });
        coursListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        loadCours();
        loadFilieres();

        filiereTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> remplirFormulaire(newVal));
    }

    private void loadCours() {
        try {
            List<Cours> list = coursDAO.getAllCours();
            coursList.setAll(list);
            coursListView.setItems(coursList);
            coursListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les cours");
        }
    }
    @FXML
    public void loadFilieres() {
        try {
            filiereList.setAll(filiereDAO.getAllFilieres());
            filiereTable.setItems(filiereList);
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement impossible");
        }
    }


    private void remplirFormulaire(Filiere f) {
        if (f != null) {
            codeField.setText(f.getCode());
            nomField.setText(f.getNom());
            descriptionField.setText(f.getDescription());

            coursListView.getSelectionModel().clearSelection();

            List<Cours> coursAssocies = filiereDAO.getCoursByFiliereId(f.getId());

            for (Cours c : coursAssocies) {
                for (int i = 0; i < coursList.size(); i++) {
                    if (coursList.get(i).getId() == c.getId()) {
                        coursListView.getSelectionModel().select(i);
                    }
                }
            }
        }
    }



    private void clearForm() {
        codeField.clear();
        nomField.clear();
        descriptionField.clear();
        coursListView.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void loadAllCours() {
        try {
            List<Cours> allCours = coursDAO.getAllCours(); // méthode DAO pour récupérer tous les cours
            coursListView.setItems(FXCollections.observableArrayList(allCours));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // ===== Ajouter =====
    @FXML
    private void handleAdd() {

        if (codeField.getText().isEmpty() || nomField.getText().isEmpty()) {
            showAlert("Validation", "Code et Nom sont obligatoires");
            return;
        }

        try {
            String code = codeField.getText().trim();

            //  Règle métier : code unique
            if (filiereDAO.codeExiste(code, null)) {
                showAlert("Erreur métier", "Le code de la filière existe déjà");
                return;
            }

            Filiere f = new Filiere();
            f.setCode(code);
            f.setNom(nomField.getText());
            f.setDescription(descriptionField.getText());
            List<Cours> selectedCours = new ArrayList<>(coursListView.getSelectionModel().getSelectedItems());
            filiereDAO.addFiliereWithCours(f, selectedCours); // ajout filiere + cours
            loadFilieres();
            clearForm();

            showAlert("Succès", "Filière ajoutée avec succès");

        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }


    // ===== Modifier =====
    @FXML
    private void handleUpdate() {

        Filiere selected = filiereTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Sélection", "Sélectionnez une filière");
            return;
        }

        if (codeField.getText().isEmpty() || nomField.getText().isEmpty()) {
            showAlert("Validation", "Code et Nom sont obligatoires");
            return;
        }

        try {
            String code = codeField.getText().trim();

            // Règle métier : code unique
            if (filiereDAO.codeExiste(code, selected.getId())) {
                showAlert("Erreur métier", "Ce code est déjà utilisé");
                return;
            }

            // 🔹 Mise à jour filière
            selected.setCode(code);
            selected.setNom(nomField.getText());
            selected.setDescription(descriptionField.getText());

            filiereDAO.updateFiliere(selected);

            // 🔹 Mise à jour des cours associés
            List<Cours> selectedCours =
                    new ArrayList<>(coursListView.getSelectionModel().getSelectedItems());

            filiereDAO.updateFiliereCours(selected.getId(), selectedCours);

            loadFilieres();
            clearForm();

            showAlert("Succès", "Filière et cours mis à jour avec succès");

        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }




    // ===== Supprimer =====
    @FXML
    private void handleDelete() {
        Filiere selected = filiereTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Sélection", "Sélectionnez une filière");
            return;
        }

        try {
            boolean deleted = filiereDAO.deleteFiliere(selected.getId());
            if (!deleted) {
                showAlert("Règle métier", "Impossible de supprimer : filière utilisée");
            }
            loadFilieres();
            clearForm();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression");
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ma/tp/gestionacademique/main.fxml")
            );
            Parent root = loader.load();
            filiereTable.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}


