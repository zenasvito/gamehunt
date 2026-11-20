package com.mycompany.gamehunt.dao;

import com.mycompany.gamehunt.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/** DAO produk (stok). Akses data saja; transaksi diatur Spring di ProductService. */
public class ProductDAO {

    private SessionFactory sessionFactory;

    // IMPLEMENTASI DI: Setter Injection SessionFactory disuntikkan Spring
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Session dikelola Spring (transparent session management)
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Product> findAll() {
        return getSession().createQuery("from Product order by id", Product.class).list();
    }

    public List<Product> findInStock() {
        return getSession().createQuery("from Product where stock > 0 order by name", Product.class).list();
    }

    public Product findById(int id) {
        return getSession().get(Product.class, id);
    }

    // Mencari produk berdasarkan nama game (tidak peka huruf besar/kecil)
    public Product cariByNama(String nama) {
        return getSession()
                .createQuery("from Product p where lower(p.name) = :nama", Product.class)
                .setParameter("nama", nama.toLowerCase())
                .setMaxResults(1)
                .uniqueResult();
    }

    // Menyimpan produk baru atau memperbarui yang sudah ada
    public void simpanAtauUpdate(Product product) {
        getSession().saveOrUpdate(product);
    }

    public void hapus(Product product) {
        getSession().delete(product);
    }
}
