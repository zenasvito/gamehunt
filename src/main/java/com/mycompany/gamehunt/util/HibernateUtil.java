package com.mycompany.gamehunt.util;

import org.hibernate.SessionFactory;

/** SessionFactory dibuat oleh Spring (applicationContext.xml); fitur lain memakainya lewat sini. */
public class HibernateUtil {
    public static SessionFactory getSessionFactory() {
        return AppContext.get().getBean("sessionFactory", SessionFactory.class);
    }
}
