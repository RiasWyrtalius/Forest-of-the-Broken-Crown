package Main.UI;

import java.awt.*;
import Main.Core.Game;
import Utils.LoadSave;

public class DialogueManager {
    private String[] currentDialogue;
    private int lineIndex = 0;
    private int visibleTextIndex = 0;
    private int tickCounter = 0;
    private boolean active = false;
    private boolean autoPlay = false;
    private Font customFont;

    private final int TYPE_SPEED = 2; //bigger the value the slower

    public void startDialogue(String[] lines) {
        this.currentDialogue = lines;
        this.lineIndex = 0;
        this.visibleTextIndex = 0;
        this.active = true;
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(24f);
    }

    public void update() {
        if (!active) return;

        tickCounter++;
        if (tickCounter >= TYPE_SPEED) {
            tickCounter = 0;
            if (visibleTextIndex < currentDialogue[lineIndex].length()) {
                visibleTextIndex++;
            } else if (autoPlay) {
                // Potential logic for auto-advancing
            }
        }
    }

    public void draw(Graphics g) {
        if (!active) return;
        g.setFont(customFont);
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, Game.GAME_HEIGHT - 150, Game.GAME_WIDTH - 100, 120);
        g.setColor(Color.WHITE);
        g.drawRect(50, Game.GAME_HEIGHT - 150, Game.GAME_WIDTH - 100, 120);

        g.setColor(Color.YELLOW);
        g.drawString("Niño", 70, Game.GAME_HEIGHT - 160);

        g.setColor(Color.WHITE);
        String visibleText = currentDialogue[lineIndex].substring(0, visibleTextIndex);
        g.drawString(visibleText, 70, Game.GAME_HEIGHT - 110);
    }

    public void skipOrNext() {
        // If text is still typing, skip to the end of the line
        if (visibleTextIndex < currentDialogue[lineIndex].length()) {
            visibleTextIndex = currentDialogue[lineIndex].length();
        } else {
            // Move to next line or close
            lineIndex++;
            if (lineIndex < currentDialogue.length) {
                visibleTextIndex = 0;
            } else {
                active = false;
            }
        }
    }

    public boolean isActive() { return active; }
    public void setAutoPlay(boolean auto) { this.autoPlay = auto; }
}
