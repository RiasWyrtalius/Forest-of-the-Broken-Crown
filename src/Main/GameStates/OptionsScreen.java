package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Main.UI.UI;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;

import static Audio.AudioPlayer.CLICK;

public class OptionsScreen {
    private Game game;
    private Font customFont;

    private int sliderX = Game.GAME_WIDTH / 2 - 150;
    private int sliderY = 300;
    private int sliderWidth = 300;
    private int sliderHeight = 10;

    private int handleX = sliderX + (sliderWidth / 2); // Starts at 50% volume
    private int handleWidth = 20;
    private int handleHeight = 30;
    private boolean draggingSlider = false;

    private Rectangle backBtn;
    private boolean isBackHovered = false;
    private int mouseX, mouseY;

    public OptionsScreen(Game game) {
        this.game = game;
        this.customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(24f);
        this.backBtn = new Rectangle(Game.GAME_WIDTH / 2 - 50, 500, 100, 40);
    }

    public void update() {}

    public void draw(Graphics g) {
        g.setColor(new Color(46, 34, 46));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setFont(customFont);

        //Title and Headers
        g.setColor(Color.WHITE);
        drawCenteredText(g);

        g.setColor(Color.GRAY);
        g.drawString("AUDIO", sliderX, sliderY - 30);

        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(sliderX, sliderY, sliderWidth, sliderHeight, 10, 10);

        g.setColor(new Color(255, 215, 0));
        int filledWidth = handleX - sliderX;
        g.fillRoundRect(sliderX, sliderY, filledWidth, sliderHeight, 10, 10);

        if (draggingSlider) {
            g.setColor(Color.WHITE); // Highlight when dragged
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRoundRect(handleX - (handleWidth / 2), sliderY - (handleHeight / 2) + (sliderHeight / 2), handleWidth, handleHeight, 5, 5);

        int volumePercent = (int) (((float) filledWidth / sliderWidth) * 100);
        g.setColor(Color.WHITE);
        g.drawString(volumePercent + "%", sliderX + sliderWidth + 20, sliderY + 10);

        UI.drawHoverableButton(g, backBtn.x, backBtn.y + 25, "BACK", isBackHovered, customFont);
    }

    private void drawCenteredText(Graphics g) {
        Font original = g.getFont();
        g.setFont(original.deriveFont((float) 40.0));
        FontMetrics metrics = g.getFontMetrics();
        int textX = 624 - (metrics.stringWidth("OPTIONS") / 2);
        g.drawString("OPTIONS", textX, 100);
        g.setFont(original);
    }

    // Mouse Stuff
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        Rectangle handleHitbox = new Rectangle(handleX - (handleWidth / 2), sliderY - (handleHeight / 2) + (sliderHeight / 2), handleWidth, handleHeight);
        Rectangle trackHitbox = new Rectangle(sliderX, sliderY - 10, sliderWidth, sliderHeight + 20);

        if (handleHitbox.contains(mx, my) || trackHitbox.contains(mx, my)) {
            draggingSlider = true;

            // snap
            handleX = mx;

            // insta clamp
            if (handleX < sliderX) handleX = sliderX;
            if (handleX > sliderX + sliderWidth) handleX = sliderX + sliderWidth;

            float volume = (float) (handleX - sliderX) / sliderWidth;
            game.getAudioPlayer().setVolume(volume);
        }
    }

    public void mouseReleased(MouseEvent e) {
        draggingSlider = false;

        if (backBtn.contains(e.getX(), e.getY())) {
            game.getAudioPlayer().playEffect(CLICK);
            GameState.state = GameState.MENU;
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (draggingSlider) {
            handleX = e.getX();

            if (handleX < sliderX) handleX = sliderX;
            if (handleX > sliderX + sliderWidth) handleX = sliderX + sliderWidth;

            float volume = (float) (handleX - sliderX) / sliderWidth;
            game.getAudioPlayer().setVolume(volume);
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        isBackHovered = backBtn.contains(mouseX, mouseY);
    }
}