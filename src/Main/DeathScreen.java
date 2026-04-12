package Main;

import java.awt.*;
import java.awt.event.MouseEvent;

public class DeathScreen {

    private Game game;

    private int btnWidth = 250;
    private int btnHeight = 55;

    private int btnX = (Game.GAME_WIDTH / 2) - (btnWidth / 2);

    private Rectangle btnMainMenu  = new Rectangle(btnX, 350, btnWidth, btnHeight);
    private Rectangle btnRestart   = new Rectangle(btnX, 265, btnWidth, btnHeight);

    public DeathScreen(Game game) {
        this.game = game;
    }

    public void draw(Graphics g) {
        // dim the game behind
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // title
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU DIED";
        int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
        g.drawString(title, titleX, 200);

        // subtitle
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String subtitle = "Game Over";
        fm = g.getFontMetrics();
        int subtitleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(subtitle) / 2);
        g.drawString(subtitle, subtitleX, 240);

        // buttons
        drawButton(g, btnRestart, "Restart Game");
        drawButton(g, btnMainMenu, "Main Menu");
    }

    private void drawButton(Graphics g, Rectangle btn, String label) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(btn.x, btn.y, btn.width, btn.height);

        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);

        g.setFont(new Font("Arial", Font.PLAIN, 22));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width  / 2) - (fm.stringWidth(label) / 2);
        int textY = btn.y + (btn.height / 2) + (fm.getAscent() / 2) - 2;
        g.setColor(Color.WHITE);
        g.drawString(label, textX, textY);
    }

    public void mouseClicked(MouseEvent e) {
        if (btnRestart.contains(e.getPoint())) {
            // restart the game - reset player and go back to playing
            game.resetGame();
            GameState.state = GameState.PLAYING;

        } else if (btnMainMenu.contains(e.getPoint())) {
            // go back to main menu
            GameState.state = GameState.MENU;
        }
    }
}