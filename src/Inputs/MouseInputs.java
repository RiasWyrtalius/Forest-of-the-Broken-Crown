package Inputs;

import Main.Core.GamePanel;
import Main.GameState;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel gamePanel;
    private long lastClickTime = 0;
    private final long CLICK_COOLDOWN = 200; // this adds a 200 ms cooldown after clicking a button

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
