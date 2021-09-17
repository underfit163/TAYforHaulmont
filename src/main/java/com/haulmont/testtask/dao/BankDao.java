package com.haulmont.testtask.dao;

import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Класс для работы с сущностью Bank
 */
public class BankDao extends AbstractDao<Bank> {
    @Override
    public Bank findByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Bank.class, id);
        }
    }

    @Override
    public void deleteByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.get(Bank.class, id));
            transaction.commit();
        }
    }

    @Override
    public List<Bank> selectAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("From Bank order by idBank",Bank.class).list();
        }
    }
}
