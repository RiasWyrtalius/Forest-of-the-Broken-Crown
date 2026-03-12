package Entities.Projectiles;

import java.awt.*;

public class Projectile {
    private int x, y;
    private int speed;
    private int direction;
    private boolean active = true;

    public Projectile(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = 5;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void update() {
        x += (speed * direction);
    }

    //TODO: replace with animated projectile
    public void draw(Graphics g) {
        if (active) {
            g.setColor(Color.CYAN);
            g.fillOval(x, y, 15, 15);
        }
    }
}
