package com.mycompany.gamehunt.view;

import com.mycompany.gamehunt.controller.LaporanController;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;

/** Tampilan Laporan Keuangan Ringkas + cetak JasperReports (pratinjau & PDF). */
public class Laporan extends JPanel {
    private final LaporanController controller = new LaporanController();
    private final String user;
    private final DecimalFormat rp = new DecimalFormat("#,##0.00");
    private final JLabel lblIncome = new JLabel("-"), lblExpense = new JLabel("-"), lblProfit = new JLabel("-");

    public Laporan(String user) {
        this.user = user;
        setLayout(new GridBagLayout());
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Laporan Keuangan Ringkas"));
        p.add(new JLabel("Total Pendapatan (Penjualan)")); p.add(lblIncome);
        p.add(new JLabel("Total Pengeluaran (Restok)")); p.add(lblExpense);
        p.add(new JLabel("Laba Kotor")); p.add(lblProfit);
        JButton hitung = new JButton("Hitung Ulang");
        JButton preview = new JButton("Pratinjau Laporan (Jasper)");
        JButton pdf = new JButton("Simpan PDF");
        p.add(hitung); p.add(preview);
        p.add(pdf);
        add(p);

        hitung.addActionListener(e -> refresh());
        preview.addActionListener(e -> preview());
        pdf.addActionListener(e -> savePdf());
        refresh();
    }

    public void refresh() {
        try {
            LaporanController.Ringkasan r = controller.ringkasan();
            lblIncome.setText("Rp " + rp.format(r.pendapatan));
            lblExpense.setText("Rp " + rp.format(r.pengeluaran));
            lblProfit.setText("Rp " + rp.format(r.laba()));
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void preview() {
        try {
            JasperPrint print = controller.buatLaporan(user);
            JasperViewer.viewReport(print, false);      // false = tutup jendela viewer tidak menutup aplikasi
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan:\n" + e.getMessage());
        }
    }

    private void savePdf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("laporan_keuangan.pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
        try {
            controller.simpanPdf(controller.buatLaporan(user), path);
            JOptionPane.showMessageDialog(this, "Laporan tersimpan:\n" + path);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan PDF:\n" + e.getMessage());
        }
    }
}
