package com.haulmont.testtask.utils;

import com.haulmont.testtask.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Класс для создания фабрики и открытия сессии для доступа к базе данных через Hibernate
 */
public class HibernateSessionFactory {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactory() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null)
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Bank.class);
                configuration.addAnnotatedClass(Client.class);
                configuration.addAnnotatedClass(Credit.class);
                configuration.addAnnotatedClass(Offer.class);
                configuration.addAnnotatedClass(Payment.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                System.out.println("Exception!" + e);
            }
        return sessionFactory;
    }
}
