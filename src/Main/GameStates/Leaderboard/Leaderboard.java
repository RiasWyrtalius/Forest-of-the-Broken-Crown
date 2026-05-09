package Main.GameStates.Leaderboard;

import Audio.AudioPlayer;
import Main.Core.Game;
import Main.GameState;
import Utils.LeaderboardEntry;
import Utils.LoadSave;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;

public class Leaderboard {
    private Game game;
    private Font customFont;
    private ArrayList<LeaderboardEntry> entries;
    private boolean returnHovered = false;

    public Leaderboard(Game game) {
        this.game = game;
        loadEntries();
    }

    public void loadEntries() {
        this.entries = LoadSave.GetLeaderboard();
        entries.sort(Comparator.comparingLong(LeaderboardEntry::getRawTicks)); //faster time (lowest tick)
        customFont = LoadSave.getFont("Font/VCR.ttf").deriveFont(20f);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(46, 34, 46));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        game.getUi().drawCenteredText(g, "HALL OF THE FORSAKEN", 100, 60);

        // header
        g.setFont(customFont);
        g.drawString("NAME", 200, 180);
        g.drawString("HERO", 400, 180);
        g.drawString("TIME", 600, 180);
        g.drawString("DEATHS", 800, 180);

        // show top 10
        for (int i = 0; i < Math.min(entries.size(), 10); i++) {
            LeaderboardEntry e = entries.get(i);
            int yPos = 230 + (i * 40);
            g.drawString(e.getName(), 200, yPos);
            g.drawString(e.getCharacter(), 400, yPos);
            g.drawString(e.getTime(), 600, yPos);
            g.drawString(String.valueOf(e.getDeaths()), 800, yPos);
        }

        game.getUi().drawCenteredText(g, "Press ESC to Return", Game.GAME_HEIGHT - 50, 25);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            GameState.state = GameState.MENU;
    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (x >= (Game.GAME_WIDTH / 2) - 150 && x <= (Game.GAME_WIDTH / 2) + 150) {
            if (y >= Game.GAME_HEIGHT - 80 && y <= Game.GAME_HEIGHT - 20) {
                game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
                GameState.state = GameState.MENU;
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Check if mouse is over the "Return" area at the bottom
        if (x >= (Game.GAME_WIDTH / 2) - 150 && x <= (Game.GAME_WIDTH / 2) + 150 &&
                y >= Game.GAME_HEIGHT - 80 && y <= Game.GAME_HEIGHT - 20) {

            if (!returnHovered) {
                game.getAudioPlayer().playEffect(AudioPlayer.HOVER); // Play sound once
            }
            returnHovered = true;
        } else {
            returnHovered = false;
        }
    }
}