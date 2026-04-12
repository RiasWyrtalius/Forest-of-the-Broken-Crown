package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import javax.swing.*;

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

    // VLCJ video player components
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JFrame videoFrame;
    private boolean videoFinished = false;
    private static final String VIDEO_PATH =
        "C:\\Users\\Ayban\\Desktop\\3rd year\\Nino Michael Gwapo\\Forest-of-the-Broken-Crown\\src\\Video\\YOU DIED (HD).mp4";

    public DeathScreen(Game game) {
        this.game = game;
        // Initialize VLCJ native discovery
        new NativeDiscovery().discover();
    }

    public void draw(Graphics g) {
        // Only draw the menu if video is not playing
        if (!videoPlaying) {
            // dim the game behind
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // Show death screen menu
            drawDeathMenu(g);
        }
        // If video is playing, the video frame handles the display
    }

    private void playVideo() {
        if (videoFrame != null && videoFrame.isVisible()) {
            return; // Video is already playing
        }

        videoFrame = new JFrame();
        videoFrame.setUndecorated(true);
        videoFrame.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
        videoFrame.setLocationRelativeTo(null);
        videoFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        videoFrame.add(mediaPlayerComponent, BorderLayout.CENTER);

        // Add event listener for when video finishes
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(
            new uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter() {
                @Override
                public void finished(uk.co.caprica.vlcj.player.base.MediaPlayer mediaPlayer) {
                    SwingUtilities.invokeLater(() -> {
                        stopVideo();
                        videoFinished = true;
                        videoPlaying = false;
                    });
                }

                @Override
                public void error(uk.co.caprica.vlcj.player.base.MediaPlayer mediaPlayer) {
                    SwingUtilities.invokeLater(() -> {
                        stopVideo();
                        videoFinished = true;
                        videoPlaying = false;
                        System.err.println("Error playing video: " + VIDEO_PATH);
                    });
                }
            }
        );

        videoFrame.setVisible(true);

        // Check if video file exists
        File videoFile = new File(VIDEO_PATH);
        if (videoFile.exists()) {
            mediaPlayerComponent.mediaPlayer().media().play(VIDEO_PATH);
        } else {
            // Fallback to placeholder if video doesn't exist
            System.err.println("Video file not found: " + VIDEO_PATH);
            SwingUtilities.invokeLater(() -> {
                stopVideo();
                videoFinished = true;
                videoPlaying = false;
            });
        }
    }

    private void stopVideo() {
        if (mediaPlayerComponent != null) {
            mediaPlayerComponent.mediaPlayer().controls().stop();
            mediaPlayerComponent.release();
        }
        if (videoFrame != null) {
            videoFrame.dispose();
            videoFrame = null;
        }
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
        if (videoPlaying && !videoFinished) {
            // Video is playing, check if we need to start it
            if (mediaPlayerComponent == null || videoFrame == null || !videoFrame.isVisible()) {
                playVideo();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (!videoPlaying) {
            if (btnRestart.contains(e.getPoint())) {
                // restart the game - reset player and go back to playing
                cleanup();
                game.resetGame();
                GameState.state = GameState.PLAYING;
                resetVideoState();

            } else if (btnMainMenu.contains(e.getPoint())) {
                // go back to main menu
                cleanup();
                GameState.state = GameState.MENU;
                resetVideoState();
            }
        }
    }

    private void resetVideoState() {
        videoPlaying = true;
        videoFinished = false;
        videoStartTime = System.currentTimeMillis();
        // Clean up any existing video components
        stopVideo();
    }

    public void startVideo() {
        resetVideoState();
    }

    public boolean isVideoPlaying() {
        return videoPlaying && !videoFinished;
    }

    public void cleanup() {
        stopVideo();
    }
}