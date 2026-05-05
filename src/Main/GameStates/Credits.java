package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Credits {
    private Game game;
    private float creditY;
    private final float SCROLL_SPEED = 0.5f;
    private Font titleFont;
    private Font nameFont;

    private String[] lines = {
            "FOREST OF THE BROKEN CROWN",
            "",
            "~ DEVELOPMENT TEAM ~",
            "Chad Ellie Sanchez",
            "Sean Riley Dela Cruz",
            "Charlz David Despues",
            "Alonzo Denver Raganas",
            "Niño Michael Mahusay",
            "",
            "~ AUDIO FROM: ~",
            "Resoundxstudio",
            "Pixabay",
            "",
            "~ SPECIAL THANKS ~",
            "Sir Khai",
            "To the players",
            "",
            "Thank you for playing!"
    };

    public Credits(Game game) {
        titleFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
        nameFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(36f);
        resetCredits();
    }

    public void resetCredits() {
        creditY = Game.GAME_HEIGHT + 50;
    }

    public void update() {
        creditY -= SCROLL_SPEED;

        if (creditY < -(lines.length * 60)) {
            GameState.state = GameState.MENU;
        }
    }

    public void draw(Graphics g) {
        g.setColor(new Color(46, 34, 46));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        int yOffset = 0;

        for (String line : lines) {
            int currentY = (int) creditY + yOffset;

            if (line.startsWith("~") || line.equals("FOREST OF THE BROKEN CROWN")) {
                g.setFont(titleFont);
                g.setColor(new Color(246, 246, 30));
            } else {
                g.setFont(nameFont);
                g.setColor(Color.WHITE);
            }

            drawCenteredText(g, line, currentY);
            yOffset += 60; //spacing
        }

        //SKIP
        g.setFont(nameFont.deriveFont(24f));
        g.setColor(new Color(255, 255, 255, 100)); // Faded white
        g.drawString("Press ESC to Skip", Game.GAME_WIDTH - 200, Game.GAME_HEIGHT - 30);
    }

    private void drawCenteredText(Graphics g, String text, int y) {
        int width = g.getFontMetrics().stringWidth(text);
        int x = (Game.GAME_WIDTH / 2) - (width / 2);
        g.drawString(text, x, y);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            GameState.state = GameState.MENU; // Allow the player to skip
        }
    }
}
