package com.mycompany.gamehunt.controller;

import com.mycompany.gamehunt.model.User;
import com.mycompany.gamehunt.util.HibernateUtil;
import com.mycompany.gamehunt.util.Password;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** Logika autentikasi: login & register. */
public class UserController {

    /** @return User jika valid, null jika username/password salah. */
    public User login(String username, String password) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from User where username = :u and password = :p", User.class)
                    .setParameter("u", username)
                    .setParameter("p", Password.hash(password))
                    .uniqueResult();
        }
    }

    /** Daftarkan akun baru (role kasir). @return false jika username sudah dipakai. */
    public boolean register(String username, String password) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Long exists = s.createQuery("select count(u) from User u where u.username = :u", Long.class)
                    .setParameter("u", username).uniqueResult();
            if (exists > 0) { tx.rollback(); return false; }
            s.save(new User(username, Password.hash(password), "kasir"));
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
