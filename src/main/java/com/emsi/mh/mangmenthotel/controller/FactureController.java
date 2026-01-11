package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.model.Client;
import com.emsi.mh.mangmenthotel.model.Facture;
import com.emsi.mh.mangmenthotel.model.Personne;
import com.emsi.mh.mangmenthotel.enums.StatutFacture;
import com.emsi.mh.mangmenthotel.service.FactureService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class FactureController {

    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private ComboBox<StatutFacture> statusComboBox;

    @FXML
    private javafx.scene.layout.VBox adminBox;
    @FXML
    private javafx.scene.layout.VBox clientBox;
    @FXML
    private ComboBox<com.emsi.mh.mangmenthotel.model.Reservation> reservationCombo; // Client View
    @FXML
    private ComboBox<com.emsi.mh.mangmenthotel.model.Reservation> pendingReservationsCombo; // Admin View
    @FXML
    private Label totalLabel;

    @FXML
    private TableView<Facture> factureTable;
    @FXML
    private TableColumn<Facture, Long> idColumn;
    @FXML
    private TableColumn<Facture, String> numColumn;
    @FXML
    private TableColumn<Facture, Double> amountColumn;
    @FXML
    private TableColumn<Facture, LocalDate> dateColumn;
    @FXML
    private TableColumn<Facture, StatutFacture> statusColumn;

    private final FactureService factureService = new FactureService();
    private final com.emsi.mh.mangmenthotel.service.ReservationService reservationService = new com.emsi.mh.mangmenthotel.service.ReservationService();
    private Personne currentUser;

    public void initSession(Personne user) {
        this.currentUser = user;

        if (currentUser instanceof Client) {
            // Vue Client
            if (adminBox != null) {
                adminBox.setVisible(false);
                adminBox.setManaged(false);
            }
            if (clientBox != null) {
                clientBox.setVisible(true);
                clientBox.setManaged(true);
            }
            loadClientReservations();
        } else {
            // Vue Admin
            if (clientBox != null) {
                clientBox.setVisible(false);
                clientBox.setManaged(false);
            }
            if (adminBox != null) {
                adminBox.setVisible(true);
                adminBox.setManaged(true);
            }
            loadFactures();
            loadPendingReservations();
        }
    }

    @FXML
    public void initialize() {
        // Set Converters for ComboBoxes
        javafx.util.StringConverter<com.emsi.mh.mangmenthotel.model.Reservation> converter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(com.emsi.mh.mangmenthotel.model.Reservation r) {
                if (r == null)
                    return "";
                if (r.getClient() != null) {
                    return "Client: " + r.getClient().getNom() + " | Chambre: " + r.getChambre().getNumChambre() + " | "
                            + r.getDateArrivee() + " -> " + r.getDateDepart();
                }
                return "Res #" + r.getId();
            }

            @Override
            public com.emsi.mh.mangmenthotel.model.Reservation fromString(String string) {
                return null;
            }
        };

        if (reservationCombo != null)
            reservationCombo.setConverter(converter);
        if (pendingReservationsCombo != null)
            pendingReservationsCombo.setConverter(converter);

        if (idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            numColumn.setCellValueFactory(new PropertyValueFactory<>("numFacture"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEmission"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        }

        if (factureTable != null) {
            factureTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && statusComboBox != null) {
                    statusComboBox.setValue(newVal.getStatut());
                }
            });
        }

        if (statusComboBox != null) {
            statusComboBox.setItems(FXCollections.observableArrayList(StatutFacture.values()));
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Facture selected = factureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une facture.");
            return;
        }

        StatutFacture newStatus = statusComboBox.getValue();
        if (newStatus == null) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }

        try {
            selected.setStatut(newStatus);
            factureService.updateFacture(selected);
            showAlert("Succès", "Statut mis à jour avec succès.");
            loadFactures();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    private void loadFactures() {
        Task<ObservableList<Facture>> task = new Task<>() {
            @Override
            protected ObservableList<Facture> call() {
                return FXCollections.observableArrayList(factureService.getAllFactures());
            }
        };
        task.setOnSucceeded(e -> factureTable.setItems(task.getValue()));
        new Thread(task).start();
    }

    private void loadClientReservations() {
        Task<ObservableList<com.emsi.mh.mangmenthotel.model.Reservation>> task = new Task<>() {
            @Override
            protected ObservableList<com.emsi.mh.mangmenthotel.model.Reservation> call() {
                java.util.List<com.emsi.mh.mangmenthotel.model.Reservation> all = reservationService
                        .getAllReservations();
                java.util.List<com.emsi.mh.mangmenthotel.model.Reservation> clientRes = all.stream()
                        .filter(r -> r.getClient().getId().equals(currentUser.getId()))
                        .filter(r -> r.getFacture() != null)
                        .collect(java.util.stream.Collectors.toList());
                return FXCollections.observableArrayList(clientRes);
            }
        };
        task.setOnSucceeded(e -> reservationCombo.setItems(task.getValue()));
        new Thread(task).start();
    }

    private void loadPendingReservations() {
        Task<ObservableList<com.emsi.mh.mangmenthotel.model.Reservation>> task = new Task<>() {
            @Override
            protected ObservableList<com.emsi.mh.mangmenthotel.model.Reservation> call() {
                java.util.List<com.emsi.mh.mangmenthotel.model.Reservation> all = reservationService
                        .getAllReservations();
                java.util.List<com.emsi.mh.mangmenthotel.model.Reservation> pending = all.stream()
                        .filter(r -> r.getFacture() == null)
                        .collect(java.util.stream.Collectors.toList());
                return FXCollections.observableArrayList(pending);
            }
        };
        task.setOnSucceeded(e -> {
            if (pendingReservationsCombo != null) {
                pendingReservationsCombo.setItems(task.getValue());
            }
        });
        new Thread(task).start();
    }

    @FXML
    private void handleReservationSelection() {
        com.emsi.mh.mangmenthotel.model.Reservation selected = reservationCombo.getValue();
        if (selected != null && selected.getFacture() != null) {
            totalLabel.setText(selected.getFacture().getMontantTotal() + " DHS");
        } else {
            totalLabel.setText("0.0 DHS");
        }
    }

    @FXML
    private void handleGenerate() {
        com.emsi.mh.mangmenthotel.model.Reservation selected = pendingReservationsCombo.getValue();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une réservation.");
            return;
        }

        try {
            // Auto-calculation
            long days = java.time.temporal.ChronoUnit.DAYS.between(selected.getDateArrivee(), selected.getDateDepart());
            if (days <= 0)
                days = 1;

            Double price = selected.getChambre().getPrixParNuit();
            Double total = days * price;

            selected.setMontantTotal(total);

            Facture facture = Facture.builder()
                    .numFacture("FAC-" + System.currentTimeMillis())
                    .dateEmission(LocalDate.now())
                    .montantTotal(total)
                    .statut(StatutFacture.IMPAYEE)
                    .reservation(selected)
                    .build();

            selected.setFacture(facture);
            reservationService.updateReservation(selected);

            showAlert("Succès", "Facture générée avec succès ! Montant: " + total);
            loadFactures();
            loadPendingReservations(); // Remove from list

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Echec de la génération : " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Facture selected = factureTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            factureService.deleteFacture(selected);
            loadFactures();
            loadPendingReservations(); // Might become pending again if we delink logic exists, strictly delete facture
                                       // here.
            // If delete facture, reservation.facture becomes null? Depends on cascade.
            // Generally safer to refresh.
        } else {
            showAlert("Erreur", "Veuillez sélectionner une facture à supprimer.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.contains("Erreur"))
            alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
