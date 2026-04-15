package Inputs;

import Main.Core.Game;
import Main.Core.GamePanel;
import Main.GameState;

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
            case MENU:
                gamePanel.getGame().getMainMenu().mouseClicked(adjustedEvent);
                break;
            case CHARACTER_SELECT:
                gamePanel.getGame().getCharacterSelect().mouseClicked(adjustedEvent);
                break;
            case PLAYING:
                //insert logic if needed
                break;
            case PAUSED:
                gamePanel.getGame().getPauseScreen().mouseClicked(adjustedEvent);
                break;
            case SLOTS:
                gamePanel.getGame().getSlotScreen().mouseClicked(adjustedEvent);
                break;
            case DEATH:
                gamePanel.getGame().getDeathScreen().mouseClicked(adjustedEvent);
                break;
            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        long now = System.currentTimeMillis();
        if (now - lastClickTime < CLICK_COOLDOWN) return; // this ignores too-fast clicks
        lastClickTime = now;
        if (Main.GameState.state == Main.GameState.MENU) {
            gamePanel.getGame().getMainMenu().mouseClicked(e);
        } else if (GameState.state == GameState.CHARACTER_SELECT) {
            gamePanel.getGame().getCharacterSelect().mouseClicked(e);
        } else if (Main.GameState.state == Main.GameState.SLOTS) {
            gamePanel.getGame().getSlotScreen().mouseClicked(e);
        } else if (Main.GameState.state == Main.GameState.PAUSED) {
            gamePanel.getGame().getPauseScreen().mouseClicked(e);
        } else if (Main.GameState.state == Main.GameState.DEATH) {
            gamePanel.getGame().getDeathScreen().mouseClicked(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gamePanel.getGame().getPlayer().setAttacking(false);
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
        double scaleX = (double) gamePanel.getWidth() / GAME_WIDTH;
        double scaleY = (double) gamePanel.getHeight() / GAME_HEIGHT;

        int adjustedX = (int) (e.getX() / scaleX);
        int adjustedY = (int) (e.getY() / scaleY);

        switch (GameState.state) {
            case CHARACTER_SELECT -> gamePanel.getGame().getCharacterSelect().mouseMoved(adjustedX, adjustedY);
            case MENU -> gamePanel.getGame().getMainMenu().mouseMoved(adjustedX, adjustedY);
            case PAUSED -> gamePanel.getGame().getPauseScreen().mouseMoved(adjustedX, adjustedY);
            default -> {
            }

        }
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}

}
