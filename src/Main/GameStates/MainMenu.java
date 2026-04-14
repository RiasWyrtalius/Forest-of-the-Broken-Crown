package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MainMenu {

    private Game game;

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

    private String currentScreen = "MAIN_MENU";

    private Image bgImage;
    private Font customFont;

    public MainMenu(Game game) {
        this.game = game;
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
        bgImage = Toolkit.getDefaultToolkit().getImage("Assets/MainMenu/MainMenu_Variant.jpg");
    }

    //REPLACE HERE
    public void draw(Graphics g) {
        g.drawImage(bgImage, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        g.setFont(customFont);
        g.setColor(new Color(246, 246, 30));

        if (currentScreen.equals("MAIN_MENU")) {
            g.drawString("Play Game",   btnX, btnStartY);
            g.drawString("Load Game",   btnX, btnLoadY);
            g.drawString("Options",     btnX, btnOptionsY);
            g.drawString("Credits",     btnX, btnCreditsY);
            g.drawString("Quit",        btnX, btnQuitY);

        } else if (currentScreen.equals("OPTIONS")) {
            g.drawString("OPTIONS", btnX + 100, 400);

            g.drawString("Volume: " + volume + "%", btnX, 480);
            g.drawString("+", btnX + 320, 480);
            g.drawString("-", btnX + 220, 480);

            g.drawString("How to Play", btnX, 550);
            g.drawString("Back", backBtnX, backBtnY);

        } else if (currentScreen.equals("HOW_TO_PLAY")) {
            g.drawString("How to Play", btnX + 80, 400);
            g.drawString("ASD for movement", btnX, 480);
            g.drawString("and spacebar for jump, that's it", btnX, 520);
            g.drawString("Back", backBtnX, backBtnY);

        } else if (currentScreen.equals("CREDITS")) {
            g.drawString("Credits", btnX + 120, 390);

            g.drawString("Chad Ellie Sanchez", btnX, 460);
            g.drawString("Sean Riley Dela Cruz", btnX, 500);
            g.drawString("Alonzo Raganas", btnX, 540);
            g.drawString("Charlz David Despues", btnX, 580);
            g.drawString("Niño Michael Mahusay", btnX, 620);

            g.drawString("Back", backBtnX, backBtnY);
        }
        g.drawString(message, btnX, btnQuitY + 60);
    }

    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (currentScreen.equals("MAIN_MENU")) {
            if (isNearButton(mx, my, btnX, btnStartY)) {
//                game.getPlayer().getHitbox().x = 200;
//                game.getPlayer().getHitbox().y = 200;
//                game.getLevelHandler().loadLevel(1);
//                game.startFadeTo(GameState.PLAYING);
                GameState.state = GameState.CHARACTER_SELECT;
            } else if (isNearButton(mx, my, btnX, btnLoadY)) {
                game.getSlotScreen().setMode("LOAD");
                GameState.state = GameState.SLOTS;

            } else if (isNearButton(mx, my, btnX, btnOptionsY)) {
                currentScreen = "OPTIONS";

            } else if (isNearButton(mx, my, btnX, btnCreditsY)) {
                currentScreen = "CREDITS";

            } else if (isNearButton(mx, my, btnX, btnQuitY)) {
                System.exit(0);
            }

        } else if (currentScreen.equals("OPTIONS")) {

            if (mx >= btnX + 300 && mx <= btnX + 350 && my >= 450 && my <= 500) {
                volume = Math.min(100, volume + 10);
                message = "Volume increased";
            }

            else if (mx >= btnX + 200 && mx <= btnX + 250 && my >= 450 && my <= 500) {
                volume = Math.max(0, volume - 10);
                message = "Volume decreased";
            }

            else if (isNearButton(mx, my, btnX, 550)) {
                currentScreen = "HOW_TO_PLAY";
            }

            else if (isNearButton(mx, my, backBtnX, backBtnY)) {
                currentScreen = "MAIN_MENU";
                message = "";
            }

        } else if (currentScreen.equals("HOW_TO_PLAY") || currentScreen.equals("CREDITS")) {
            if (isNearButton(mx, my, backBtnX, backBtnY)) {
                currentScreen = "MAIN_MENU";
                message = "";
            }
        }
    }

    private boolean isNearButton(int mx, int my, int x, int y) {
        return mx >= x && mx <= x + 220
                && my >= y - btnFontSize - 5 && my <= y + 15;
    }

    //TODO: MOVE INPUT TO KeyboardInputs.class
    // added ESC support, if you press ESC it goes back to Main Menu screen
    public void handleEscapeKey() {
        if (!currentScreen.equals("MAIN_MENU")) {
            currentScreen = "MAIN_MENU";
            message = "";
        }
    }


    public void saveGame() { }
    private void loadGame() { }
}
