package Main.GameStates.Leaderboard;

import Main.Core.Game;
import Main.GameState;
import Utils.LeaderboardEntry;
import Utils.LoadSave;
import java.awt.*;
import java.awt.event.KeyEvent;

public class NameInputState {
    private Game game;
    private Font customFont;
    private StringBuilder playerName = new StringBuilder();
    private final int MAX_CHARACTERS = 12;

    public NameInputState(Game game) {
        this.game = game;
        customFont = LoadSave.getFont("Font/VCR.ttf").deriveFont(20f);
    }

    public void draw(Graphics g) {
        //dark overlay
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        //text prompt
        g.setColor(Color.WHITE);
        game.getUi().drawCenteredText(g, "THE CROWN IS YOURS", 200, 45);
        game.getUi().drawCenteredText(g, "Enter your name for the archives:", 300, 25);

        //input box
        int boxW = 400, boxH = 60, boxX = (Game.GAME_WIDTH/2) - (boxW/2), boxY = 350;
        g.drawRect(boxX, boxY, boxW, boxH);

        //typing name
        g.setFont(customFont);
        String currentText = playerName.toString() + (System.currentTimeMillis() % 1000 < 500 ? "|" : ""); // Blinking cursor
        int textX = (Game.GAME_WIDTH / 2) - (g.getFontMetrics().stringWidth(playerName.toString()) / 2);
        g.drawString(currentText, textX, boxY + 45);

        game.getUi().drawCenteredText(g, "Press ENTER to immortalize your run", 500, 20);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !playerName.isEmpty()) {
            saveAndContinue();
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !playerName.isEmpty()) {
            playerName.deleteCharAt(playerName.length() - 1);
        } else {
            // Standard Character Input
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) && playerName.length() < MAX_CHARACTERS) {
                playerName.append(c);
            }
        }
    }

    private void saveAndContinue() {
        String finalName = playerName.toString();
        String hero = game.getPlaying().getPlayer().getCharacterData().name();
        String time = game.getUi().getFormattedTime();
        int deaths = game.getPlaying().getPlayer().getDeathCounter();
        long ticks = game.getSpeedrunTicks();

        LoadSave.AddLeaderboardEntry(new LeaderboardEntry(finalName, hero, time, deaths, ticks));

        // Reset for next time and move to Leaderboard
        playerName.setLength(0);
        game.getLeaderboard().loadEntries();
        GameState.state = GameState.LEADERBOARD;
    }
}