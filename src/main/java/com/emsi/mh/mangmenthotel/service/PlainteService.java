package com.emsi.mh.mangmenthotel.service;

import com.emsi.mh.mangmenthotel.dao.PlainteDAO;
import com.emsi.mh.mangmenthotel.model.Plainte;
import java.util.List;

public class PlainteService {
    private final PlainteDAO plainteDAO = new PlainteDAO();

    public void addPlainte(Plainte plainte) {
        plainteDAO.create(plainte);
    }

    public List<Plainte> getAllPlaintes() {
        return plainteDAO.findAll();
    }

    public void updatePlainte(Plainte plainte) {
        plainteDAO.update(plainte);
    }

    public void deletePlainte(Plainte plainte) {
        plainteDAO.delete(plainte);
    }
}
