package Inputs;

import Main.Core.Game;
import Main.Core.GamePanel;
import Main.GameState;
import Main.UI.UI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static Main.Core.Game.GAME_HEIGHT;
import static Main.Core.Game.GAME_WIDTH;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel gamePanel;
    private long lastClickTime = 0;
    private final long CLICK_COOLDOWN = 200; // this adds a 200 ms cooldown after clicking a button

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MouseEvent adjustedEvent = getMouseEvent(e);

        switch (GameState.state) {
            case WORLD_SELECT -> gamePanel.getGame().getWorldSelect().mouseClicked(adjustedEvent);
            case MENU -> gamePanel.getGame().getMainMenu().mouseClicked(adjustedEvent);
            case CHARACTER_SELECT -> gamePanel.getGame().getCharacterSelect().mouseClicked(adjustedEvent);
            case PAUSED -> gamePanel.getGame().getPauseScreen().mouseClicked(adjustedEvent);
            case SLOTS -> gamePanel.getGame().getSlotScreen().mouseClicked(adjustedEvent);
            case DEATH -> gamePanel.getGame().getDeathScreen().mouseClicked(adjustedEvent);
            case CUTSCENE -> gamePanel.getGame().getCutsceneState().mouseClicked(adjustedEvent);
            case LEADERBOARD -> gamePanel.getGame().getLeaderboard().mouseClicked(adjustedEvent);
            case PLAYING -> {}
            case BOSS_DECISION -> {
                UI ui = gamePanel.getGame().getUi();
                Game game = gamePanel.getGame();

                // Determine where to go AFTER the cutscene based on the speedrun timer toggle
                GameState nextState = game.isSpeedrunActive() ? GameState.NAME_INPUT : GameState.CREDITS;

                if (ui.getTakeCrownBtn().contains(adjustedEvent.getPoint())) {
                    game.getCutsceneState().startCutscene("OUTRO_BAD", nextState);
                    GameState.state = GameState.CUTSCENE;
                } else if (ui.getThrowCrownBtn().contains(adjustedEvent.getPoint())) {
                    game.getCutsceneState().startCutscene("OUTRO_GOOD", nextState);
                    GameState.state = GameState.CUTSCENE;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        MouseEvent adjustedEvent = getMouseEvent(e);

        long now = System.currentTimeMillis();
        if (now - lastClickTime < CLICK_COOLDOWN && GameState.state != GameState.WORLD_SELECT) return;
        lastClickTime = now;

        switch (GameState.state) {
            case WORLD_SELECT -> gamePanel.getGame().getWorldSelect().mouseClicked(adjustedEvent);
            case MENU -> gamePanel.getGame().getMainMenu().mouseClicked(adjustedEvent);
            case CHARACTER_SELECT -> gamePanel.getGame().getCharacterSelect().mouseClicked(adjustedEvent);
            case SLOTS -> gamePanel.getGame().getSlotScreen().mouseClicked(adjustedEvent);
            case PAUSED -> gamePanel.getGame().getPauseScreen().mouseClicked(adjustedEvent);
            case DEATH -> gamePanel.getGame().getDeathScreen().mouseClicked(adjustedEvent);
            case CUTSCENE -> gamePanel.getGame().getCutsceneState().mousePressed(adjustedEvent);
            case OPTIONS -> gamePanel.getGame().getOptionsScreen().mousePressed(adjustedEvent);
            case BOSS_DECISION -> {
                UI ui = gamePanel.getGame().getUi();
                Game game = gamePanel.getGame();

                // Determine where to go AFTER the cutscene based on the speedrun timer toggle
                GameState nextState = game.isSpeedrunActive() ? GameState.NAME_INPUT : GameState.CREDITS;

                if (ui.getTakeCrownBtn().contains(adjustedEvent.getPoint())) {
                    game.getCutsceneState().startCutscene("OUTRO_BAD", nextState);
                    GameState.state = GameState.CUTSCENE;
                } else if (ui.getThrowCrownBtn().contains(adjustedEvent.getPoint())) {
                    game.getCutsceneState().startCutscene("OUTRO_GOOD", nextState);
                    GameState.state = GameState.CUTSCENE;
                }
            }
        }
    }

    @Override public void mouseReleased(MouseEvent e) {
        MouseEvent adjustedEvent = getMouseEvent(e);

        switch (GameState.state) {
            case OPTIONS -> gamePanel.getGame().getOptionsScreen().mouseReleased(adjustedEvent);
        }
    }

    private MouseEvent getMouseEvent(MouseEvent e) {
        double scaleX = (double) gamePanel.getWidth() / GAME_WIDTH;
        double scaleY = (double) gamePanel.getHeight() / GAME_HEIGHT;

        //adjust mouse coordinates to actual game res.
        int adjustedX = (int) (e.getX() / scaleX);
        int adjustedY = (int) (e.getY() / scaleY);

        MouseEvent adjustedEvent = new MouseEvent(
                (Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(),
                adjustedX, adjustedY, e.getClickCount(), e.isPopupTrigger()
        );
        return adjustedEvent;
    }

    @Override public void mouseMoved(MouseEvent e) {
        MouseEvent adjustedEvent = getMouseEvent(e);

        double scaleX = (double) gamePanel.getWidth() / GAME_WIDTH;
        double scaleY = (double) gamePanel.getHeight() / GAME_HEIGHT;

        int adjustedX = (int) (e.getX() / scaleX);
        int adjustedY = (int) (e.getY() / scaleY);

        switch (GameState.state) {
            case CHARACTER_SELECT -> gamePanel.getGame().getCharacterSelect().mouseMoved(adjustedX, adjustedY);
            case MENU -> gamePanel.getGame().getMainMenu().mouseMoved(adjustedX, adjustedY);
            case PAUSED -> gamePanel.getGame().getPauseScreen().mouseMoved(adjustedX, adjustedY);
            case DEATH -> gamePanel.getGame().getDeathScreen().mouseMoved(adjustedX, adjustedY);
            case OPTIONS -> gamePanel.getGame().getOptionsScreen().mouseMoved(adjustedEvent);
            case WORLD_SELECT -> gamePanel.getGame().getWorldSelect().mouseMoved(adjustedEvent);
            case SLOTS -> gamePanel.getGame().getSlotScreen().mouseMoved(adjustedEvent);
            case LEADERBOARD -> gamePanel.getGame().getLeaderboard().mouseMoved(adjustedEvent);
            default -> {}
        }
    }

    @Override public void mouseDragged(MouseEvent e) {
        MouseEvent adjustedEvent = getMouseEvent(e);
        switch (GameState.state) {
            case OPTIONS -> gamePanel.getGame().getOptionsScreen().mouseDragged(adjustedEvent);
        }
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}