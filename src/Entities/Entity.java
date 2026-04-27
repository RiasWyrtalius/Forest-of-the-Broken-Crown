package Entities;

import Main.Core.Game;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    protected float x, y;
    protected int width, height;
    protected Rectangle2D.Float hitbox;
    protected int animationTick, animationIndex;
    protected float airSpeed = 0f;
    protected boolean inAir = false;
    protected int maxLife;
    protected int life;
    protected float walkSpeed = 1.0f * Game.SCALE;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawHitbox(Graphics g, int xLvlOffset, int yLvlOffset){
        g.setColor(Color.PINK);
        g.drawRect((int)hitbox.x - xLvlOffset, (int)hitbox.y - yLvlOffset,
                (int)hitbox.width, (int)hitbox.height);
    }

    protected void initHitbox(float width, float height){
        hitbox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE), (int)(height * Game.SCALE));
    }

    public Rectangle2D.Float getHitbox(){
        return hitbox;
    }

    public int getMaxLife() { return maxLife; }
    public int getLife() { return life; }
}
