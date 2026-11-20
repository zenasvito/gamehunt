package com.mycompany.gamehunt.view;

import javax.swing.*;
import java.awt.*;

/** Jendela utama. Admin: Inventaris (CRUD) + Penjualan + Restok + Laporan. Kasir: Inventaris (lihat saja) + Penjualan. */
public class MainFrame extends JFrame {
    public MainFrame(String user, String role) {
        super("Game Hunt - " + user + " (" + role + ")");
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Inventaris", new Inventaris(role.equals("admin")));
        tabs.add("Penjualan", new Penjualan(user));
        if (role.equals("admin")) {                                    // hanya admin
            tabs.add("Restok", new Restok());
            tabs.add("Laporan", new Laporan(user));
        }
        tabs.addChangeListener(e -> {                       // refresh data saat pindah tab
            Component c = tabs.getSelectedComponent();
            if (c instanceof Penjualan) ((Penjualan) c).refresh();
            if (c instanceof Inventaris) ((Inventaris) c).refresh();
            if (c instanceof Restok) ((Restok) c).refresh();
            if (c instanceof Laporan) ((Laporan) c).refresh();
        });
        add(tabs, BorderLayout.CENTER);
        JButton out = new JButton("Logout");
        out.addActionListener(e -> { dispose(); new Login().setVisible(true); });
        add(out, BorderLayout.SOUTH);
        setSize(850, 500); setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
