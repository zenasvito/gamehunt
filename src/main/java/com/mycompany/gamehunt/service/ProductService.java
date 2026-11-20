package com.mycompany.gamehunt.service;

import com.mycompany.gamehunt.dao.ProductDAO;
import com.mycompany.gamehunt.model.Product;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Logika bisnis stok/inventaris.
 * @Transactional: setiap method berjalan dalam satu transaksi yang dikelola Spring
 * (commit otomatis jika sukses, rollback otomatis jika terjadi exception).
 */
@Transactional
public class ProductService {

    private ProductDAO productDAO;

    // Setter Injection: ProductDAO disuntikkan Spring (lihat applicationContext.xml)
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Transactional(readOnly = true)
    public List<Product> daftar() {
        return productDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> daftarTersedia() {
        return productDAO.findInStock();
    }

    public void tambah(String name, String platform, BigDecimal price, int stock) {
        validasi(name, platform, price, stock);
        if (productDAO.cariByNama(name) != null)
            throw new IllegalStateException("Game dengan nama itu sudah ada.");
        productDAO.simpanAtauUpdate(new Product(name, platform, price, stock));
    }

    public void ubah(int id, String name, String platform, BigDecimal price, int stock) {
        validasi(name, platform, price, stock);
        Product p = productDAO.findById(id);
        if (p == null) throw new IllegalStateException("Produk tidak ditemukan.");
        Product sama = productDAO.cariByNama(name);
        if (sama != null && !sama.getId().equals(id))
            throw new IllegalStateException("Game dengan nama itu sudah ada.");
        p.setName(name); p.setPlatform(platform); p.setPrice(price); p.setStock(stock);
        productDAO.simpanAtauUpdate(p);
    }

    public void hapus(int id) {
        Product p = productDAO.findById(id);
        if (p != null) productDAO.hapus(p);
    }

    private void validasi(String name, String platform, BigDecimal price, int stock) {
        if (name == null || name.trim().isEmpty() || platform == null || platform.trim().isEmpty())
            throw new IllegalArgumentException("Nama dan platform wajib diisi.");
        if (price == null || price.signum() <= 0)
            throw new IllegalArgumentException("Harga harus lebih dari 0.");
        if (stock < 0)
            throw new IllegalArgumentException("Stok tidak boleh negatif.");
    }
}
