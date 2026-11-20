package com.mycompany.gamehunt.view;

import com.mycompany.gamehunt.controller.SaleController;
import com.mycompany.gamehunt.model.Product;
import com.mycompany.gamehunt.model.Sale;
import com.mycompany.gamehunt.service.ProductService;
import com.mycompany.gamehunt.util.AppContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/** Tampilan Penjualan (kasir). */
public class Penjualan extends JPanel {
    /** Pasangan id + teks untuk ComboBox. */
    private static class Item {
        final int id; final String label;
        Item(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private final ProductService products = AppContext.productService();
    private final SaleController sales = new SaleController();
    private final String user;
    private final JComboBox<Item> cbProduct = new JComboBox<>();
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JLabel lblInfo = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Game", "Jumlah", "Total", "Kasir", "Waktu"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public Penjualan(String user) {
        this.user = user;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Kasir"));
        form.add(new JLabel("Produk")); form.add(cbProduct);
        form.add(new JLabel("Jumlah")); form.add(spQty);
        JButton btn = new JButton("Proses Transaksi");
        JButton reload = new JButton("Muat Ulang");
        form.add(btn); form.add(reload); form.add(lblInfo);
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        btn.addActionListener(e -> process());
        reload.addActionListener(e -> refresh());
        refresh();
    }

    public void refresh() {
        cbProduct.removeAllItems();
        model.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        try {
            for (Product p : products.daftarTersedia())
                cbProduct.addItem(new Item(p.getId(),
                        p.getName() + " - Rp" + p.getPrice() + " (stok " + p.getStock() + ")"));
            for (Sale x : sales.findRecent(100))
                model.addRow(new Object[]{x.getId(), x.getProductName(), x.getQty(), x.getTotal(),
                        x.getSoldBy(), x.getSoldAt() == null ? "" : x.getSoldAt().format(f)});
        } catch (Exception e) { lblInfo.setText(e.getMessage()); }
    }

    private void process() {
        Item it = (Item) cbProduct.getSelectedItem();
        if (it == null) { JOptionPane.showMessageDialog(this, "Tidak ada produk yang tersedia."); return; }
        int qty = (Integer) spQty.getValue();
        try {
            BigDecimal total = sales.sell(it.id, qty, user);
            lblInfo.setText("Sukses! Total Rp" + total);
            refresh();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            refresh();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }
}
