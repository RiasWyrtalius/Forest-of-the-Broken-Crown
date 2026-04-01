package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

public class MainMenu {

    private Game game;

    // Button areas (x, y, width, height)
    private Rectangle btnStart  = new Rectangle(300, 200, 200, 50);
    private Rectangle btnLoad   = new Rectangle(300, 280, 200, 50);
    private Rectangle btnQuit   = new Rectangle(300, 360, 200, 50);

    private String message = ""; // shows feedback like "Saved!" or "No save found"

    public MainMenu(Game game) {
        this.game = game;
    }

    public void draw(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Forest of the Broken Crown", 170, 120);

        // Buttons
        drawButton(g, btnStart, "Start Game");
        drawButton(g, btnLoad,  "Load Game");
        drawButton(g, btnQuit,  "Quit");

        // Feedback message
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(message, 320, 440);
    }

    private void drawButton(Graphics g, Rectangle btn, String label) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(btn.x, btn.y, btn.width, btn.height);

        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        // Center the text inside the button (roughly)
        g.drawString(label, btn.x + 20, btn.y + 32);
    }

    public void mouseClicked(MouseEvent e) {
        if (btnStart.contains(e.getPoint())) {
            // Fresh start — reset player position
            game.getPlayer().getHitbox().x = 200;
            game.getPlayer().getHitbox().y = 200;
            GameState.state = GameState.PLAYING;

        } else if (btnLoad.contains(e.getPoint())) {
            loadGame();

        } else if (btnQuit.contains(e.getPoint())) {
            System.exit(0);
        }
    }


    public void saveGame() {
        try {
            FileWriter fw = new FileWriter("save.txt");
            fw.write(game.getPlayer().getHitbox().x + "\n");
            fw.write(game.getPlayer().getHitbox().y + "\n");
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
            float x = Float.parseFloat(br.readLine());
            float y = Float.parseFloat(br.readLine());
            br.close();

            game.getPlayer().getHitbox().x = x;
            game.getPlayer().getHitbox().y = y;

            message = "Game Loaded!";
            GameState.state = GameState.PLAYING;

        } catch (IOException e) {
            e.printStackTrace();
            message = "Load failed.";
        }
    }
}