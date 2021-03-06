package com.zj.lesson3;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestWindow {
    public static void main(String[] args) {
        new WindowFrame();
    }
}

//class WindowFrame extends Frame {
//    public WindowFrame(){
//        setBackground(Color.blue);
//        setBounds(100,100,200,200);
//        setVisible(true);
//        addWindowListener(new MyWindowListener());
//    }
//    class MyWindowListener extends WindowAdapter{
//        @Override
//        public void windowClosing(WindowEvent e) {
//            setVisible(false);//隐藏窗口，通过按钮
//            System.exit(0);
//        }
//    }
//}
//优化

class WindowFrame extends Frame {
    public WindowFrame(){
        setBackground(Color.blue);
        setBounds(100,100,200,200);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            //匿名内部类
            //关闭窗口
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);//隐藏窗口，通过按钮
                System.exit(0);
            }
            //激活窗口

            @Override
            public void windowActivated(WindowEvent e) {
                WindowFrame source = (WindowFrame) e.getSource();
                source.setTitle("被激活了");
                System.out.println("windowActivated");
            }
        });
    }
}