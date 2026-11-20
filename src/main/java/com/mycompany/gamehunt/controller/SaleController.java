package com.mycompany.gamehunt.controller;

import com.mycompany.gamehunt.model.Product;
import com.mycompany.gamehunt.model.Sale;
import com.mycompany.gamehunt.util.HibernateUtil;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

/** Transaksi penjualan: simpan penjualan + potong stok dalam SATU transaksi. */
public class SaleController {

    /**
     * @return total harga transaksi
     * @throws IllegalStateException jika produk tidak ada / stok tidak cukup
     */
    public BigDecimal sell(int productId, int qty, String kasir) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Product p = s.get(Product.class, productId, LockMode.PESSIMISTIC_WRITE); // kunci baris produk
            if (p == null) throw new IllegalStateException("Produk tidak ditemukan.");
            if (p.getStock() < qty) throw new IllegalStateException("Stok tidak cukup! Sisa: " + p.getStock());

            BigDecimal total = p.getPrice().multiply(BigDecimal.valueOf(qty));
            Sale sale = new Sale();
            sale.setProductId(p.getId());
            sale.setProductName(p.getName());
            sale.setQty(qty);
            sale.setTotal(total);
            sale.setSoldBy(kasir);
            s.save(sale);

            p.setStock(p.getStock() - qty);   // potong stok
            s.update(p);

            tx.commit();                      // keduanya tersimpan bersamaan
            return total;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<Sale> findRecent(int max) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Sale order by id desc", Sale.class).setMaxResults(max).list();
        }
    }
}
