package com.emsi.mh.mangmenthotel.service;

import com.emsi.mh.mangmenthotel.dao.FactureDAO;
import com.emsi.mh.mangmenthotel.model.Facture;
import java.util.List;

public class FactureService {
    private final FactureDAO factureDAO = new FactureDAO();

    public void addFacture(Facture facture) {
        factureDAO.create(facture);
    }

    public List<Facture> getAllFactures() {
        return factureDAO.findAll();
    }

    public void updateFacture(Facture facture) {
        factureDAO.update(facture);
    }

    public void deleteFacture(Facture facture) {
        factureDAO.delete(facture);
    }
}
