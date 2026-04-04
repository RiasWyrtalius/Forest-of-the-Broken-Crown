package Entities;

import java.awt.*;

public class Crabby extends Enemy {

    public Crabby(float x, float y) {
        super(x, y, 32, 32);
        initHitbox(x, y, 32, 32);
    }

    @Override
    public void update(int[][] lvlData) {
        // TODO: Implement Crabby AI behavior
    }

    @Override
    public void render(Graphics g) {
        // TODO: Implement Crabby rendering
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);
    }
}