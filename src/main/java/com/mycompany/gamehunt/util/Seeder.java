package com.mycompany.gamehunt.util;

import com.mycompany.gamehunt.model.Product;
import com.mycompany.gamehunt.model.Supplier;
import com.mycompany.gamehunt.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;

/** Mengisi data awal (akun admin + contoh game) hanya jika tabel masih kosong. */
public class Seeder {
    public static void run() {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            long users = s.createQuery("select count(u) from User u", Long.class).uniqueResult();
            if (users == 0) {
                s.save(new User("admin", Password.hash("admin123"), "admin"));
                s.save(new User("kasir", Password.hash("kasir123"), "kasir"));
            }
            long products = s.createQuery("select count(p) from Product p", Long.class).uniqueResult();
            if (products == 0) {
                s.save(new Product("Elden Ring", "Steam", new BigDecimal("599000"), 20));
                s.save(new Product("Hades", "Epic", new BigDecimal("145000"), 15));
                s.save(new Product("Stardew Valley", "Steam", new BigDecimal("80000"), 30));
            }
            long suppliers = s.createQuery("select count(x) from Supplier x", Long.class).uniqueResult();
            if (suppliers == 0) {
                s.save(new Supplier("Steam Distributor"));
                s.save(new Supplier("Epic Key Supplier"));
                s.save(new Supplier("GOG Partner"));
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
