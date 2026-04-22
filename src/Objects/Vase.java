package Objects;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Vase extends GameObject {
    private BufferedImage[] images;
    private int animationTick, animationIndex;
    private final int animationSpeed = 20;
    private boolean breaking = false;

    public Vase(float x, float y, int objectType) {
        super(x, y, objectType);
        initHitbox(Game.TILES_DEFAULT_SIZE, Game.TILES_DEFAULT_SIZE);
        loadImages();
    }

    private void loadImages() {
        BufferedImage atlas = LoadSave.getSpriteAtlas(LoadSave.VASE_ATLAS);
        images = new BufferedImage[4];
        for (int i = 0; i < images.length; i++) {
            images[i] = atlas.getSubimage(  i * Game.TILES_DEFAULT_SIZE,
                                            0,
                                            Game.TILES_DEFAULT_SIZE,
                                            Game.TILES_DEFAULT_SIZE);
        }
    }

    public void update() {
        if (breaking) {
            updateAnimation();
        }
    }

    private void updateAnimation() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= images.length) {
                active = false;
                breaking = false;
            }
        }
    }

    public void draw(Graphics g, int xLevelOffset, int yLvlOffset) {
        if ((active || breaking) && images != null && images[animationIndex] != null) {
            g.drawImage(images[animationIndex],
                    (int) (hitbox.x - xLevelOffset),
                    (int) hitbox.y - yLvlOffset,
                    (int) hitbox.width,
                    (int) hitbox.height,
                    null);
        }
    }

    public void reset() {
        active = true;
        breaking = false;
        animationIndex = 0;
        animationTick = 0;
    }

    public void setBreaking(boolean breaking) { this.breaking = breaking; }
    public boolean isBreaking() { return breaking; }
}
