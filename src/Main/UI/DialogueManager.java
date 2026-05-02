package Main.UI;

import java.awt.*;
import java.awt.image.BufferedImage;
import Entities.NPC;
import Main.Core.Game;
import Utils.LoadSave;
import Utils.Constants;
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

    // Sprite Handling
    private BufferedImage[][] npcAnimations;
    private int aniIndex, aniTick;
    private final int aniSpeed = 25;
    private int currentAniRow = 0;

    // State Flags
    private boolean isWakingUp = false;
    private boolean wakeUpComplete = false;

    private final int TYPE_SPEED = 2;

    public void startDialogue(String[] lines, NPC npc) {
        this.currentDialogue = lines;
        this.activeNpcID = npc.getNID();
        this.lineIndex = 0;
        this.visibleTextIndex = 0;
        this.active = true;
        this.aniIndex = 0;
        this.aniTick = 0;

        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(24f);
        dialogueFont = LoadSave.getFont("Font/VCR.ttf").deriveFont(20f);

        setSpriteRow(npc);
        loadNPCAnimations(npc);
    }

    private void setSpriteRow(NPC npc) {
        String name = getName(npc.getNID());

        if (name.contains("Denver")) {
            this.currentAniRow = 1; // Waking up row index
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

        int maxRowsAvailable = atlas.getHeight() / 64;
        npcAnimations = new BufferedImage[maxRowsAvailable][];

        for (int row = 0; row < maxRowsAvailable; row++) {
            int rowWidth = atlas.getWidth();
            int spritesInThisRow = 0;

            for (int i = 0; i < 10; i++) {
                if ((i + 1) * 64 <= rowWidth) spritesInThisRow++;
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

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            if (aniIndex >= npcAnimations[currentAniRow].length) {
                if (isWakingUp && !wakeUpComplete) {
                    isWakingUp = false;
                    wakeUpComplete = true;
                    currentAniRow = 2;
                    aniIndex = 0;
                } else {
                    aniIndex = 0;
                }
            }
        }

        // 2. Update Text
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
        int boxY = Game.GAME_HEIGHT - 170;
        int boxH = 130;
        int portW = 130;
        int portX = margin;
        int textX = portX + portW + 10;
        int textW = Game.GAME_WIDTH - textX - margin;

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
            String visibleText = currentDialogue[lineIndex].substring(0, visibleTextIndex);
            g.drawString(visibleText, textX + 20, boxY + 50);
        }
    }

    public void skipOrNext() {
        if (!wakeUpComplete) return;

        if (visibleTextIndex < currentDialogue[lineIndex].length()) {
            visibleTextIndex = currentDialogue[lineIndex].length();
        } else {
            lineIndex++;
            if (lineIndex < currentDialogue.length) {
                visibleTextIndex = 0;
            } else {
                active = false;
            }
        }
    }

    public boolean isActive() { return active; }
}