package main;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private int moveX = 40;
    private int moveY = 40;

    public GamePanel() {
        mouseInputs = new MouseInputs(this);

        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    public void changeMoveX(int value) {
        this.moveX += value;
        repaint();
    }

    public void changeMoveY(int value) {
        this.moveY += value;
        repaint();
    }

    public void setPostion(int x, int y) {
        this.moveX = x;
        this.moveY = y;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawRect(20, 20, 20, 20);
        g.drawRect(40, 20, 20, 20);
        g.drawRect(60, 20, 20, 20);

        g.drawRect(20, 40, 20, 20);
        g.fillRect(moveX, moveY, 20, 20);
        g.drawRect(60, 40, 20, 20);

        g.drawRect(20, 60, 20, 20);
        g.drawRect(40, 60, 20, 20);
        g.drawRect(60, 60, 20, 20);
    }
}
