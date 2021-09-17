package com.haulmont.testtask.dao;

import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Credit;
import com.haulmont.testtask.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Класс для работы с сущностью Credit
 */
public class CreditDao extends AbstractDao<Credit> {
    @Override
    public Credit findByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Credit.class, id);
        }
    }

    @Override
    public void deleteByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.get(Credit.class, id));
            transaction.commit();
        }
    }

    @Override
    public List<Credit> selectAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("From Credit c JOIN FETCH c.fkBank order by c.idCredit",Credit.class).list();
        }
    }

    public void insertCredit(int bankID, Credit credit) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Bank bank = session.get(Bank.class, bankID);
            bank.addCredit(credit);
            session.update(bank);
            session.save(credit);
            transaction.commit();
        }
    }
}
