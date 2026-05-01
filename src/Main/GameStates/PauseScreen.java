package Main.GameStates;

import Audio.AudioPlayer;
import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PauseScreen {

    private Font customFont;
    private Game game;
    private BufferedImage pauseBg;
    private int bgX, bgY, bgW, bgH;

    private int btnWidth = 250;
    private int btnHeight = 55;
    private int btnX = (Game.GAME_WIDTH / 2) - (btnWidth / 2);

    private Rectangle hoveredBtn = null;
    private Rectangle lastHovered = null;

    private Rectangle btnContinue  = new Rectangle(btnX, 250, btnWidth, btnHeight);
    private Rectangle btnLoad      = new Rectangle(btnX, 300, btnWidth, btnHeight);
    private Rectangle btnMainMenu  = new Rectangle(btnX, 350, btnWidth, btnHeight);

    public PauseScreen(Game game) {
        this.game = game;
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(35f);

        pauseBg = LoadSave.getSpriteAtlas(LoadSave.PauseBg);
        bgW = (int) (pauseBg.getWidth() * Game.SCALE);
        bgH = (int) (pauseBg.getHeight() * Game.SCALE);
        bgX = (Game.GAME_WIDTH / 2) - (bgW / 2);
        bgY = (int) (100 * Game.SCALE);
    }

    public void draw(Graphics g) {
        // dim the game behind
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.drawImage(pauseBg, bgX, bgY, bgW, bgH, null);
        // buttons
        Utils.HelpMethods.DrawHoverableButton(g, btnContinue, "Continue", hoveredBtn, customFont);
        Utils.HelpMethods.DrawHoverableButton(g, btnLoad, "Load Game", hoveredBtn, customFont);
        Utils.HelpMethods.DrawHoverableButton(g, btnMainMenu, "Main Menu", hoveredBtn, customFont);
    }

    public void mouseMoved(int x, int y) {
        Point mousePos = new Point(x, y);
        hoveredBtn = null;

        if (btnContinue.contains(mousePos)) hoveredBtn = btnContinue;
        else if (btnLoad.contains(mousePos)) hoveredBtn = btnLoad;
        else if (btnMainMenu.contains(mousePos)) hoveredBtn = btnMainMenu;

        if (hoveredBtn != null && hoveredBtn != lastHovered) {
            game.getAudioPlayer().playEffect(AudioPlayer.HOVER);
        }
        lastHovered = hoveredBtn;
    }

    public void mouseClicked(MouseEvent e) {
        if (btnContinue.contains(e.getPoint())) {
            // just go back to playing
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            GameState.state = GameState.PLAYING;
        } else if (btnLoad.contains(e.getPoint())) {
            // open slot screen in LOAD mode
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            game.getSlotScreen().setMode("LOAD");
            GameState.state = GameState.SLOTS;
        } else if (btnMainMenu.contains(e.getPoint())) {
            // go back to main menu
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            GameState.state = GameState.MENU;
        }
    }
}
