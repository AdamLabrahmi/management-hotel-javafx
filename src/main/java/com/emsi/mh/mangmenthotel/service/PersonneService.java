package com.emsi.mh.mangmenthotel.service;

import com.emsi.mh.mangmenthotel.dao.IDao;
import com.emsi.mh.mangmenthotel.dao.GenericDAOImpl;
import com.emsi.mh.mangmenthotel.model.Personne;
import com.emsi.mh.mangmenthotel.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class PersonneService {

    public Personne authenticate(String email, String password) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Personne personne = em.createQuery("SELECT p FROM Personne p WHERE p.email = :email", Personne.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (personne != null && personne.getPassword().equals(password)) {
                return personne;
            }
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return null;
    }
}
