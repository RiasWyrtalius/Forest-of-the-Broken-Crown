package Entities.Boss;

import Main.Core.Game;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Star {
    public Rectangle2D.Float hitbox;
    public boolean collected = false;
    private BufferedImage[] animations;
    private int aniTick, aniIndex;

    public Star(float x, float y, BufferedImage[] animations) {
        this.animations = animations;
        this.hitbox = new Rectangle2D.Float(x, y, 64 * Game.SCALE, 64 * Game.SCALE);
    }

    public void update() {
        if (collected) return;
        aniTick++;
        if (aniTick >= 25) { // Match standard animation speed
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 6) aniIndex = 0;
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!collected) {
            g.drawImage(animations[aniIndex],
                    (int)(hitbox.x - xLvlOffset),
                    (int)(hitbox.y - yLvlOffset),
                    (int)hitbox.width, (int)hitbox.height, null);
        }
    }
}