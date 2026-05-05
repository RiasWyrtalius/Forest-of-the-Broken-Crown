package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DeathScreen {

    private Game game;
    private BufferedImage backgroundImg;
    private Font customFont;
    private boolean animationPlaying = true;
    private long animationStartTime;
    private final long ANIMATION_DURATION = 2000; // 2 seconds animation

    private int btnWidth = 300;
    private int btnHeight = 70;

    private Rectangle btnRestart = new Rectangle(160, 500, btnWidth, btnHeight);
    private Rectangle btnMainMenu = new Rectangle(800, 500, btnWidth, btnHeight);

    // Animation properties
    private final int START_FONT_SIZE = 24;
    private final int END_FONT_SIZE = 256;

    public DeathScreen(Game game) {
        this.game = game;
        this.animationStartTime = System.currentTimeMillis();
        this.backgroundImg = LoadSave.getSpriteAtlas(LoadSave.DeathScreen);
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
    }

    public void draw(Graphics g) {
        if (animationPlaying) {
            // Show animated "YOU DIED" text
            drawDeathAnimation(g);
        } else {
            // Show death screen menu
            drawDeathMenu(g);
        }
    }

    private void drawDeathAnimation(Graphics g) {
        // Black background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // Calculate animation progress (0.0 to 1.0)
        long elapsed = System.currentTimeMillis() - animationStartTime;
        float progress = Math.min((float) elapsed / ANIMATION_DURATION, 1.0f);

        // Calculate current font size using smooth interpolation
        int currentFontSize = START_FONT_SIZE + (int)((END_FONT_SIZE - START_FONT_SIZE) * progress);

        // Draw the animated "YOU DIED" text
        g.setColor(Color.RED);
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU DIED";
        int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
        int titleY = Game.GAME_HEIGHT / 2 + (fm.getAscent() / 2) - 20;

        g.drawString(title, titleX, titleY);

        // Optional: Add some visual effects like shadow or glow
        g.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
        g.drawString(title, titleX + 2, titleY + 2);
    }


    private void drawDeathMenu(Graphics g) {
        // title
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        // 2. Draw "YOU DIED" Title
        g.setColor(Color.RED);
        g.setFont(customFont.deriveFont(Font.BOLD, 72f)); // Made slightly larger for the background
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU DIED";
        int titleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2);
        g.drawString(title, titleX, 80);

        // 3. Draw Subtitle
        g.setColor(Color.WHITE);
        g.setFont(customFont.deriveFont(24f));
        String subtitle = "Your journey ends here...";
        fm = g.getFontMetrics();
        int subtitleX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(subtitle) / 2);
        g.drawString(subtitle, subtitleX, 110);

        // buttons
        drawButton(g, btnRestart, "Restart Game");
        drawButton(g, btnMainMenu, "Main Menu");
    }

    private void drawButton(Graphics g, Rectangle btn, String label) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(btn.x, btn.y, btn.width, btn.height);

        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);

        g.setFont(customFont);
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width  / 2) - (fm.stringWidth(label) / 2);
        int textY = btn.y + (btn.height / 2) + (fm.getAscent() / 2) - 2;
        g.setColor(Color.WHITE);
        g.drawString(label, textX, textY);
    }

    public void update() {
        if (animationPlaying) {
            // Check if animation is complete
            long elapsed = System.currentTimeMillis() - animationStartTime;
            if (elapsed >= ANIMATION_DURATION) {
                animationPlaying = false;
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (!animationPlaying) {
            if (btnRestart.contains(e.getPoint())) {
                cleanup();
                int currentLevel = game.getLevelHandler().getCurrentLevelNum();
                game.setupLevel(currentLevel);
                game.getPlayer().resetAll();
                game.getObjectManager().resetAllObjects();
                game.getPlaying().resetCamera();
                game.getPlaying().getEnemyManager().reset();
                game.getPlaying().loadEnemiesForLevel(currentLevel);
                GameState.state = GameState.PLAYING;
                resetAnimationState();
            }
        }
    }

    private void resetAnimationState() {
        animationPlaying = true;
        animationStartTime = System.currentTimeMillis();
    }

    public void startAnimation() {
        resetAnimationState();
    }

    public boolean isAnimationPlaying() {
        return animationPlaying;
    }

    public void cleanup() {
        // No cleanup needed for animation
    }
}