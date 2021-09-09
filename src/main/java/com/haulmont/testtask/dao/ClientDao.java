package com.haulmont.testtask.dao;


import com.haulmont.testtask.entities.Bank;
import com.haulmont.testtask.entities.Client;
import com.haulmont.testtask.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Класс для работы с сущностью Client
 */
public class ClientDao extends AbstractDao<Client> {
    @Override
    public Client findByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Client.class, id);
        }
    }

    @Override
    public void deleteByID(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.get(Client.class, id));
            transaction.commit();
        }
    }

    @Override
    public List<Client> selectAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return (List<Client>) session.createQuery("From Client c JOIN FETCH c.fkBank order by c.idClient").list();
        }
    }

    public void insertClient(int bankID, Client client) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Bank bank = session.get(Bank.class, bankID);
            bank.addClient(client);
            session.update(bank);
            session.save(client);
            transaction.commit();
        }
    }
}
