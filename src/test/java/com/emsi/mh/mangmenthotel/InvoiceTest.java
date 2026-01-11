package com.emsi.mh.mangmenthotel;

import com.emsi.mh.mangmenthotel.model.Chambre;
import com.emsi.mh.mangmenthotel.model.Facture;
import com.emsi.mh.mangmenthotel.model.Reservation;
import com.emsi.mh.mangmenthotel.service.ReservationService;
import com.emsi.mh.mangmenthotel.enums.StatutFacture;
import com.emsi.mh.mangmenthotel.enums.StatutReservation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceTest {

    @Test
    public void testInvoiceGeneration() {
        ReservationService service = new ReservationService();

        Chambre chambre = Chambre.builder()
                .id(1L)
                .numChambre("101")
                .prixParNuit(100.0)
                .build();

        Reservation reservation = Reservation.builder()
                .dateArrivee(LocalDate.of(2023, 1, 1))
                .dateDepart(LocalDate.of(2023, 1, 5)) // 4 nights
                .chambre(chambre)
                .statut(StatutReservation.EN_ATTENTE)
                .build();

        try {
            service.addReservation(reservation);
        } catch (Exception e) {
            // DAO might fail due to database connection, but that's expected in isolation
            System.out.println("Ignored DAO exception: " + e.getMessage());
        }

        Facture facture = reservation.getFacture();
        assertNotNull(facture, "Facture should be generated");
        assertEquals(400.0, facture.getMontantTotal(), 0.01, "Total amount should be 4 * 100 = 400");
        assertEquals(StatutFacture.IMPAYEE, facture.getStatut(), "Status should be IMPAYEE");
        assertNotNull(facture.getNumFacture(), "Invoice number should be generated");
        assertEquals(reservation, facture.getReservation(), "Reservation should be linked");

        System.out.println("Invoice Verification Success: Amount=" + facture.getMontantTotal());
    }
}
