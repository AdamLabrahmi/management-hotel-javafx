package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.HelloApplication;
import com.emsi.mh.mangmenthotel.model.Client;
import com.emsi.mh.mangmenthotel.service.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    private final ClientService clientService = new ClientService();

    @FXML
    private void handleRegister() {
        if (!validateInput())
            return;

        try {
            Client client = Client.builder()
                    .nom(nomField.getText())
                    .prenom(prenomField.getText())
                    .email(emailField.getText())
                    .telephone(telephoneField.getText())
                    .password(passwordField.getText())
                    .build();

            clientService.addClient(client);

            showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                    "You can now login with your credentials.");
            goBackToLogin();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        goBackToLogin();
    }

    private boolean validateInput() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return false;
        }
        return true;
    }

    private void goBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("/com/emsi/mh/mangmenthotel/view/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setTitle("Hotel Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
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
