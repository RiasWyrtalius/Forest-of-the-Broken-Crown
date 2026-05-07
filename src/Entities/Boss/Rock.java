package Entities.Boss;

import Main.Core.Game;
import Utils.LoadSave;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Rock {
    private Rectangle2D.Float hitbox;
    private float speed;
    private boolean active = true;
    private int aniTick, aniIndex;
    private BufferedImage[] imgs;
    private int rockSize;
    private float sizeMult;
    private float padding;

    public Rock(float x, float y, float sizeMultiplier) {
        this.sizeMult = sizeMultiplier;

        this.rockSize = (int) (64 * sizeMultiplier * Game.SCALE);
        this.speed = (1.5f + sizeMultiplier) * Game.SCALE;
        this.padding = 12 * sizeMultiplier * Game.SCALE;
        float hbWidth = rockSize - (padding * 2);
        float hbHeight = rockSize - (padding * 2);
        this.hitbox = new Rectangle2D.Float(x + padding, y + padding, hbWidth, hbHeight);
        loadImgs();
    }

    private void loadImgs() {
        BufferedImage temp = LoadSave.getSpriteAtlas(LoadSave.KAELOR_ATK_ATLAS);
        imgs = new BufferedImage[4];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = temp.getSubimage(i * 64, 0, 64, 64);
        }
    }

    public void update(int[][] lvlData) {
        hitbox.y += speed;
        updateAnimation();
    }

    private void updateAnimation() {
        aniTick++;
        if (aniTick >= 25) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 4) aniIndex = 0;
        }
    }

    public void draw(Graphics g, int xOffset, int yOffset) {
        int visualX = (int) (hitbox.x - padding - xOffset);
        int visualY = (int) (hitbox.y - padding - yOffset);

        g.drawImage(imgs[aniIndex], visualX, visualY, rockSize, rockSize, null);

        //hitbox
//        g.setColor(Color.PINK);
//        g.drawRect((int)(hitbox.x - xOffset), (int)(hitbox.y - yOffset), (int)hitbox.width, (int)hitbox.height);
    }

    public boolean isActive() { return active; }
    public Rectangle2D.Float getHitbox() { return hitbox; }
    public float getSizeMult() { return sizeMult;}
}