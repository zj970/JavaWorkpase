package com.zj.snake;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

//游戏的面板  //键盘监听器  //定时器
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    //游戏当前的状态
    boolean isStart = false;//默认是不开始
    boolean isFail = false;//游戏失败，默认false
    int score = 0;//初始化成绩
    Snake snake = new Snake();
    //定时器
    Timer timer = new Timer(100, this);//以毫秒为单位

    //食物的坐标
    int foodx;
    int foody;

    Random random = new Random();

    public GamePanel() {
        this.setFocusable(true);//获得焦点事件
        this.addKeyListener(this);//获得键盘的监听事件
        timer.start();
        createFood();
    }

    //绘制面板,游戏里的所有东西都是由此画笔制作
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//清屏的作用，没有就会闪屏
        //绘制静态的面板
        this.setBackground(Color.WHITE);
        Data.header.paintIcon(this, g, 25, 11);//头部广告栏
        g.fillRect(25, 75, 850, 600);//默认的游戏界面


        Data.food.paintIcon(this,g,foodx,foody);
        //画积分

        g.setColor(Color.YELLOW);
        g.setFont(new Font("微软雅黑", Font.BOLD, 18));//设置字体
        g.drawString("长度"+snake.length, 750, 35);
        g.drawString("分数"+score, 750, 53);

        //把小蛇画上去
        //方向
        switch (snake.fx) {
            case "W":
                Data.up.paintIcon(this, g, snake.snakex[0], snake.snakey[0]);
                break;
            case "A":
                Data.left.paintIcon(this, g, snake.snakex[0], snake.snakey[0]);
                break;
            case "S":
                Data.down.paintIcon(this, g, snake.snakex[0], snake.snakey[0]);
                break;
            case "D":
                Data.right.paintIcon(this, g, snake.snakex[0], snake.snakey[0]);
                break;
        }

        for (int i = 1; i < snake.length; i++) {
            Data.body.paintIcon(this, g, snake.snakex[i], snake.snakey[i]);//画身体部分
        }

        //游戏未开始时
        if (isStart == false) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("微软雅黑", Font.BOLD, 40));//设置字体
            g.drawString("按下空格开始游戏", 300, 300);
        }

        //游戏失败
        if (isFail){
            g.setColor(Color.RED);
            g.setFont(new Font("微软雅黑", Font.BOLD, 40));//设置字体
            g.drawString("按下空格重新开始游戏", 300, 300);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //键盘按下
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();//获得键盘按键是哪一个
        if (keyCode == KeyEvent.VK_SPACE) {
            //按下的空格

            if (isFail){
                //重新开始
                isFail=false;
                snake.init();
                createFood();
                score = 0;
            }else {
                isStart = !isStart;
            }
            repaint();
        }

        //小蛇移动
        switch (keyCode) {
            case KeyEvent.VK_W:
                snake.fx = "W";
                break;
            case KeyEvent.VK_S:
                snake.fx = "S";
                break;
            case KeyEvent.VK_A:
                snake.fx = "A";
                break;
            case KeyEvent.VK_D:
                snake.fx = "D";
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    //事件监听----通过固定事件监听
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStart && !isFail) {
            //如果游戏是开始状态，就让小蛇动起来
            //右移
            for (int i = snake.length - 1; i > 0; i--) {
                snake.snakex[i] = snake.snakex[i - 1];//向前移动
                snake.snakey[i] = snake.snakey[i - 1];
            }
            //走向
            switch (snake.fx) {
                case "D":
                    snake.snakex[0] = snake.snakex[0] + 25;
                    //边界判断
                    if (snake.snakex[0] > 850) {
                        snake.snakex[0] = 25;
                    }
                    break;
                case "A":
                    snake.snakex[0] = snake.snakex[0] - 25;
                    //边界判断
                    if (snake.snakex[0] < 25) {
                        snake.snakex[0] = 850;
                    }
                    break;
                case "W":
                    snake.snakey[0] = snake.snakey[0] - 25;
                    //边界判断
                    if (snake.snakey[0] < 75) {
                        snake.snakey[0] = 650;
                    }
                    break;
                case "S":
                    snake.snakey[0] = snake.snakey[0] + 25;
                    //边界判断
                    if (snake.snakey[0] > 650) {
                        snake.snakey[0] = 75;
                    }
                    break;
            }
            //失败判断，撞到自己就算失败
            for (int i = 1;i< snake.length;i++){
                if (snake.snakex[0]==snake.snakex[i] && snake.snakey[0]==snake.snakey[i]){
                    isFail = true;
                }
            }

            //吃食物
            if (snake.snakex[0] == foodx && snake.snakey[0] == foody){
                snake.length++;//长度加1
                score = addCarry(score,10);
                //再次随机生成食物
                createFood();
            }

            repaint();//重画页面
        }
        timer.start();//定时器开始
    }

    //生成食物
    public void createFood(){
        //把食物随机分布在界面上
        foodx = 25 + 25*random.nextInt(35);
        foody = 75 + 25*random.nextInt(24);
    }

    //位运算实现相加
    int addCarry(int a,int b){
        int result;
        int sum,carry;
        sum = a^b;
        carry = (a&b)<<1;
        if (carry==0){
            result = sum;
        }else {
           result = addCarry(sum,carry);
        }
        return result;
    }
}


