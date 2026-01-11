package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.model.*;
import com.emsi.mh.mangmenthotel.enums.StatutReservation;
import com.emsi.mh.mangmenthotel.service.ChambreService;
import com.emsi.mh.mangmenthotel.service.ClientService;
import com.emsi.mh.mangmenthotel.service.ReservationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ReservationController {

    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private ComboBox<Chambre> chambreComboBox;
    @FXML
    private DatePicker arriveeDatePicker;
    @FXML
    private DatePicker departDatePicker;
    @FXML
    private TextField guestsField;
    @FXML
    private ComboBox<StatutReservation> statusComboBox;

    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, Long> idColumn;
    @FXML
    private TableColumn<Reservation, String> clientColumn;
    @FXML
    private TableColumn<Reservation, String> roomColumn;
    @FXML
    private TableColumn<Reservation, LocalDate> arrivalColumn;
    @FXML
    private TableColumn<Reservation, LocalDate> departureColumn;
    @FXML
    private TableColumn<Reservation, StatutReservation> statusColumn;

    private final ReservationService reservationService = new ReservationService();
    private final ClientService clientService = new ClientService();
    private final ChambreService chambreService = new ChambreService();

    private Personne currentUser;

    public void initSession(Personne user) {
        this.currentUser = user;
        setupAccessControl();
        loadReservations();
    }

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(StatutReservation.values()));
        loadClients();
        loadChambres();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getClient().getNom()));
        roomColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getChambre().getNumChambre()));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("dateArrivee"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Custom display for rooms in ComboBox
        chambreComboBox.setConverter(new javafx.util.StringConverter<Chambre>() {
            @Override
            public String toString(Chambre chambre) {
                return chambre == null ? ""
                        : "Chambre N°" + chambre.getNumChambre() + " (" + chambre.getType() + ")";
            }

            @Override
            public Chambre fromString(String string) {
                return null;
            }
        });

        // Listener for table selection
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clientComboBox.setValue(newSelection.getClient());
                chambreComboBox.setValue(newSelection.getChambre());
                arriveeDatePicker.setValue(newSelection.getDateArrivee());
                departDatePicker.setValue(newSelection.getDateDepart());
                guestsField.setText(String.valueOf(newSelection.getNombrePersonnes()));
                statusComboBox.setValue(newSelection.getStatut());
            }
        });
    }

    private void setupAccessControl() {
        if (currentUser instanceof Client) {
            // Pre-select current client based on ID
            for (Client c : clientComboBox.getItems()) {
                if (c.getId() != null && c.getId().equals(currentUser.getId())) {
                    clientComboBox.getSelectionModel().select(c);
                    break;
                }
            }
            clientComboBox.setDisable(true);
            clientComboBox.setStyle("-fx-opacity: 1.0; -fx-text-fill: black;");

            // Client cannot choose status, it defaults to EN_ATTENTE
            statusComboBox.setDisable(true);
            statusComboBox.setStyle("-fx-opacity: 1.0; -fx-text-fill: black;");
        }
    }

    private void loadReservations() {
        Task<ObservableList<Reservation>> task = new Task<>() {
            @Override
            protected ObservableList<Reservation> call() {
                ObservableList<Reservation> all = FXCollections
                        .observableArrayList(reservationService.getAllReservations());
                if (currentUser instanceof Client) {
                    return all.stream()
                            .filter(r -> r.getClient().getId().equals(currentUser.getId()))
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
                }
                return all;
            }
        };

        task.setOnSucceeded(e -> {
            reservationTable.setItems(task.getValue());
        });

        task.setOnFailed(e -> {
            showAlert("Error", "Failed to load reservations: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void loadClients() {
        clientComboBox.setItems(FXCollections.observableArrayList(clientService.getAllClients()));
    }

    private void loadChambres() {
        Task<ObservableList<Chambre>> task = new Task<>() {
            @Override
            protected ObservableList<Chambre> call() {
                return chambreService.getAllChambres().stream()
                        .filter(c -> c.getStatut() == com.emsi.mh.mangmenthotel.enums.StatutChambre.LIBRE)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            }
        };
        task.setOnSucceeded(e -> chambreComboBox.setItems(task.getValue()));
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        try {
            Client client = clientComboBox.getValue();
            if (currentUser instanceof Client) {
                client = (Client) currentUser; // Force current user
            }

            // Input Validation
            if (client == null) {
                showAlert("Erreur de validation", "Veuillez sélectionner un client.");
                return;
            }
            if (chambreComboBox.getValue() == null) {
                showAlert("Erreur de validation", "Veuillez sélectionner une chambre.");
                return;
            }
            if (arriveeDatePicker.getValue() == null || departDatePicker.getValue() == null) {
                showAlert("Erreur de validation", "Veuillez sélectionner les dates d'arrivée et de départ.");
                return;
            }
            if (departDatePicker.getValue().isBefore(arriveeDatePicker.getValue())) {
                showAlert("Erreur de validation", "La date de départ doit être après la date d'arrivée.");
                return;
            }
            if (guestsField.getText() == null || guestsField.getText().trim().isEmpty()) {
                showAlert("Erreur de validation", "Veuillez entrer le nombre d'invités.");
                return;
            }

            int guests;
            try {
                guests = Integer.parseInt(guestsField.getText().trim());
                if (guests <= 0) {
                    showAlert("Erreur de validation", "Le nombre d'invités doit être positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de validation", "Le champ invités doit être un nombre valide.");
                return;
            }

            StatutReservation status = statusComboBox.getValue();
            if (currentUser instanceof Client) {
                status = StatutReservation.EN_ATTENTE;
            } else if (status == null) {
                showAlert("Erreur de validation", "Veuillez sélectionner un statut.");
                return;
            }

            Reservation reservation = Reservation.builder()
                    .client(client)
                    .chambre(chambreComboBox.getValue())
                    .dateArrivee(arriveeDatePicker.getValue())
                    .dateDepart(departDatePicker.getValue())
                    .nombrePersonnes(guests)
                    .statut(status)
                    .montantTotal(0.0) // Sera calculé par le service
                    .build();
            reservationService.addReservation(reservation);
            loadReservations();

            // Clear fields/reset selection if needed
            if (!(currentUser instanceof Client)) {
                // If admin, maybe reset client selection or other fields
            }
            loadChambres(); // Refresh room list (status might have changed)

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleDelete() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reservationService.deleteReservation(selected);
            loadReservations();
        } else {
            showAlert("Selection Error", "Please select a reservation to delete.");
        }
    }

    @FXML
    private void handleUpdate() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a reservation to update.");
            return;
        }

        try {
            // Update fields
            selected.setStatut(statusComboBox.getValue());
            selected.setChambre(chambreComboBox.getValue());
            selected.setDateArrivee(arriveeDatePicker.getValue());
            selected.setDateDepart(departDatePicker.getValue());
            selected.setNombrePersonnes(Integer.parseInt(guestsField.getText()));

            reservationService.updateReservation(selected);
            loadReservations();
            loadChambres(); // Refresh in case status changed room availability

            showAlert("Success", "Reservation updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Update failed: " + e.getMessage());
        }
    }

}
