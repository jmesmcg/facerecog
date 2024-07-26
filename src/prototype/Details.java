/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prototype;

import Extra.Database;
import java.awt.event.*;
import javax.swing.*;

public class Details extends JFrame implements ActionListener {

    JButton save = new JButton("Save");
    JTextField txt_first = new JTextField();
    JTextField txt_second = new JTextField();
    JLabel first = new JLabel("First name");
    JLabel last = new JLabel("Last name");
    JLabel id_label = new JLabel("1");
    JPanel panel = new JPanel();
    Database connection = new Database();

    Details() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.add(panel);

        save.addActionListener(this);
        Box vert = Box.createVerticalBox();
        vert.add(id_label);
        vert.add(first);
        vert.add(txt_first);
        vert.add(last);
        vert.add(txt_second);
        vert.add(save);
        panel.add(vert);

        this.setVisible(true);
        showIdUser();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            String fName = txt_first.getText();
            String lName = txt_second.getText();
            int id = Integer.parseInt(id_label.getText().replace("ID: ", ""));
            System.out.println(id);
            Register register = new Register(id, fName, lName);
            dispose();
            
        
    

    
        }
    }

    private void showIdUser() {
        connection.connect();
        connection.execute("SELECT * FROM person ORDER BY id DESC LIMIT 1");

        try {
            connection.result.first();
            id_label.setText(String.valueOf(connection.result.getInt("id")));
            int id = Integer.parseInt(id_label.getText());
            id++;
            id_label.setText(String.valueOf(id));

            // connection.result.first();
        } catch (Exception e) {

        }
    }
}
