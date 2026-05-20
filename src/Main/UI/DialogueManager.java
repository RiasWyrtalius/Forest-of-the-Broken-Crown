package Main.UI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Entities.NPC;
import Main.Core.Game;
import Utils.DialogueData;
import Utils.LoadSave;
import Utils.Constants;

import static Main.Core.Game.*;
import static Utils.Constants.NPCConstants.getName;

public class DialogueManager {
    private String[] currentDialogue;
    private int activeNpcID;
    private int lineIndex = 0;
    private int visibleTextIndex = 0;
    private int tickCounter = 0;
    private boolean active = false;
    private Font customFont;
    private Font dialogueFont;

    private ArrayList<String> wrappedLines = new ArrayList<>();

    // Sprite Handling
    private BufferedImage[][] npcAnimations;
    private int aniIndex, aniTick;
    private final int aniSpeed = 25;
    private int currentAniRow = 0;

    // State Flags
    private boolean isWakingUp = false;
    private boolean wakeUpComplete = false;

    private final int TYPE_SPEED = 2;

    public DialogueManager() {
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(24f);
        dialogueFont = LoadSave.getFont("Font/VCR.ttf").deriveFont(8f);

        npcAnimations = new BufferedImage[0][0];
    }

    public void startDialogue(String[] lines, NPC npc) {
        this.currentDialogue = lines;
        this.activeNpcID = npc.getNID();
        this.lineIndex = 0;
        this.visibleTextIndex = 0;
        this.active = true;

        prepareLines(lines[lineIndex]);

        this.aniIndex = 0;
        this.aniTick = 0;

        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(24f);
        dialogueFont = LoadSave.getFont("Font/VCR.ttf").deriveFont(10f);

        setSpriteRow(npc);
        loadNPCAnimations(npc);

        if (npcAnimations != null && npcAnimations.length > 0) {
            if (currentAniRow >= npcAnimations.length) {
                currentAniRow = npcAnimations.length - 1;
            }
        }
    }

    private void setSpriteRow(NPC npc) {
        String name = getName(npc.getNID());

        if (name.contains("Denver")) {
            this.currentAniRow = 1; // waking up
            this.isWakingUp = true;
            this.wakeUpComplete = false;
        } else if (name.contains("Queer")) {
            this.currentAniRow = 0;
            this.isWakingUp = false;
            this.wakeUpComplete = true;
        } else {
            this.currentAniRow = 1;
            this.isWakingUp = false;
            this.wakeUpComplete = true;
        }
    }

    private void loadNPCAnimations(NPC npc) {
        String path = Constants.NPCConstants.getSpritePath(npc.getNID());
        BufferedImage atlas = LoadSave.getSpriteAtlas(path);

        int maxRowsAvailable = atlas.getHeight() / SPRITE_DEFAULT_SIZE;
        npcAnimations = new BufferedImage[maxRowsAvailable][];

        for (int row = 0; row < maxRowsAvailable; row++) {
            int rowWidth = atlas.getWidth();
            int spritesInThisRow = 0;

            for (int i = 0; i < 10; i++) {
                if ((i + 1) * SPRITE_DEFAULT_SIZE <= rowWidth) spritesInThisRow++;
                else break;
            }

            if (getName(npc.getNID()).contains("Denver")) {
                if (row == 1) spritesInThisRow = 6;
                if (row == 2) spritesInThisRow = 8;
            }

            npcAnimations[row] = new BufferedImage[spritesInThisRow];
            for (int i = 0; i < spritesInThisRow; i++) {
                npcAnimations[row][i] = atlas.getSubimage(i * 64, row * 64, 64, 64);
            }
        }
    }

    public void update() {
        if (!active) return;

        if (npcAnimations == null || npcAnimations.length == 0 || currentAniRow >= npcAnimations.length || npcAnimations[currentAniRow] == null) {
            return;
        }

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            if (aniIndex >= npcAnimations[currentAniRow].length) {
                if (isWakingUp && !wakeUpComplete) {
                    isWakingUp = false;
                    wakeUpComplete = true;
                    currentAniRow = 2;
                }
                aniIndex = 0;
            }
        }

        // Update text
        if (wakeUpComplete) {
            tickCounter++;
            if (tickCounter >= TYPE_SPEED) {
                tickCounter = 0;
                if (visibleTextIndex < currentDialogue[lineIndex].length()) {
                    visibleTextIndex++;
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (!active) return;

        int margin = 50;
        int boxY = GAME_HEIGHT - 170;
        int boxH = 130;
        int portW = 130;
        int portX = margin;
        int textX = portX + portW + 10;
        int textW = GAME_WIDTH - textX - margin;

        g.setColor(new Color(0, 0, 0, 230));
        g.fillRect(portX, boxY, portW, boxH);
        g.fillRect(textX, boxY, textW, boxH);
        g.setColor(new Color(211, 211, 211));
        g.drawRect(portX, boxY, portW, boxH);
        g.drawRect(textX, boxY, textW, boxH);

        if (npcAnimations != null && currentAniRow < npcAnimations.length) {
            if (npcAnimations[currentAniRow] != null && npcAnimations[currentAniRow].length > 0) {
                int safeIndex = Math.min(aniIndex, npcAnimations[currentAniRow].length - 1);
                g.drawImage(npcAnimations[currentAniRow][safeIndex], portX + 5, boxY + 5, portW - 10, boxH - 10, null);
            }
        }

        g.setFont(customFont.deriveFont(Font.BOLD, 28f));
        g.setColor(Color.YELLOW);
        g.drawString(getName(activeNpcID), textX + 5, boxY - 10);

        if (wakeUpComplete) {
            g.setFont(dialogueFont.deriveFont(24f));
            g.setColor(Color.WHITE);

            int lineHeight = g.getFontMetrics().getHeight();
            int drawY = boxY + 50;
            int charsRevealed = visibleTextIndex;

            for (String line : wrappedLines) {
                if (charsRevealed <= 0) break; //nothing left

                if (charsRevealed >= line.length()) {
                    //visible
                    g.drawString(line, textX + 20, drawY);
                    charsRevealed -= line.length();
                } else {
                    // only part of this line is visible
                    g.drawString(line.substring(0, charsRevealed), textX + 20, drawY);
                    charsRevealed = 0;
                }
                drawY += lineHeight;
            }
        }
    }

    public boolean isActive() { return active; }

    //dialogue stuff

    public void skipOrNext() {
        if (!wakeUpComplete) return;

        if (visibleTextIndex < currentDialogue[lineIndex].length()) {
            //skip fx
            visibleTextIndex = currentDialogue[lineIndex].length();
        } else {
            // next line
            lineIndex++;
            if (lineIndex < currentDialogue.length) {
                visibleTextIndex = 0;
                prepareLines(currentDialogue[lineIndex]);
            } else {
                active = false;
            }
        }
    }

    private void prepareLines(String fullText) {
        wrappedLines.clear();
        int maxWidth = Game.GAME_WIDTH - 270;
        String[] words = fullText.split(" ");
        StringBuilder currentLine = new StringBuilder();

        Canvas c = new Canvas();
        FontMetrics fm = c.getFontMetrics(dialogueFont.deriveFont(24f));

        for (String word : words) {
            if (fm.stringWidth(currentLine + word) < maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                wrappedLines.add(currentLine.toString());
                currentLine = new StringBuilder(word + " ");
            }
        }
        wrappedLines.add(currentLine.toString());
    }
}