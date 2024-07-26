/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Login extends JFrame implements ActionListener {

    JButton enter;
    JLabel title;
    JLabel Juser;
    JLabel Jpass;
    JPanel panel;
    JTextField Jusername;
    JPasswordField Jpassword;

    Login() {
        this.setSize(300, 500);
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setTitle("Login");
        this.setLocationRelativeTo(null);
        panel = new JPanel();
        this.add(panel);
        enter = new JButton("Enter");
        enter.addActionListener(this);
        Juser = new JLabel("Username");
        Jpass = new JLabel("Password");
        Jusername = new JTextField();
        Jpassword = new JPasswordField();
        title = new JLabel("Log in");

        Box vert = Box.createVerticalBox();
        vert.add(title);
        vert.add(Juser);
        vert.add(Jusername);
        vert.add(Jpass);
        vert.add(Jpassword);
        vert.add(enter);
        panel.add(vert);

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enter) {
            String user = Jusername.getText();
            String password = Jpassword.getText();
            if (user.equals("admin") && password.equals("password")) {
                Menu menu = new Menu();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Wrong username or password");
            }
        }

    }
}
