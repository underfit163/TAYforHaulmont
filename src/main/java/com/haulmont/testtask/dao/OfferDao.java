package com.haulmont.testtask.dao;

import com.haulmont.testtask.entities.Client;
import com.haulmont.testtask.entities.Credit;
import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Класс для работы с сущностью Offer
 */
public class OfferDao extends AbstractDao<Offer> {
    @Override
    public Offer findByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Offer.class, id);
        }
    }

    @Override
    public void deleteByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.get(Offer.class, id));
            transaction.commit();
        }
    }

    @Override
    public List<Offer> selectAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery(
                    "From Offer o JOIN fetch o.fkClient JOIN fetch o.fkCredit JOIN fetch o.fkCredit.fkBank JOIN fetch o.fkClient.fkBank order by o.idOffer",Offer.class).list();
        }
    }

    public void insertOffer(int clientID, int creditID, Offer offer) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientID);
            Credit credit = session.get(Credit.class, creditID);

            client.addOffer(offer);
            credit.addOffer(offer);

            session.update(client);
            session.update(credit);
            session.save(offer);
            transaction.commit();
        }
    }
}
