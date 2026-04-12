package Entities;

import Entities.Projectiles.Projectile;
import static Utils.HelpMethods.*;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Boss extends Entity {

    private BufferedImage sprite;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
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

        // Attack if close
        if (dist < attackRange) {
            long currTime = System.currentTimeMillis();
            if (currTime - lastAttackTime >= attackCd) {
                // Shoot projectile towards player
                int direction = (dx > 0) ? 1 : -1;
                float spawnX = hitbox.x + (direction == 1 ? hitbox.width : 0);
                float spawnY = hitbox.y + hitbox.height / 2;
                projectiles.add(new Projectile((int) spawnX, (int) spawnY, direction));
                lastAttackTime = currTime;
            }
        }

        // Update projectiles
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if (p.isActive()) {
                p.update();
            } else {
                projectiles.remove(i);
                i--;
            }
        }
    }

    public void render(Graphics g, int xLvlOffset) {
        g.drawImage(sprite, (int) (x - xLvlOffset), (int) y, width, height, null);
        // Uncomment to draw hitbox for debugging
        drawHitbox(g, xLvlOffset);
        // Render projectiles
        for (Projectile p : projectiles) {
            p.draw(g, xLvlOffset);
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public void reset() {
        health = 10;
        projectiles.clear();
        // Reset position to initial spawn point
        int[][] lvlData = this.lvlData; // Assuming we have access to level data
        // Hard-coded boss position: 50 blocks right and 5 blocks down from player spawn (200, 200)
        float bossX = 200 + 53 * 32 * 1.5f; // TILES_SIZE * SCALE
        float bossY = 200 + 4 * 32 * 1.5f;
        hitbox.x = bossX;
        hitbox.y = bossY;
        this.x = bossX;
        this.y = bossY;
    }
}
