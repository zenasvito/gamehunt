package com.mycompany.gamehunt.controller;

import com.mycompany.gamehunt.model.Product;
import com.mycompany.gamehunt.model.Purchase;
import com.mycompany.gamehunt.model.Supplier;
import com.mycompany.gamehunt.util.HibernateUtil;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

/** Restok: catat pembelian dari supplier + tambah stok produk dalam SATU transaksi. */
public class RestokController {

    public List<Supplier> findSuppliers() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Supplier order by name", Supplier.class).list();
        }
    }

    /**
     * @param unitCost harga beli per unit
     * @return total pengeluaran (unitCost x qty)
     * @throws IllegalStateException jika supplier / produk tidak ditemukan
     */
    public BigDecimal restock(int supplierId, int productId, int qty, BigDecimal unitCost) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Supplier sup = s.get(Supplier.class, supplierId);
            Product p = s.get(Product.class, productId, LockMode.PESSIMISTIC_WRITE);  // kunci baris produk
            if (sup == null) throw new IllegalStateException("Supplier tidak ditemukan.");
            if (p == null) throw new IllegalStateException("Produk tidak ditemukan.");

            BigDecimal total = unitCost.multiply(BigDecimal.valueOf(qty));
            Purchase pur = new Purchase();
            pur.setSupplierId(sup.getId());
            pur.setSupplierName(sup.getName());
            pur.setProductId(p.getId());
            pur.setProductName(p.getName());
            pur.setQty(qty);
            pur.setTotal(total);
            s.save(pur);                       // catat rekam jejak pengadaan

            p.setStock(p.getStock() + qty);    // tambah stok gudang
            s.update(p);

            tx.commit();                       // keduanya tersimpan bersamaan
            return total;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<Purchase> findRecent(int max) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Purchase order by id desc", Purchase.class).setMaxResults(max).list();
        }
    }
}
