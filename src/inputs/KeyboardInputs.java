package inputs;

import main.Game;
import main.GamePanel;

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
                gamePanel.changeMoveY(-20);
                System.out.println("Input: W");
                break;
            case KeyEvent.VK_A:
                gamePanel.changeMoveX(-20);
                System.out.println("Input: A");
                break;
            case KeyEvent.VK_S:
                gamePanel.changeMoveY(20);
                System.out.println("Input: S");
                break;
            case KeyEvent.VK_D:
                gamePanel.changeMoveX(20);
                System.out.println("Input: D");
                break;
        }
    }
}
