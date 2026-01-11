package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.model.Personne;
import com.emsi.mh.mangmenthotel.model.Gestionnaire;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox sideBar;
    private Personne currentUser;

    @FXML
    private javafx.scene.control.Button btnClients;
    @FXML
    private javafx.scene.control.Button btnChambres;
    @FXML
    private javafx.scene.control.Button btnReservations;
    @FXML
    private javafx.scene.control.Button btnFactures;
    @FXML
    private javafx.scene.control.Button btnPlaintes;

    public void initSession(Personne user) {
        this.currentUser = user;
        if (!(user instanceof Gestionnaire)) {
            // Client view
            if (btnClients != null) {
                btnClients.setVisible(false);
                btnClients.setManaged(false);
            }
            if (btnChambres != null) {
                btnChambres.setVisible(false);
                btnChambres.setManaged(false);
            }
            loadView("reservation-view.fxml");
        } else {
            loadView("client-view.fxml");
        }
    }

    @FXML
    public void initialize() {
        // Wait for session init
    }

    @FXML
    private void showClients() {
        loadView("client-view.fxml");
    }

    @FXML
    private void showChambres() {
        loadView("chambre-view.fxml");
    }

    @FXML
    private void showReservations() {
        loadView("reservation-view.fxml");
    }

    @FXML
    private void showFactures() {
        loadView("facture-view.fxml");
    }

    @FXML
    private void showPlaintes() {
        loadView("plainte-view.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/emsi/mh/mangmenthotel/view/login-view.fxml"));
            Parent view = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) mainBorderPane.getScene().getWindow();
            stage.setTitle("Hotel Login");
            stage.setScene(new javafx.scene.Scene(view, 400, 500)); // Default login size
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emsi/mh/mangmenthotel/view/" + fxmlFile));
            Parent view = loader.load();

            // Inject session into sub-controller
            Object controller = loader.getController();
            if (controller instanceof ReservationController) {
                ((ReservationController) controller).initSession(currentUser);
            } else if (controller instanceof PlainteController) {
                ((PlainteController) controller).initSession(currentUser);
            } else if (controller instanceof FactureController) {
                ((FactureController) controller).initSession(currentUser);
            }

            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
