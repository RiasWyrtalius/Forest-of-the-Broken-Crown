package Inputs;

import Main.Core.Game;
import Main.Core.GamePanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Main.GameState;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;
    private Game game;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.game = gamePanel.getGame();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (GameState.state) {
            case MENU -> game.getMainMenu().keyPressed(e);
            case PLAYING -> {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    game.getPlaying().getPlayer().setDown(true);
                }
                game.getPlaying().keyPressed(e);
            }
            case CREDITS -> game.getCredits().keyPressed(e);
            case CUTSCENE -> gamePanel.getGame().getCutsceneState().keyPressed(e);
            case CHARACTER_SELECT -> game.getCharacterSelect().keyPressed(e);
            case PAUSED -> {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    GameState.state = GameState.PLAYING;
            }
            case OPTIONS -> game.getOptionsScreen().keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (GameState.state) {
            case MENU, CHARACTER_SELECT -> {}
            case PLAYING -> {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    game.getPlaying().getPlayer().setDown(false);
                }
                game.getPlaying().keyReleased(e);
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}