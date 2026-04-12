package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

public class DeathScreen {

    private Game game;
    private boolean videoPlaying = true;
    private long videoStartTime;
    private final long VIDEO_DURATION = 3000; // 3 seconds for demo, adjust based on actual video length

    private int btnWidth = 250;
    private int btnHeight = 55;

    private int btnX = (Game.GAME_WIDTH / 2) - (btnWidth / 2);

    private Rectangle btnMainMenu  = new Rectangle(btnX, 350, btnWidth, btnHeight);
    private Rectangle btnRestart   = new Rectangle(btnX, 265, btnWidth, btnHeight);

    public DeathScreen(Game game) {
        this.game = game;
    }

    public void draw(Graphics g) {
        // dim the game behind
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        if (videoPlaying) {
            // Show video placeholder - in a real implementation, this would render the video
            drawVideoPlaceholder(g);
        } else {
            // Show death screen menu
            drawDeathMenu(g);
        }
    }

    private void drawVideoPlaceholder(Graphics g) {
        // Placeholder for video - in production, this would be actual video rendering
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // Video frame border
        g.setColor(Color.RED);
        g.drawRect(50, 50, Game.GAME_WIDTH - 100, Game.GAME_HEIGHT - 100);

        // Video title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        String videoText = "PLAYING: YOU DIED (HD).mp4";
        int textX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(videoText) / 2);
        g.drawString(videoText, textX, Game.GAME_HEIGHT / 2 - 50);

        // Progress indicator
        long elapsed = System.currentTimeMillis() - videoStartTime;
        int progressWidth = (int)((elapsed / (double)VIDEO_DURATION) * (Game.GAME_WIDTH - 200));
        g.setColor(Color.RED);
        g.fillRect(100, Game.GAME_HEIGHT / 2 + 50, progressWidth, 10);

        // Progress text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String progressText = elapsed/1000 + "s / " + VIDEO_DURATION/1000 + "s";
        g.drawString(progressText, Game.GAME_WIDTH / 2 - 50, Game.GAME_HEIGHT / 2 + 80);
    }

    private void drawDeathMenu(Graphics g) {
        // title
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU DIED";
        int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
        g.drawString(title, titleX, 200);

        // subtitle
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String subtitle = "Game Over";
        fm = g.getFontMetrics();
        int subtitleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(subtitle) / 2);
        g.drawString(subtitle, subtitleX, 240);

        // buttons
        drawButton(g, btnRestart, "Restart Game");
        drawButton(g, btnMainMenu, "Main Menu");
    }

    private void drawButton(Graphics g, Rectangle btn, String label) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(btn.x, btn.y, btn.width, btn.height);

        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);

        g.setFont(new Font("Arial", Font.PLAIN, 22));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width  / 2) - (fm.stringWidth(label) / 2);
        int textY = btn.y + (btn.height / 2) + (fm.getAscent() / 2) - 2;
        g.setColor(Color.WHITE);
        g.drawString(label, textX, textY);
    }

    public void update() {
        if (videoPlaying) {
            // Check if video should end
            long elapsed = System.currentTimeMillis() - videoStartTime;
            if (elapsed >= VIDEO_DURATION) {
                videoPlaying = false;
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (!videoPlaying) {
            if (btnRestart.contains(e.getPoint())) {
                // restart the game - reset player and go back to playing
                game.resetGame();
                GameState.state = GameState.PLAYING;
                resetVideoState();

            } else if (btnMainMenu.contains(e.getPoint())) {
                // go back to main menu
                GameState.state = GameState.MENU;
                resetVideoState();
            }
        }
    }

    private void resetVideoState() {
        videoPlaying = true;
        videoStartTime = System.currentTimeMillis();
    }

    public void startVideo() {
        videoPlaying = true;
        videoStartTime = System.currentTimeMillis();
    }

    public boolean isVideoPlaying() {
        return videoPlaying;
    }
}