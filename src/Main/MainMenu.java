package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

public class MainMenu {

    private Game game;

    private String message = "";

    // these are button text positions
    private int btnX      = 80;
    private int btnStartY = 498;
    private int btnLoadY  = 543;
    private int btnQuitY  = 588;

    private int btnFontSize = 36;

    public MainMenu(Game game) {
        this.game = game;
    }

    public void draw(Graphics g) {
        // this IS the background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // title
        g.setColor(new Color(246, 246, 109));
        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.drawString("FOREST OF THE", 80, 130);
        g.drawString("BROKEN CROWN",  80, 200);

        // buttons
        g.setFont(new Font("Arial", Font.BOLD, btnFontSize));
        g.setColor(new Color(246, 246, 30));
        g.drawString("Play Game",      btnX, btnStartY);
        g.drawString("Load Game", btnX, btnLoadY);
        g.drawString("Quit",      btnX, btnQuitY);

        // feedback message
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(message, btnX, btnQuitY + 40);
    }

    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (isNearButton(mx, my, btnX, btnStartY)) {
            game.getPlayer().getHitbox().x = 200;
            game.getPlayer().getHitbox().y = 200;
            game.getLevelHandler().loadLevel(1);
            game.startFadeTo(GameState.PLAYING);

        } else if (isNearButton(mx, my, btnX, btnLoadY)) {
            game.getSlotScreen().setMode("LOAD");
            GameState.state = GameState.SLOTS;

        } else if (isNearButton(mx, my, btnX, btnQuitY)) {
            System.exit(0);
        }
    }

    // invisible hit area for each text button
    private boolean isNearButton(int mx, int my, int x, int y) {
        return mx >= x && mx <= x + 200
                && my >= y - btnFontSize && my <= y;
    }

    public void saveGame() {
        try {
            FileWriter fw = new FileWriter("save.txt");
            fw.write(game.getPlayer().getHitbox().x + "\n");
            fw.write(game.getPlayer().getHitbox().y + "\n");
            fw.write(game.getLevelHandler().getCurrentLevelNum() + "\n");
            fw.close();
            message = "Game Saved!";
        } catch (IOException e) {
            e.printStackTrace();
            message = "Save failed.";
        }
    }

    private void loadGame() {
        File file = new File("save.txt");
        if (!file.exists()) {
            message = "No save file found!";
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            float x      = Float.parseFloat(br.readLine());
            float y      = Float.parseFloat(br.readLine());
            int levelNum = Integer.parseInt(br.readLine());
            br.close();

            game.getPlayer().getHitbox().x = x;
            game.getPlayer().getHitbox().y = y;
            game.getLevelHandler().loadLevel(levelNum);

            GameState.state = GameState.PLAYING;
            message = "Game Loaded!";

        } catch (IOException e) {
            e.printStackTrace();
            message = "Load failed.";
        }
    }
}