package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.HelloApplication;
import com.emsi.mh.mangmenthotel.model.Personne;
import com.emsi.mh.mangmenthotel.service.ClientService;
import com.emsi.mh.mangmenthotel.service.PersonneService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private final PersonneService personneService = new PersonneService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        Personne user = personneService.authenticate(email, password);

        if (user != null) {
            loadDashboard(user);
        } else {
            showAlert("Login Failed", "Invalid credentials.");
        }
    }

    @FXML
    private void handleRegisterNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("/com/emsi/mh/mangmenthotel/view/register-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("Create Account");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboard(Personne user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("/com/emsi/mh/mangmenthotel/view/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Pass user to MainController if needed
            MainController controller = loader.getController();
            controller.initSession(user);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("Hotel Management System - " + user.getNom());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
