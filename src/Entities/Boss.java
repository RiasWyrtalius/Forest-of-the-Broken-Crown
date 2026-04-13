package Entities;

import static Utils.HelpMethods.*;

import Main.Game;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Boss extends Entity {

    private BufferedImage sprite;
    private int[][] lvlData;
    private float speed = 1.5f;
    private int health = 10;
    private long lastAttackTime;
    private long attackCd = 2000; // 2 seconds
    private float attackRange = 200; // pixels

    public Boss(float x, float y, int width, int height, int[][] lvlData) {
        super(x, y, width, height);
        this.lvlData = lvlData;
        loadSprite();
        initHitbox(width, height);
    }

    private void loadSprite() {
        sprite = LoadSave.getSpriteAtlas(LoadSave.EMBRYN_ATLAS);
    }

    public void update(Entities.Player player) {
        // Move towards player
        float dx = player.getHitbox().x - hitbox.x;
        float dy = player.getHitbox().y - hitbox.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            float moveX = (dx / dist) * speed;
            float moveY = (dy / dist) * speed;
            // Check horizontal movement
            if (CanMoveHere(hitbox.x + moveX, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x += moveX;
                this.x = hitbox.x;
            }
            // Check vertical movement
            if (CanMoveHere(hitbox.x, hitbox.y + moveY, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += moveY;
                this.y = hitbox.y;
            }
        }
    }

    public void render(Graphics g, int xLvlOffset) {
        g.drawImage(sprite, (int) (x - xLvlOffset), (int) y, width, height, null);
        // Uncomment to draw hitbox for debugging
        drawHitbox(g, xLvlOffset);
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public void reset() {
        health = 10;
        // Reset position to initial spawn point
        int[][] lvlData = this.lvlData; // Assuming we have access to level data
        // Hard-coded boss position: 50 blocks right and 5 blocks down from player spawn (200, 200)
        float bossX = 200 + 53 * Game.TILES_SIZE;
        float bossY = 200 + 4 * Game.TILES_SIZE;
        hitbox.x = bossX;
        hitbox.y = bossY;
        this.x = bossX;
        this.y = bossY;
    }
}
