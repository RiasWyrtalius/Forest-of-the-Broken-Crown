package Objects;

import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class GameObject {
    protected float x, y;
    protected int objectType;
    protected Rectangle2D.Float hitbox;
    protected boolean active = true;

    public GameObject(float x, float y, int objectType) {
        this.x = x;
        this.y = y;
        this.objectType = objectType;
    }

    protected void initHitbox(float w, float h) {
        hitbox = new Rectangle2D.Float(x, y, w * Game.SCALE, h * Game.SCALE);
    }

    public void drawHitbox(Graphics g, int xLevelOffset) {
        g.setColor(Color.PINK);
        g.drawRect((int) (hitbox.x - xLevelOffset), (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Rectangle2D.Float getHitbox() { return hitbox; }
}
