// Changes made by AI Assistant:
// - Added import for Main.Game to access SCALE constant.
// - Added abstract methods update(int[][] lvlData) and render(Graphics g) to define interface for enemy subclasses.
// - Added fields: inAir, airSpeed, gravity, active for enemy physics and state management.

package Entities;

import Main.Game;
import java.awt.Graphics;

public abstract class Enemy extends Entity {

    protected int health;
    protected int damage;
    protected boolean inAir = false;
    protected float airSpeed = 0f;
    protected float gravity = 0.04f * Game.SCALE;
    protected boolean active = true;

    public abstract void update(int[][] lvlData);
    public abstract void render(Graphics g);

    public Enemy(float x, float y, int width, int height, int health, int damage) {
        super(x, y, width, height);
        this.health = health;
        this.damage = damage;
    }

    public void takeDamage(int amount){
        health -= amount;
        if(health < 0) health = 0;
    }

    public boolean isDead(){
        return health <= 0;
    }

    public boolean isActive() {
        return active;
    }
}