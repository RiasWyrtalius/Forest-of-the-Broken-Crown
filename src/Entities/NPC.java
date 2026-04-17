package Entities;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC extends Entity {
    private int npcID;
    private String[] dialogue;
    private boolean isHovered = false;
    private BufferedImage[] npcImages;

    public NPC(float x, float y, int width, int height, int npcID, String[] dialogue) {
        super(x, y, width, height);
        this.npcID = npcID;
        this.dialogue = dialogue;
        initHitbox(width, height);
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
            if (animationIndex >= 5) animationIndex = 0;
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        int drawX = (int)(hitbox.x - xLvlOffset) - (width - (int)hitbox.width) / 2;
        int drawY = (int)(hitbox.y + hitbox.height) - height;

        if (isHovered) {
            g.setColor(Color.WHITE);

            int arrowCenterX = (int)(hitbox.x - xLvlOffset + (hitbox.width / 2));
            int arrowTopY = (int)(hitbox.y + 5);

            int[] xPoints = {arrowCenterX - 8, arrowCenterX + 8, arrowCenterX};
            int[] yPoints = {arrowTopY, arrowTopY, arrowTopY + 10};

            g.fillPolygon(xPoints, yPoints, 3);
        }

        if (npcImages != null && npcImages[animationIndex] != null) {
            g.drawImage(npcImages[animationIndex], drawX, drawY, width, height, null);
        }
    }

    private void loadNPCImages() {
        BufferedImage temp = LoadSave.getSpriteAtlas(LoadSave.Nino_Atlas);
        npcImages = new BufferedImage[5];

        for (int i = 0; i < npcImages.length; i++) {
            npcImages[i] = temp.getSubimage(
                    i * Game.SPRITE_DEFAULT_SIZE, // X position (frame * width)
                    0,                              // Y position (row)
                    Game.SPRITE_DEFAULT_SIZE,        // Frame width
                    Game.SPRITE_DEFAULT_SIZE        // Frame height
            );
        }
    }

    public void setHovered(boolean hovered) { this.isHovered = hovered; }
    public String[] getDialogue() { return dialogue; }
}
