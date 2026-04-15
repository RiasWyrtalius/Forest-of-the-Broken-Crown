package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MainMenu {

    private enum Screen { MAIN_MENU, OPTIONS, HOW_TO_PLAY, CREDITS }

    private Game game;
    private Screen currentScreen = Screen.MAIN_MENU;
    private String message = "";

    private int btnX = 80;
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

    public void saveGame() { }
    private void loadGame() { }

    //DRAW METHODS
    public void draw(Graphics g) {
        g.drawImage(bgImage, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        g.setFont(customFont);
        g.setColor(new Color(246, 246, 30));

        switch (currentScreen) {
            case MAIN_MENU  -> drawMainMenu(g);
            case OPTIONS    -> drawOptions(g);
            case HOW_TO_PLAY-> drawHowToPlay(g);
            case CREDITS    -> drawCredits(g);
        }

        g.drawString(message, btnX, btnQuitY + 60);
    }

    private void drawMainMenu(Graphics g) {
        g.drawString("Play Game", btnX, btnStartY);
        g.drawString("Load Game", btnX, btnLoadY);
        g.drawString("Options",   btnX, btnOptionsY);
        g.drawString("Credits",   btnX, btnCreditsY);
        g.drawString("Quit",      btnX, btnQuitY);
    }

    private void drawOptions(Graphics g) {
        g.drawString("OPTIONS",           btnX + 100, 400);
        g.drawString("Volume: " + volume + "%", btnX, 480);
        g.drawString("+",                 btnX + 320, 480);
        g.drawString("-",                 btnX + 220, 480);
        g.drawString("How to Play",       btnX, 550);
        g.drawString("Back",              backBtnX, backBtnY);
    }

    private void drawHowToPlay(Graphics g) {
        g.drawString("How to Play",                 btnX + 80, 400);
        g.drawString("A/D or Arrow Keys to move",   btnX, 480);
        g.drawString("Space / W / Up to jump",      btnX, 520);
        g.drawString("K to attack",                 btnX, 560);
        g.drawString("Back",                        backBtnX, backBtnY);
    }

    private void drawCredits(Graphics g) {
        g.drawString("Credits",              btnX + 120, 390);
        g.drawString("Chad Ellie Sanchez",   btnX, 460);
        g.drawString("Sean Riley Dela Cruz", btnX, 500);
        g.drawString("Alonzo Raganas",        btnX, 540);
        g.drawString("Charlz David Despues", btnX, 580);
        g.drawString("Niño Michael Mahusay", btnX, 620);
        g.drawString("Back",                 backBtnX, backBtnY);
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
            case OPTIONS     -> handleOptionsClick(mx, my);
            case HOW_TO_PLAY, CREDITS -> {
                if (isNearButton(mx, my, backBtnX, backBtnY)) goBack();
            }
        }
    }

    public void handleEscapeKey() {
        if (currentScreen != Screen.MAIN_MENU) goBack();
    }

    private void handleMainMenuClick(int mx, int my) {
        if (isNearButton(mx, my, btnX, btnStartY)) {
            GameState.state = GameState.CHARACTER_SELECT;
        } else if (isNearButton(mx, my, btnX, btnLoadY)) {
            game.getSlotScreen().setMode("LOAD");
            GameState.state = GameState.SLOTS;
        } else if (isNearButton(mx, my, btnX, btnOptionsY)) {
            currentScreen = Screen.OPTIONS;
        } else if (isNearButton(mx, my, btnX, btnCreditsY)) {
            currentScreen = Screen.CREDITS;
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
