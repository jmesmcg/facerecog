/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prototype;

import java.awt.Desktop;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class Menu extends JFrame implements ActionListener {

    JButton register = new JButton("Register Face");
    JButton sign = new JButton("Sign in");
    JButton data = new JButton("Data");
    JLabel title = new JLabel("Choose an option");
    JPanel panel;

    Menu() {
        this.setSize(500, 400);
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setTitle("Menu");
        this.setLocationRelativeTo(null);
        panel = new JPanel();
        this.add(panel);
        register.addActionListener(this);
        sign.addActionListener(this);
        data.addActionListener(this);
        Box vert = Box.createVerticalBox();
        vert.add(title);
        vert.add(register);
        vert.add(sign);
        vert.add(data);
        panel.add(vert);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == register) {
            dispose();
            Details details = new Details();
        } else if (e.getSource() == sign) {
            dispose();
            Sign signin = new Sign();
        } else if (e.getSource() == data) {
            File file = new File("C:\\Users\\jemcg\\Documents\\Computer Science\\Project\\data.txt");
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
