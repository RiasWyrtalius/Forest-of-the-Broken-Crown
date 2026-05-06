package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DeathScreen {

    private Game game;
    private BufferedImage backgroundImg;
    private Font customFont;

    private int btnWidth = 300;
    private int btnHeight = 70;

    private Rectangle btnRestart = new Rectangle(160, 500, btnWidth, btnHeight);
    private Rectangle btnMainMenu = new Rectangle(800, 500, btnWidth, btnHeight);

    public DeathScreen(Game game) {
        this.game = game;
        this.backgroundImg = LoadSave.getSpriteAtlas(LoadSave.DeathScreen);
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
    }

    public void draw(Graphics g) {
        drawDeathMenu(g);
    }

    private void drawDeathMenu(Graphics g) {
        // title
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        //you died
        g.setColor(Color.RED);
        g.setFont(customFont.deriveFont(Font.BOLD, 72f)); // Made slightly larger for the background
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU DIED";
        int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
        g.drawString(title, titleX, 80);

        //subtitle
        g.setColor(Color.WHITE);
        g.setFont(customFont.deriveFont(24f));
        String subtitle = "Your journey ends here...";
        fm = g.getFontMetrics();
        int subtitleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(subtitle) / 2);
        g.drawString(subtitle, subtitleX, 110);

        drawButton(g, btnRestart, "Restart Game");
        drawButton(g, btnMainMenu, "Main Menu");
    }

    private void drawButton(Graphics g, Rectangle btn, String label) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(btn.x, btn.y, btn.width, btn.height);

        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);

        g.setFont(customFont);
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width  / 2) - (fm.stringWidth(label) / 2);
        int textY = btn.y + (btn.height / 2) + (fm.getAscent() / 2) - 2;
        g.setColor(Color.WHITE);
        g.drawString(label, textX, textY);
    }

    public void update() {}

    public void mouseClicked(MouseEvent e) {
        if (btnRestart.contains(e.getPoint())) {
            int currentLevel = game.getLevelHandler().getCurrentLevelNum();
            game.setupLevel(currentLevel);
            game.getPlayer().resetAll();
            game.getObjectManager().resetAllObjects();
            game.getPlaying().resetCamera();
            game.getPlaying().getEnemyManager().reset();
            game.getPlaying().loadEnemiesForLevel(currentLevel);
            GameState.state = GameState.PLAYING;
        }
    }
}