package Inputs;

import Main.Game;
import Main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Main.GameState;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;
    private final Game game;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.game = gamePanel.getGame();
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT:
                gamePanel.getGame().getPlayer().setLeft(true);
                break;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT:
                gamePanel.getGame().getPlayer().setRight(true);
                break;
            case KeyEvent.VK_SPACE, KeyEvent.VK_UP:
                gamePanel.getGame().getPlayer().setJump(true);
                break;
                //temp
            case KeyEvent.VK_K:
                gamePanel.getGame().getPlayer().setAttacking(true);
                break;
            case KeyEvent.VK_H: // TEST BUTTON FOR HEART LOSS (in-game..)
                gamePanel.getGame().getPlayer().loseLife();
                break;
            case KeyEvent.VK_ESCAPE:
                if (GameState.state == GameState.PLAYING) {
                    GameState.state = GameState.PAUSED;        // open pause screen
                } else if (GameState.state == GameState.PAUSED) {
                    GameState.state = GameState.PLAYING;       // close pause
                } else if (GameState.state == GameState.MENU) {
                    game.getMainMenu().handleEscapeKey();      // go back in menu
                }
                break;
            case KeyEvent.VK_F5:
                if (GameState.state == GameState.PLAYING) {
                    gamePanel.getGame().getSlotScreen().setMode("SAVE");
                    GameState.state = GameState.SLOTS;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP:
                //gamePanel.getGame().getPlayer().setUp(false);
                break;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT:
                gamePanel.getGame().getPlayer().setLeft(false);
                break;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN:
                //gamePanel.getGame().getPlayer().setDown(false);
                break;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT:
                gamePanel.getGame().getPlayer().setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                gamePanel.getGame().getPlayer().setJump(false);
                break;
            case KeyEvent.VK_K:
                gamePanel.getGame().getPlayer().setAttacking(false);
                break;
        }
    }
}
