package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.model.Chambre;
import com.emsi.mh.mangmenthotel.enums.StatutChambre;
import com.emsi.mh.mangmenthotel.enums.TypeChambre;
import com.emsi.mh.mangmenthotel.service.ChambreService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ChambreController {

    @FXML
    private TextField numChambreField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<TypeChambre> typeComboBox;
    @FXML
    private ComboBox<StatutChambre> statusComboBox;

    @FXML
    private TableView<Chambre> chambreTable;
    @FXML
    private TableColumn<Chambre, Long> idColumn;
    @FXML
    private TableColumn<Chambre, String> numColumn;
    @FXML
    private TableColumn<Chambre, TypeChambre> typeColumn;
    @FXML
    private TableColumn<Chambre, Double> priceColumn;
    @FXML
    private TableColumn<Chambre, StatutChambre> statusColumn;

    private final ChambreService chambreService = new ChambreService();
    private ObservableList<Chambre> chambreList;

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(TypeChambre.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(StatutChambre.values()));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numColumn.setCellValueFactory(new PropertyValueFactory<>("numChambre"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prixParNuit"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        loadChambres();

        chambreTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                numChambreField.setText(newVal.getNumChambre());
                priceField.setText(String.valueOf(newVal.getPrixParNuit()));
                locationField.setText(newVal.getLocalisation());
                typeComboBox.setValue(newVal.getType());
                statusComboBox.setValue(newVal.getStatut());
            }
        });
    }


    private void loadChambres() {

        Task<ObservableList<Chambre>> task = new Task<>() {
            @Override
            protected ObservableList<Chambre> call() {
                return FXCollections.observableArrayList(
                        chambreService.getAllChambres());
            }
        };

        task.setOnSucceeded(e -> {
            chambreTable.setItems(task.getValue());
        });

        task.setOnFailed(e -> {
            showAlert("Error", "Load Failed", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        try {
            Chambre chambre = Chambre.builder()
                    .numChambre(numChambreField.getText())
                    .prixParNuit(Double.parseDouble(priceField.getText()))
                    .localisation(locationField.getText())
                    .type(typeComboBox.getValue())
                    .statut(statusComboBox.getValue())
                    .build();
            chambreService.addChambre(chambre);
            loadChambres();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Invalid Input", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Chambre selected = chambreTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setNumChambre(numChambreField.getText());
                selected.setPrixParNuit(Double.parseDouble(priceField.getText()));
                selected.setLocalisation(locationField.getText());
                selected.setType(typeComboBox.getValue());
                selected.setStatut(statusComboBox.getValue());

                chambreService.updateChambre(selected);
                loadChambres();
                clearFields();
            } catch (Exception e) {
                showAlert("Error", "Update Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        Chambre selected = chambreTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            chambreService.deleteChambre(selected);
            loadChambres();
            clearFields();
        }
    }

    private void clearFields() {
        numChambreField.clear();
        priceField.clear();
        locationField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        chambreTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
