package Objects;

import Main.Core.Game;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;
import static Utils.Constants.ObjectConstants.*;

public class Potion extends GameObject {

    private BufferedImage[] imgs;
    private float hoverOffset;
    private boolean up = true;

    private int pickupDelayTick = 0;
    private final int PICKUP_DELAY_MAX = 60;

    public Potion(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;
        this.objectType = objType;
        initHitbox(20, 20);
        loadImgs();
    }

    private void loadImgs() {
        String path = (objectType == HEALTH_POTION) ? LoadSave.HEALTH_POTION_ATLAS : LoadSave.MANA_POTION_ATLAS;
        BufferedImage temp = LoadSave.getSpriteAtlas(path);

        imgs = new BufferedImage[5];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = temp.getSubimage(i * 32, 0, 32, 32);
        }
    }

    public void update() {
        updateAnimationTick();
        updateHover();
        updatePickupDelay();
    }

    private void updatePickupDelay() {
        if (pickupDelayTick < PICKUP_DELAY_MAX) {
            pickupDelayTick++;
        }
    }

    public boolean canBePickedUp() {
        return pickupDelayTick >= PICKUP_DELAY_MAX;
    }

    private void updateHover() {
        hoverOffset = (up) ? hoverOffset + 0.05f : hoverOffset - 0.05f;
        if (hoverOffset >= 1) up = false;
        else if (hoverOffset <= 0) up = true;
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (active) {
            Graphics2D g2d = (Graphics2D) g;

            if (!canBePickedUp()) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            }

            g.drawImage(imgs[aniIndex],
                    (int) (hitbox.x - xLvlOffset - (6 * Game.SCALE)),
                    (int) (hitbox.y - (hoverOffset * 10) - (6 * Game.SCALE)),
                    (int) (32 * Game.SCALE),
                    (int) (32 * Game.SCALE),
                    null);
            drawHitbox(g, xLvlOffset);
        }
    }
}