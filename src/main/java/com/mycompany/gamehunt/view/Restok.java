package com.mycompany.gamehunt.view;

import com.mycompany.gamehunt.controller.RestokController;
import com.mycompany.gamehunt.model.Product;
import com.mycompany.gamehunt.model.Purchase;
import com.mycompany.gamehunt.model.Supplier;
import com.mycompany.gamehunt.service.ProductService;
import com.mycompany.gamehunt.util.AppContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/** Tampilan Restok: pilih supplier, produk, jumlah, harga beli, lalu simpan pembelian. */
public class Restok extends JPanel {
    /** Pasangan id + teks untuk ComboBox. */
    private static class Item {
        final int id; final String label;
        Item(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private final RestokController controller = new RestokController();
    private final ProductService products = AppContext.productService();
    private final JComboBox<Item> cbSupplier = new JComboBox<>();
    private final JComboBox<Item> cbProduct = new JComboBox<>();
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JTextField txtCost = new JTextField("0", 8);
    private final JLabel lblInfo = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Supplier", "Game", "Jumlah", "Total", "Waktu"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public Restok() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Restok Stok"));
        form.add(new JLabel("Supplier")); form.add(cbSupplier);
        form.add(new JLabel("Produk")); form.add(cbProduct);
        form.add(new JLabel("Jumlah")); form.add(spQty);
        form.add(new JLabel("Harga beli/unit")); form.add(txtCost);
        JButton save = new JButton("Simpan Pembelian");
        JButton reload = new JButton("Muat Ulang");
        form.add(save); form.add(reload); form.add(lblInfo);
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        save.addActionListener(e -> save());
        reload.addActionListener(e -> refresh());
        refresh();
    }

    public void refresh() {
        cbSupplier.removeAllItems();
        cbProduct.removeAllItems();
        model.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        try {
            for (Supplier s : controller.findSuppliers()) cbSupplier.addItem(new Item(s.getId(), s.getName()));
            for (Product p : products.daftar())
                cbProduct.addItem(new Item(p.getId(), p.getName() + " (stok " + p.getStock() + ")"));
            for (Purchase x : controller.findRecent(100))
                model.addRow(new Object[]{x.getId(), x.getSupplierName(), x.getProductName(), x.getQty(),
                        x.getTotal(), x.getBoughtAt() == null ? "" : x.getBoughtAt().format(f)});
        } catch (Exception e) { lblInfo.setText(e.getMessage()); }
    }

    private void save() {
        Item sup = (Item) cbSupplier.getSelectedItem();
        Item pr = (Item) cbProduct.getSelectedItem();
        if (sup == null || pr == null) {
            JOptionPane.showMessageDialog(this, "Supplier / produk belum tersedia."); return;
        }
        try {
            BigDecimal cost = new BigDecimal(txtCost.getText().trim());
            if (cost.signum() <= 0) {
                JOptionPane.showMessageDialog(this, "Harga beli harus lebih dari 0."); return;
            }
            int qty = (Integer) spQty.getValue();
            BigDecimal total = controller.restock(sup.id, pr.id, qty, cost);
            lblInfo.setText("Tersimpan. Pengeluaran Rp" + total);
            refresh();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga beli harus berupa angka.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }
}
