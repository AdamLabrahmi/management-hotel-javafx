package com.emsi.mh.mangmenthotel.util;

import com.emsi.mh.mangmenthotel.model.Chambre;
import com.emsi.mh.mangmenthotel.model.Client;
import com.emsi.mh.mangmenthotel.model.Gestionnaire;
import com.emsi.mh.mangmenthotel.enums.*;
import com.emsi.mh.mangmenthotel.service.ChambreService;
import com.emsi.mh.mangmenthotel.service.PersonneService;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DatabaseInitializer {

    public static void initialize() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(p) FROM Personne p", Long.class).getSingleResult();
            if (count == 0) {
                System.out.println("Seeding database...");
                em.getTransaction().begin();

                // Admin
                Gestionnaire admin = Gestionnaire.builder()
                        .nom("Admin User")
                        .telephone("0000000000")
                        .email("admin@hotel.com")
                        .password("admin")
                        .build();
                em.persist(admin);

                // Client
                Client client = Client.builder()
                        .nom("John Doe")
                        .telephone("123456789")
                        .email("client@test.com")
                        .password("client")
                        .build();
                em.persist(client);

                // Rooms
                for (int i = 1; i <= 5; i++) {
                    Chambre chambre = Chambre.builder()
                            .numChambre("10" + i)
                            .type(TypeChambre.SIMPLE)
                            .localisation("Floor 1")
                            .statut(StatutChambre.LIBRE)
                            .prixParNuit(100.0 * i)
                            .build();
                    em.persist(chambre);
                }

                em.getTransaction().commit();
                System.out.println("Database seeded.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
