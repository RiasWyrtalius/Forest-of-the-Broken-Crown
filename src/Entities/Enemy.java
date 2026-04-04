package Entities;

import java.awt.*;

public abstract class Enemy extends Entity {

    protected boolean active = true;

    public Enemy(float x, float y, int width, int height) {
        super(x, y, width, height);
    }

    public abstract void update(int[][] lvlData);

    public abstract void render(Graphics g);

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}