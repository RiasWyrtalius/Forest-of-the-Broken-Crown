package Main.GameStates;

import Audio.AudioPlayer;
import Main.Core.Game;
import Main.GameState;
import Main.UI.UI;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

public class MainMenu {

    private enum Screen { MAIN_MENU, HOW_TO_PLAY }

    private Game game;
    private Screen currentScreen = Screen.MAIN_MENU;
    private String message = "";

    private int btnX        = 80;
    private int btnStartX   = 80;
    private int btnLoadX    = 80;
    private int btnOptionsX = 50;
    private int btnCreditsX = 50;
    private int btnQuitX    = 20;

    private int btnStartY   = 480;
    private int btnLoadY    = 520;
    private int btnOptionsY = 560;
    private int btnCreditsY = 600;
    private int btnQuitY    = 640;

    private int btnFontSize = 36;

    private int backBtnX = 80;
    private int backBtnY = 660;

    private int volume = 50;

    private Image bgImage;
    private Font customFont;

    private Rectangle hoveredBtn = null;
    private Rectangle lastHovered = null;

    private Rectangle playBtn = new Rectangle(btnStartX, btnStartY - 40, 220, 50);
    private Rectangle loadBtn = new Rectangle(btnLoadX, btnLoadY - 40, 220, 50);
    private Rectangle optBtn  = new Rectangle(btnOptionsX, btnOptionsY - 40, 220, 50);
    private Rectangle credBtn = new Rectangle(btnCreditsX, btnCreditsY - 40, 220, 50);
    private Rectangle quitBtn = new Rectangle(btnQuitX, btnQuitY - 40, 220, 50);

    public MainMenu(Game game) {
        this.game = game;
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
        bgImage = Toolkit.getDefaultToolkit().getImage("Assets/MainMenu/MainMenu_Variant.jpg");
    }

    public void update() {}

    private void goBack() {
        currentScreen = Screen.MAIN_MENU;
        message = "";
    }

    private boolean isNearButton(int mx, int my, int x, int y) {
        return mx >= x && mx <= x + 220
                && my >= y - btnFontSize - 5 && my <= y + 15;
    }

    //DRAW METHODS
    public void draw(Graphics g) {
        g.drawImage(bgImage, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        g.setFont(customFont);
        g.setColor(new Color(246, 246, 30));

        switch (currentScreen) {
            case MAIN_MENU  -> drawMainMenu(g);
            case HOW_TO_PLAY-> drawHowToPlay(g);
        }

        g.drawString(message, btnQuitX, btnQuitY + 60);
    }

    private void drawMainMenu(Graphics g) {
        boolean isPlayHovered = (hoveredBtn == playBtn);
        boolean isLoadHovered = (hoveredBtn == loadBtn);
        boolean isOptHovered  = (hoveredBtn == optBtn);
        boolean isCredHovered = (hoveredBtn == credBtn);
        boolean isQuitHovered = (hoveredBtn == quitBtn);

        UI.drawHoverableButton(g, playBtn.x + 20, playBtn.y + 35, "Play Game", isPlayHovered, customFont, WHITE);
        UI.drawHoverableButton(g, loadBtn.x + 20, loadBtn.y + 35, "Load Game", isLoadHovered, customFont, WHITE);
        UI.drawHoverableButton(g, optBtn.x + 50,  optBtn.y + 35,  "Options",   isOptHovered,  customFont, WHITE);
        UI.drawHoverableButton(g, credBtn.x + 50, credBtn.y + 35, "Credits",   isCredHovered, customFont, WHITE);
        UI.drawHoverableButton(g, quitBtn.x + 80, quitBtn.y + 35, "Quit",      isQuitHovered, customFont, WHITE);
    }

    private void drawHowToPlay(Graphics g) {
        g.drawString("How to Play",                 btnX + 80, 400);
        g.drawString("A/D or Arrow Keys to move",   btnX, 480);
        g.drawString("Space / W / Up to jump",      btnX, 520);
        g.drawString("K to attack",                 btnX, 560);
        g.drawString("Back",                        backBtnX, backBtnY);
    }

    //INPUT METHODS
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            handleEscapeKey();
        }
    }

    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        switch (currentScreen) {
            case MAIN_MENU   -> handleMainMenuClick(mx, my);
            case HOW_TO_PLAY -> {
                if (isNearButton(mx, my, backBtnX, backBtnY)) goBack();
            }
        }
    }

    public void mouseMoved(int x, int y) {
        Point mousePos = new Point(x, y);
        hoveredBtn = null;

        if (currentScreen == Screen.MAIN_MENU) {
            if (playBtn.contains(mousePos)) hoveredBtn = playBtn;
            else if (loadBtn.contains(mousePos)) hoveredBtn = loadBtn;
            else if (optBtn.contains(mousePos)) hoveredBtn = optBtn;
            else if (credBtn.contains(mousePos)) hoveredBtn = credBtn;
            else if (quitBtn.contains(mousePos)) hoveredBtn = quitBtn;
        }

        if (hoveredBtn != null && hoveredBtn != lastHovered) {
            game.getAudioPlayer().playEffect(AudioPlayer.HOVER);
        }
        lastHovered = hoveredBtn;
    }

    public void handleEscapeKey() {
        if (currentScreen != Screen.MAIN_MENU) goBack();
    }

    private void handleMainMenuClick(int mx, int my) {

        if (isNearButton(mx, my, btnX, btnStartY) ||
                isNearButton(mx, my, btnX, btnLoadY) ||
                isNearButton(mx, my, btnX, btnOptionsY) ||
                isNearButton(mx, my, btnX, btnCreditsY) ||
                isNearButton(mx, my, btnX, btnQuitY)) {

            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
        }

        if (isNearButton(mx, my, btnX, btnStartY)) {
            GameState.state = GameState.CHARACTER_SELECT;
        } else if (isNearButton(mx, my, btnX, btnLoadY)) {
            game.getSlotScreen().setMode("LOAD");
            GameState.state = GameState.SLOTS;
        } else if (isNearButton(mx, my, btnX, btnOptionsY)) {
            GameState.state = GameState.OPTIONS;
        } else if (isNearButton(mx, my, btnX, btnCreditsY)) {
            game.getCredits().resetCredits();
            GameState.state = GameState.CREDITS;
        } else if (isNearButton(mx, my, btnX, btnQuitY)) {
            System.exit(0);
        }
    }

    private void handleOptionsClick(int mx, int my) {
        if (mx >= btnX + 300 && mx <= btnX + 350 && my >= 450 && my <= 500) {
            volume = Math.min(100, volume + 10);
            message = "Volume increased";
        } else if (mx >= btnX + 200 && mx <= btnX + 250 && my >= 450 && my <= 500) {
            volume = Math.max(0, volume - 10);
            message = "Volume decreased";
        } else if (isNearButton(mx, my, btnX, 550)) {
            currentScreen = Screen.HOW_TO_PLAY;
        } else if (isNearButton(mx, my, backBtnX, backBtnY)) {
            goBack();
        }
    }
}