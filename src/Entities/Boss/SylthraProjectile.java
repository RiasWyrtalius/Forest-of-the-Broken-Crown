package Entities.Boss;

import Entities.Player;
import Main.Core.Game;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class SylthraProjectile {
    private Rectangle2D.Float hitbox;
    private boolean active = true;
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, row = 0;

    public SylthraProjectile(float x, float y, BufferedImage[][] animations) {
        this.animations = animations;
        this.hitbox = new Rectangle2D.Float(x, y, 48 * Game.SCALE, 48 * Game.SCALE);
    }

    public void update(Player player) {
        if (!active) return;

        // Move
        hitbox.x -= (2.5f * Game.SCALE);

        // Check Hit
        if (hitbox.intersects(player.getHitbox())) {
            player.changeHealth(-1);
            player.applyKnockback(hitbox.x);
            active = false;
        }

        // Animation
        aniTick++;
        if (aniTick >= 20) {
            aniTick = 0;
            aniIndex++;
            if (row == 0 && aniIndex >= 3) {
                row = 1; // Switch to full loop
                aniIndex = 0;
            } else if (row == 1 && aniIndex >= 3) {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (active) {
            g.drawImage(animations[row][aniIndex],
                    (int)(hitbox.x - xLvlOffset), (int)(hitbox.y - yLvlOffset),
                    (int)hitbox.width, (int)hitbox.height, null);
        }
    }
    public boolean isActive() { return active; }
}