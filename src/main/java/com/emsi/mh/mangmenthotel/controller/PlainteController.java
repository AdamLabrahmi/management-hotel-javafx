package com.emsi.mh.mangmenthotel.controller;

import com.emsi.mh.mangmenthotel.model.*;
import com.emsi.mh.mangmenthotel.enums.StatutPlainte;
import com.emsi.mh.mangmenthotel.service.ClientService;
import com.emsi.mh.mangmenthotel.service.PlainteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.time.LocalDate;

public class PlainteController {

    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox<StatutPlainte> statusComboBox;

    @FXML
    private TableView<Plainte> plainteTable;
    @FXML
    private TableColumn<Plainte, Long> idColumn;
    @FXML
    private TableColumn<Plainte, String> clientColumn;
    @FXML
    private TableColumn<Plainte, String> descriptionColumn;
    @FXML
    private TableColumn<Plainte, StatutPlainte> statusColumn;
    @FXML
    private TableColumn<Plainte, LocalDate> dateColumn;

    private final PlainteService plainteService = new PlainteService();
    private final ClientService clientService = new ClientService();

    private Personne currentUser;

    public void initSession(Personne user) {
        this.currentUser = user;
        setupAccessControl();
        loadPlaintes();
    }

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(StatutPlainte.values()));
        loadClients();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getNom()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        clientComboBox.setConverter(new StringConverter<Client>() {
            @Override
            public String toString(Client client) {
                return client == null ? "" : client.getNom();
            }

            @Override
            public Client fromString(String string) {
                return null;
            }
        });
    }

    private void setupAccessControl() {
        if (currentUser instanceof Client) {
            // Pre-select current client and disable selection
            for (Client c : clientComboBox.getItems()) {
                if (c.getId().equals(currentUser.getId())) {
                    clientComboBox.getSelectionModel().select(c);
                    break;
                }
            }
            clientComboBox.setDisable(true);
        }
    }

    private void loadPlaintes() {
        Task<ObservableList<Plainte>> task = new Task<>() {
            @Override
            protected ObservableList<Plainte> call() {
                ObservableList<Plainte> list = FXCollections.observableArrayList(plainteService.getAllPlaintes());
                if (currentUser instanceof Client) {
                    list = list.filtered(p -> p.getClient().getId().equals(currentUser.getId()));
                }
                return list;
            }
        };
        task.setOnSucceeded(e -> plainteTable.setItems(task.getValue()));
        new Thread(task).start();
    }

    private void loadClients() {
        Task<ObservableList<Client>> task = new Task<>() {
            @Override
            protected ObservableList<Client> call() {
                return FXCollections.observableArrayList(clientService.getAllClients());
            }
        };
        task.setOnSucceeded(e -> clientComboBox.setItems(task.getValue()));
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        try {
            Client client = clientComboBox.getValue();
            if (currentUser instanceof Client) {
                client = (Client) currentUser;
            }

            Plainte plainte = Plainte.builder()
                    .client(client)
                    .description(descriptionField.getText())
                    .statut(statusComboBox.getValue())
                    .date(LocalDate.now())
                    .build();
            plainteService.addPlainte(plainte);
            loadPlaintes();
            descriptionField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Plainte selected = plainteTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            plainteService.deletePlainte(selected);
            loadPlaintes();
        }
    }
}
