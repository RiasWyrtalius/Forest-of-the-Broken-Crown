package Objects;

import Main.Core.Game;
import java.awt.*;
import java.awt.image.BufferedImage;

import static Utils.Constants.ObjectConstants.*;

public class Spike extends GameObject {
    private BufferedImage[] imgs;
    private int spriteIndex;
    private int spikeType;

    public Spike(float x, float y, int objectType, BufferedImage[] imgs, int spriteIndex) {
        super(x, y, objectType);
        this.imgs = imgs;
        this.spriteIndex = spriteIndex;
        this.spikeType = objectType;
        initDynamicHitbox();
    }

    private void initDynamicHitbox() {
        int size = 32;
        int thickness = 14;
        int margin = 6;

        int w = 0, h = 0;
        float xOff = 0, yOff = 0;

        switch (spriteIndex) {
            case SPIKE_FLOOR_MID, SPIKE_FLOOR_LEFT, SPIKE_FLOOR_RIGHT -> {
                w = size - (margin * 2);
                h = thickness;
                xOff = margin;
                yOff = size - thickness; //pin to bottom
            }

            case SPIKE_LEFT -> {
                w = thickness;
                h = size - (margin * 2);
                xOff = 0; // pin to left
                yOff = margin;
            }

            case SPIKE_CEILING -> {
                w = size - (margin * 2);
                h = thickness;
                xOff = margin;
                yOff = 0; // pin to top
            }

            //right wall spike (if ever added)
            default -> {
                w = size;
                h = size;
            }
        }

        //scale hitbox
        initHitbox(w, h);

        //snap hitbox
        hitbox.x = x + (xOff * Game.SCALE);
        hitbox.y = y + (yOff * Game.SCALE);
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.drawImage(imgs[spriteIndex], (int)(x - xLvlOffset), (int)(y - yLvlOffset),
                Game.TILES_SIZE, Game.TILES_SIZE, null);

        drawHitbox(g, xLvlOffset, yLvlOffset);
    }

    public int getSpriteIndex() { return spriteIndex; }
}
