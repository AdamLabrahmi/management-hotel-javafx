package com.emsi.mh.mangmenthotel.service;

import com.emsi.mh.mangmenthotel.dao.ReservationDAO;
import com.emsi.mh.mangmenthotel.model.Reservation;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ChambreService chambreService = new ChambreService();

    public void addReservation(Reservation reservation) {

        if (reservation.getChambre() != null) {

            long days = java.time.temporal.ChronoUnit.DAYS.between(reservation.getDateArrivee(),
                    reservation.getDateDepart());
            if (days <= 0)
                days = 1;

            Double totalAmount = days * reservation.getChambre().getPrixParNuit();

            com.emsi.mh.mangmenthotel.model.Facture facture = com.emsi.mh.mangmenthotel.model.Facture.builder()
                    .numFacture("FAC-" + System.currentTimeMillis())
                    .dateEmission(java.time.LocalDate.now())
                    .montantTotal(totalAmount)
                    .statut(com.emsi.mh.mangmenthotel.enums.StatutFacture.IMPAYEE)
                    .reservation(reservation)
                    .build();

            reservation.setFacture(facture);
            reservation.setMontantTotal(totalAmount);


            reservation.getChambre().setStatut(com.emsi.mh.mangmenthotel.enums.StatutChambre.OCCUPEE);
            chambreService.updateChambre(reservation.getChambre());
        }
        reservationDAO.create(reservation);
    }

    public List<Reservation> getAllReservations() {

        checkExpiredReservations();
        return reservationDAO.findAll();
    }

    public void updateReservation(Reservation reservation) {

        if (reservation.getStatut() == com.emsi.mh.mangmenthotel.enums.StatutReservation.ANNULEE ||
                reservation.getStatut() == com.emsi.mh.mangmenthotel.enums.StatutReservation.TERMINEE) {
            if (reservation.getChambre() != null) {
                reservation.getChambre().setStatut(com.emsi.mh.mangmenthotel.enums.StatutChambre.LIBRE);
                chambreService.updateChambre(reservation.getChambre());
            }
        }
        reservationDAO.update(reservation);
    }

    public void deleteReservation(Reservation reservation) {

        if (reservation.getChambre() != null) {
            reservation.getChambre().setStatut(com.emsi.mh.mangmenthotel.enums.StatutChambre.LIBRE);
            chambreService.updateChambre(reservation.getChambre());
        }
        reservationDAO.delete(reservation);
    }

    private void checkExpiredReservations() {
        List<Reservation> all = reservationDAO.findAll();
        java.time.LocalDate today = java.time.LocalDate.now();

        all.stream()
                .filter(r -> r.getDateDepart().isBefore(today)
                        && r.getStatut() != com.emsi.mh.mangmenthotel.enums.StatutReservation.TERMINEE)
                .forEach(r -> {
                    r.setStatut(com.emsi.mh.mangmenthotel.enums.StatutReservation.TERMINEE);
                    if (r.getChambre() != null) {
                        r.getChambre().setStatut(com.emsi.mh.mangmenthotel.enums.StatutChambre.LIBRE);
                        chambreService.updateChambre(r.getChambre());
                    }
                    reservationDAO.update(r);
                });
    }
}
