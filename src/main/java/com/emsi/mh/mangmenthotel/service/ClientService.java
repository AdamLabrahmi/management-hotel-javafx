package com.emsi.mh.mangmenthotel.service;

import com.emsi.mh.mangmenthotel.dao.ClientDAO;
import com.emsi.mh.mangmenthotel.model.Client;
import java.util.List;

public class ClientService {
    private final ClientDAO clientDAO = new ClientDAO();

    public void addClient(Client client) {
        clientDAO.create(client);
    }

    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }

    public void updateClient(Client client) {
        clientDAO.update(client);
    }

    public void deleteClient(Client client) {
        clientDAO.delete(client);
    }
}
