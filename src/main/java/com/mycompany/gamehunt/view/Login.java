package com.mycompany.gamehunt.view;

import com.mycompany.gamehunt.controller.UserController;
import com.mycompany.gamehunt.model.User;

import javax.swing.*;
import java.awt.*;

/** Form Login & Register. */
public class Login extends JFrame {
    private final UserController controller = new UserController();
    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);

    public Login() {
        super("Game Hunt - Login");
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        p.add(new JLabel("Username")); p.add(txtUser);
        p.add(new JLabel("Password")); p.add(txtPass);
        JButton btnLogin = new JButton("Login");
        JButton btnReg = new JButton("Register");
        p.add(btnLogin); p.add(btnReg);
        add(p);
        add(new JLabel("Admin: admin / admin123   |   Kasir: kasir / kasir123", SwingConstants.CENTER),
                BorderLayout.SOUTH);
        btnLogin.addActionListener(e -> doLogin());
        btnReg.addActionListener(e -> doRegister());
        getRootPane().setDefaultButton(btnLogin);
        pack(); setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void doLogin() {
        try {
            User u = controller.login(txtUser.getText().trim(), new String(txtPass.getPassword()));
            if (u != null) {
                new MainFrame(u.getUsername(), u.getRole()).setVisible(true);
                dispose();
            } else JOptionPane.showMessageDialog(this, "Username atau password salah!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void doRegister() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty() || p.length() < 4) {
            JOptionPane.showMessageDialog(this, "Username wajib diisi, password minimal 4 karakter."); return;
        }
        try {
            if (controller.register(u, p))
                JOptionPane.showMessageDialog(this, "Registrasi berhasil sebagai kasir. Silakan login.");
            else JOptionPane.showMessageDialog(this, "Username sudah dipakai.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }
}
