package Objects;

import Main.Core.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class GameObject {
    protected float x, y;
    protected int objectType;
    protected Rectangle2D.Float hitbox;
    protected boolean doAnimation, active = true;
    protected int aniTick, aniIndex, aniSpeed = 25;

    public GameObject(float x, float y, int objectType) {
        this.x = x;
        this.y = y;
        this.objectType = objectType;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 5) {
                aniIndex = 0;
            }
        }
    }

    protected void initHitbox(float w, float h) {
        hitbox = new Rectangle2D.Float(x, y, w * Game.SCALE, h * Game.SCALE);
    }

    public void drawHitbox(Graphics g, int xLevelOffset) {
        g.setColor(Color.PINK);
        g.drawRect((int) (hitbox.x - xLevelOffset), (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
    }

    public int getObjType() { return objectType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Rectangle2D.Float getHitbox() { return hitbox; }
}
