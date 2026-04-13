package Objects;

import Main.Core.Game;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Spike extends GameObject {
    private BufferedImage[] imgs;
    private int spriteIndex;

    public Spike(float x, float y, int objectType, BufferedImage[] imgs, int spriteIndex) {
        super(x, y, objectType);
        this.imgs = imgs;
        this.spriteIndex = spriteIndex;

        initHitbox(32, 16);
        hitbox.y += 16 * Game.SCALE;
    }

    public void draw(Graphics g, int xLvlOffset) {
        g.drawImage(imgs[spriteIndex], (int)(x - xLvlOffset), (int)(y),
                Game.TILES_SIZE, Game.TILES_SIZE, null);
    }
}
