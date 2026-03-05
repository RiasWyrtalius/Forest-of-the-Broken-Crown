package Inputs;

import Main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
                    gamePanel.changeY(-20);
//                System.out.println("Input: W");
                break;
            case KeyEvent.VK_A:
                gamePanel.changeX(-20);
//                System.out.println("Input: A");
                break;
            case KeyEvent.VK_S:
                gamePanel.changeY(20);
//                System.out.println("Input: S");
                break;
            case KeyEvent.VK_D:
                gamePanel.changeX(20);
//                System.out.println("Input: D");
                break;
        }
    }
}
