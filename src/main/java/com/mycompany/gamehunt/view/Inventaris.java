package com.mycompany.gamehunt.view;

import com.mycompany.gamehunt.service.ProductService;
import com.mycompany.gamehunt.util.AppContext;
import com.mycompany.gamehunt.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

/** Tampilan Manajemen Inventaris (CRUD produk). */
public class Inventaris extends JPanel {
    private final ProductService service = AppContext.productService();   // bean dari Spring
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Nama Game", "Platform", "Harga", "Stok"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField txtName = new JTextField(12), txtPlat = new JTextField(8),
            txtPrice = new JTextField(8), txtStock = new JTextField(5);

    public Inventaris(boolean canEdit) {
        setLayout(new BorderLayout(5, 5));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Nama")); form.add(txtName);
        form.add(new JLabel("Platform")); form.add(txtPlat);
        form.add(new JLabel("Harga")); form.add(txtPrice);
        form.add(new JLabel("Stok")); form.add(txtStock);
        JButton add = new JButton("Tambah"), upd = new JButton("Ubah"),
                del = new JButton("Hapus"), clr = new JButton("Bersih");
        form.add(add); form.add(upd); form.add(del); form.add(clr);
        add(form, BorderLayout.SOUTH);
        form.setVisible(canEdit);          // kasir: hanya lihat daftar produk

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                txtName.setText(model.getValueAt(r, 1).toString());
                txtPlat.setText(model.getValueAt(r, 2).toString());
                txtPrice.setText(model.getValueAt(r, 3).toString());
                txtStock.setText(model.getValueAt(r, 4).toString());
            }
        });
        add.addActionListener(e -> save(true));
        upd.addActionListener(e -> save(false));
        del.addActionListener(e -> delete());
        clr.addActionListener(e -> clear());
        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        try {
            for (Product p : service.daftar())
                model.addRow(new Object[]{p.getId(), p.getName(), p.getPlatform(), p.getPrice(), p.getStock()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void save(boolean insert) {
        try {
            String name = txtName.getText().trim(), plat = txtPlat.getText().trim();
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (name.isEmpty() || plat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama dan platform wajib diisi."); return;
            }
            if (insert) {
                service.tambah(name, plat, price, stock);
            } else {
                int r = table.getSelectedRow();
                if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih produk di tabel dulu."); return; }
                service.ubah((Integer) model.getValueAt(r, 0), name, plat, price, stock);
            }
            clear(); refresh();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih produk di tabel dulu."); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus produk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != 0) return;
        try {
            service.hapus((Integer) model.getValueAt(r, 0));
            clear(); refresh();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage()); }
    }

    private void clear() {
        txtName.setText(""); txtPlat.setText(""); txtPrice.setText(""); txtStock.setText("");
        table.clearSelection();
    }
}
