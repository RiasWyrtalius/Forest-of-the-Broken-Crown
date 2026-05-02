package Entities;

import Main.Core.Game;
import Utils.Constants;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Utils.Constants.NPCConstants.*;

public class NPC extends Entity {
    private int npcID;
    private String[] dialogue;
    private boolean isHovered = false;
    private boolean canSave = false;
    private BufferedImage[] npcImages;
    private static final int SOURCE_SIZE = 64;
    private int xDrawOffset, yDrawOffset;

    public NPC(float x, float y, int width, int height, int npcID, String[] dialogue, boolean canSave) {
        super(x, y,
                (int)(SOURCE_SIZE * getScale(npcID)),
                (int)(SOURCE_SIZE * getScale(npcID)));

        this.npcID = npcID;
        this.dialogue = dialogue;
        this.canSave = canSave;

        this.xDrawOffset = (width - Game.TILES_SIZE) / 2;
        this.yDrawOffset = (int) (getYOffset(npcID) * Game.SCALE);

        initHitbox(Game.TILES_SIZE, height);

        loadNPCImages();
    }

    public void update() {
        updateAnimationTick();
    }

    private void updateAnimationTick() {
        animationTick++;
        if (animationTick >= 30) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= npcImages.length) animationIndex = 0;
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        int drawX = (int)(hitbox.x - xLvlOffset) - xDrawOffset;
        int drawY = (int)(hitbox.y + hitbox.height) - height - yLvlOffset + yDrawOffset;

        if (isHovered) {
            g.setColor(Color.WHITE);

            int headNudge = getHeadNudge(npcID);
            int sideNudge = getSideNudge(npcID);

            int arrowCenterX = (int)(hitbox.x - xLvlOffset + (hitbox.width / 2)) + sideNudge;
            int arrowTopY = (int)(hitbox.y - headNudge) - yLvlOffset;

            int[] xPoints = {arrowCenterX - 8, arrowCenterX + 8, arrowCenterX};
            int[] yPoints = {arrowTopY, arrowTopY, arrowTopY + 10};

            g.fillPolygon(xPoints, yPoints, 3);
        }

        if (npcImages != null && npcImages[animationIndex] != null) {
            g.drawImage(npcImages[animationIndex], drawX, drawY, width, height, null);
        }

        //ddrawHitbox(g, xLvlOffset, yLvlOffset);
    }

    private void loadNPCImages() {
        String path = Constants.NPCConstants.getSpritePath(npcID);
        int amount = Constants.NPCConstants.getSpriteAmount(npcID);

        BufferedImage temp = LoadSave.getSpriteAtlas(path);
        npcImages = new BufferedImage[amount];

        for (int i = 0; i < npcImages.length; i++) {
            npcImages[i] = temp.getSubimage(
                    i * SOURCE_SIZE, // Column * 64
                    0,
                    SOURCE_SIZE,     // 64
                    SOURCE_SIZE      // 64
            );
        }
    }

    public void setHovered(boolean hovered) { this.isHovered = hovered; }
    public String[] getDialogue() { return dialogue; }
    public int getNID() { return npcID; }
    public boolean isSavePoint() { return canSave; }
}
