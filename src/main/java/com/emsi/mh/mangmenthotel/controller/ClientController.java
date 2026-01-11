package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.model.Client;
import com.emsi.mh.mangmenthotel.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, Long> idColumn;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;

    private final ClientService clientService = new ClientService();
    private ObservableList<Client> clientList;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadClients();


        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getNom());
                phoneField.setText(newSelection.getTelephone());
                emailField.setText(newSelection.getEmail());
                passwordField.setText(newSelection.getPassword());
            }
        });
    }

    private void loadClients() {
        Task<ObservableList<Client>> task = new Task<>() {
            @Override
            protected ObservableList<Client> call() {
                return FXCollections.observableArrayList(clientService.getAllClients());
            }
        };

        task.setOnSucceeded(e -> {
            clientList = task.getValue();
            clientTable.setItems(clientList);
        });

        task.setOnFailed(e -> {
            showAlert("Error", "Load Failed", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Validation Error", "Please fill all fields.");
            return;
        }

        try {
            Client client = Client.builder()
                    .nom(name)
                    .telephone(phone)
                    .email(email)
                    .password(password)
                    .build();
            clientService.addClient(client);
            loadClients();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Creation Failed", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No Selection", "Please select a client to update.");
            return;
        }

        selected.setNom(nameField.getText());
        selected.setTelephone(phoneField.getText());
        selected.setEmail(emailField.getText());
        selected.setPassword(passwordField.getText());

        try {
            clientService.updateClient(selected);
            loadClients();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Update Failed", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No Selection", "Please select a client to delete.");
            return;
        }

        clientService.deleteClient(selected);
        loadClients();
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        passwordField.clear();
        clientTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
