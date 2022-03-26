package com.zj.lesson5;

import javax.swing.*;
import java.awt.*;

public class JScrollDemo extends JFrame {
    public JScrollDemo() {
        Container container = this.getContentPane();
        //文本域
        JTextArea textArea = new JTextArea(20,50);
        textArea.setText("Hello World");
        JScrollPane jScrollPane = new JScrollPane(textArea);
        container.add(jScrollPane);

        this.setVisible(true);
        this.setBounds(100,100,300,600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new JScrollDemo();
    }
}
