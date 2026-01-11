package com.emsi.mh.mangmenthotel.service;

import com.emsi.mh.mangmenthotel.dao.ChambreDAO;
import com.emsi.mh.mangmenthotel.model.Chambre;
import java.util.List;

public class ChambreService {
    private final ChambreDAO chambreDAO = new ChambreDAO();

    public void addChambre(Chambre chambre) {
        chambreDAO.create(chambre);
    }

    public List<Chambre> getAllChambres() {
        return chambreDAO.findAll();
    }

    public void updateChambre(Chambre chambre) {
        chambreDAO.update(chambre);
    }

    public void deleteChambre(Chambre chambre) {
        chambreDAO.delete(chambre);
    }
}
