package Objects;

import Main.Core.Game;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Platform extends GameObject {

    private static BufferedImage[] imgs;
    private int spriteIndex;

    public Platform(float x, float y, int objectType, int spriteIndex) {
        super(x, y, objectType);
        this.spriteIndex = spriteIndex;

        if (imgs == null) {
            loadImgs();
        }

        initHitbox(32, 14);
    }

    private void loadImgs() {
        BufferedImage temp = LoadSave.getSpriteAtlas(LoadSave.PLATFORM_ATLAS);

        int cols = temp.getWidth() / 32;
        int rows = temp.getHeight() / 32;

        imgs = new BufferedImage[cols * rows];

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                int index = j * cols + i;
                imgs[index] = temp.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (imgs != null && spriteIndex < imgs.length) {
            g.drawImage(imgs[spriteIndex],
                    (int) (hitbox.x - xLvlOffset),
                    (int) (hitbox.y - yLvlOffset),
                    Game.TILES_SIZE, Game.TILES_SIZE, null);
        }
    }
}