package Inputs;

import Main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static utils.constants.Directions.*;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
                gamePanel.setDirection(UP);
                gamePanel.setMoving(true);
                System.out.println("Input: W");
                break;
            case KeyEvent.VK_A:
                gamePanel.setDirection(LEFT);
                gamePanel.setMoving(true);
                System.out.println("Input: A");
                break;
            case KeyEvent.VK_S:
                gamePanel.setDirection(DOWN);
                gamePanel.setMoving(true);
                System.out.println("Input: S");
                break;
            case KeyEvent.VK_D:
                gamePanel.setDirection(RIGHT);
                gamePanel.setMoving(true);
                System.out.println("Input: D");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_A:
            case KeyEvent.VK_S:
            case KeyEvent.VK_D:
                gamePanel.setMoving(false);
                System.out.println("Key Released");
                break;
        }
    }
}
