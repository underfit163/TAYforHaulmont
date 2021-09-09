package com.haulmont.testtask.dao;

import com.haulmont.testtask.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Абстрактный класс для работы с базой данных
 * @param <T> параметр для работы с разными сущностями
 */
public abstract class AbstractDao<T> {
    public void save(T t) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(t);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(T t) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(t);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(T t) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(t);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract T findByID(int id);

    public abstract void deleteByID(int id);

    public abstract List<T> selectAll();


}
