package ma.tp.gestionacademique.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ma.tp.gestionacademique.dao.CoursDAO;
import ma.tp.gestionacademique.model.Cours;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CoursController implements Initializable {

    @FXML
    private TextField codeField;

    @FXML
    private TextField intituleField;

    @FXML
    private TableView<Cours> coursTable;

    @FXML
    private TableColumn<Cours, Integer> colId;

    @FXML
    private TableColumn<Cours, String> colCode;

    @FXML
    private TableColumn<Cours, String> colIntitule;

    private final CoursDAO coursDAO = new CoursDAO();
    private final ObservableList<Cours> coursList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colIntitule.setCellValueFactory(new PropertyValueFactory<>("intitule"));

        loadCours();

        // Sélection ligne → formulaire
        coursTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        codeField.setText(newVal.getCode());
                        intituleField.setText(newVal.getIntitule());
                    }
                }
        );
    }

    @FXML
    private void handleAdd() {
        try {
            Cours c = new Cours();
            c.setCode(codeField.getText());
            c.setIntitule(intituleField.getText());

            coursDAO.addCours(c);
            loadCours();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        Cours selected = coursTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            selected.setCode(codeField.getText());
            selected.setIntitule(intituleField.getText());

            coursDAO.updateCours(selected);
            loadCours();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Cours selected = coursTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            coursDAO.deleteCours(selected.getId());
            loadCours();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadCours() {
        try {
            coursList.setAll(coursDAO.getAllCours());
            coursTable.setItems(coursList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        codeField.clear();
        intituleField.clear();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ma/tp/gestionacademique/main.fxml")
            );
            Parent root = loader.load();
            coursTable.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

