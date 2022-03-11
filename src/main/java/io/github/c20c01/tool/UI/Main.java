package io.github.c20c01.tool.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main extends JFrame {

    //No development, no need to read

    public Main() {
        setTitle("mCClient");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 250, 900, 600);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setVisible(true);
    }

    public static void main(String[] args) {

        new Main();
    }
}
