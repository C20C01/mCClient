package io.github.c20c01.tool.UI;

import io.github.c20c01.tool.MessageTool;
import io.github.c20c01.tool.TimeTool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends JFrame {

    //No development, no need to read

    public Main() {
        setResizable(false);
        setTitle("mCClient");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 250, 450, 190);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println(MessageTool.readString("{\"translate\":\"chat.type.text\",\"with\":[{\"insertion\":\"8899\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/tell 8899 \"},\"hoverEvent\":{\"action\":\"show_entity\",\"contents\":{\"type\":\"minecraft:player\",\"id\":\"94b58eab-d528-36de-b7eb-039e01271aa7\",\"name\":{\"text\":\"8899\"}}},\"text\":\"8899\"},\"66666666666666666\"]}"));
    }
}
