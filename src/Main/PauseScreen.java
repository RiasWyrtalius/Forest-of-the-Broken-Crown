package Main;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PauseScreen {

    private Game game;

    private int btnWidth = 250;
    private int btnHeight = 55;

    private int btnX = (Game.GAME_WIDTH / 2) - (btnWidth / 2);

    private Rectangle btnContinue  = new Rectangle(btnX, 200, btnWidth, btnHeight);
    private Rectangle btnLoad      = new Rectangle(btnX, 285, btnWidth, btnHeight);
    private Rectangle btnMainMenu  = new Rectangle(btnX, 370, btnWidth, btnHeight);

    public PauseScreen(Game game) {
        this.game = game;
    }
        public void draw(Graphics g) {
            // dim the game behind
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // title
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            FontMetrics fm = g.getFontMetrics();
            String title = "Paused";
            int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
            g.drawString(title, titleX, 150);

            // buttons
            drawButton(g, btnContinue, "Continue");
            drawButton(g, btnLoad,     "Load Game");
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
            if (btnContinue.contains(e.getPoint())) {
                // just go back to playing
                GameState.state = GameState.PLAYING;

            } else if (btnLoad.contains(e.getPoint())) {
                // open slot screen in LOAD mode
                game.getSlotScreen().setMode("LOAD");
                GameState.state = GameState.SLOTS;

            } else if (btnMainMenu.contains(e.getPoint())) {
                // go back to main menu
                GameState.state = GameState.MENU;
            }
        }
    }
