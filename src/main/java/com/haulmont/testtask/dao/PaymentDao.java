package com.haulmont.testtask.dao;

import com.haulmont.testtask.entities.Offer;
import com.haulmont.testtask.entities.Payment;
import com.haulmont.testtask.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Класс для работы с сущностью Payment
 */
public class PaymentDao extends AbstractDao<Payment> {
    @Override
    public Payment findByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Payment.class, id);
        }
    }

    @Override
    public void deleteByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.get(Payment.class, id));
            transaction.commit();
        }
    }

    @Override
    public List<Payment> selectAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("From Payment p join fetch p.fkOffer order by p.idPayment",Payment.class).list();
        }
    }

    public List<Payment> selectAllForOffer(Offer offer) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Query<Payment> q = session.createQuery("From Payment p join fetch p.fkOffer where p.fkOffer = :idParam order by p.idPayment",Payment.class);
            q.setParameter("idParam", offer);
            return q.list();
        }
    }

    public void insertPayment(int offerID, Payment payment) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Offer offer = session.get(Offer.class, offerID);
            offer.addPayment(payment);
            session.update(offer);
            session.save(payment);
            transaction.commit();
        }
    }
}
