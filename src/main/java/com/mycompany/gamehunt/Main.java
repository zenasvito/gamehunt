package com.mycompany.gamehunt;

import com.mycompany.gamehunt.util.HibernateUtil;
import com.mycompany.gamehunt.util.Seeder;
import com.mycompany.gamehunt.view.Login;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory();   // konek + buat tabel otomatis
            Seeder.run();                        // isi data awal jika masih kosong
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "Gagal konek database:\n" + e.getMessage()
                    + "\nPeriksa hibernate.cfg.xml dan pastikan database 'gamehunt' sudah dibuat.");
            return;
        }
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
