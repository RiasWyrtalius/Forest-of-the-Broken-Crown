package Main.GameStates;

import Audio.AudioPlayer;
import Main.Core.Game;
import Main.GameState;
import Main.UI.UI;
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
    private Rectangle hoveredBtn = null;
    private Rectangle lastHovered = null;

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

        // you died
        g.setColor(Color.RED);
        g.setFont(customFont.deriveFont(Font.BOLD, 72f));
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU DIED";
        int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
        g.drawString(title, titleX, 80);

        // subtitle
        g.setColor(Color.WHITE);
        g.setFont(customFont.deriveFont(24f));
        String subtitle = "Your journey ends here...";
        fm = g.getFontMetrics();
        int subtitleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(subtitle) / 2);
        g.drawString(subtitle, subtitleX, 110);

        boolean isRestartHovered = (hoveredBtn == btnRestart);
        boolean isMenuHovered = (hoveredBtn == btnMainMenu);
        UI.drawHoverableButton(g, btnRestart.x + 35, btnRestart.y + 60, "Restart Game", isRestartHovered, customFont, Color.WHITE);
        UI.drawHoverableButton(g, btnMainMenu.x + 10, btnMainMenu.y + 60, "Main Menu", isMenuHovered, customFont, Color.WHITE);
    }

    public void update() {}

    //mouse stuff
    public void mouseMoved(int x, int y) {
        Point mousePos = new Point(x, y);
        hoveredBtn = null;

        if (btnRestart.contains(mousePos)) hoveredBtn = btnRestart;
        else if (btnMainMenu.contains(mousePos)) hoveredBtn = btnMainMenu;

        if (hoveredBtn != null && hoveredBtn != lastHovered) {
            game.getAudioPlayer().playEffect(AudioPlayer.HOVER);
        }
        lastHovered = hoveredBtn;
    }

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
        } else if (btnMainMenu.contains(e.getPoint())) { // Make sure Main Menu click works too!
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            game.resetSpeedrunTimer();
            game.resetGame();
            GameState.state = GameState.MENU;
        }
    }
}