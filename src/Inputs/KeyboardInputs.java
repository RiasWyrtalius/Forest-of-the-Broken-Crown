package Inputs;

import Main.Core.Game;
import Main.Core.GamePanel;

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
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (GameState.state == GameState.PLAYING) {
                GameState.state = GameState.PAUSED;
            } else if (GameState.state == GameState.PAUSED) {
                GameState.state = GameState.PLAYING;
            } else if (GameState.state == GameState.MENU) {
                gamePanel.getGame().getMainMenu().handleEscapeKey();
            }
            return;
        }

        // Handle State-Specific Keys
        switch (GameState.state) {
            case PLAYING:
                handlePlayingInput(e);
                break;
            case CHARACTER_SELECT:
                gamePanel.getGame().getCharacterSelect().keyPressed(e);
                break;
            case MENU:
                // handle menu specific keys if anyd
                break;
        }
    }

    private void handlePlayingInput(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT:
                gamePanel.getGame().getPlayer().setLeft(true);
                break;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT:
                gamePanel.getGame().getPlayer().setRight(true);
                break;
            case KeyEvent.VK_SPACE, KeyEvent.VK_UP, KeyEvent.VK_W:
                gamePanel.getGame().getPlayer().setJump(true);
                break;
            case KeyEvent.VK_K:
                gamePanel.getGame().getPlayer().setAttacking(true);
                break;
            case KeyEvent.VK_F5:
                gamePanel.getGame().getSlotScreen().setMode("SAVE");
                GameState.state = GameState.SLOTS;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_UP:
                gamePanel.getGame().getPlayer().setJump(false);
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
            case KeyEvent.VK_K:
                gamePanel.getGame().getPlayer().setAttacking(false);
                break;
        }
    }
}
