package Entities.Boss;
import Entities.Entity;
import Entities.Player;
import Main.Core.Game;

import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.GRAVITY;
import static Utils.HelpMethods.*;
import static Utils.Constants.LEFT;
import static Utils.Constants.RIGHT;
import java.awt.geom.Rectangle2D;


public abstract class Enemy extends Entity {

    protected int enemyType;
    protected boolean firstUpdate = true;
    protected int enemyState;
    protected float walkSpeed = 0.35f * Game.SCALE;
    protected int animationTick, animationIndex;
    protected int walkDir = LEFT;
    protected float attackDistance = 1.5f * Game.TILES_SIZE;
    protected int maxHealth, currentHealth;
    protected boolean active = true;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
    }

    protected abstract int getEnemyDamage();
    protected abstract int getEnemyMaxHealth();

    protected void initHealth() {
        this.maxHealth = getEnemyMaxHealth();
        this.currentHealth = maxHealth;
    }

    protected void firstUpdateCheck(int[][] lvlData) {
        if (!isEntityOnFloor(hitbox, lvlData))
            inAir = true;
        firstUpdate = false;
    }

    protected void updateInAir(int[][] lvlData) {
        if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y += airSpeed;
            airSpeed += GRAVITY;
        } else {
            inAir = false;
            hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
        }
    }

    protected void move(int[][] lvlData) {
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;

        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            changeWalkDir();
        }
    }

    protected void turnTowardsPlayer(Player player) {
        if (player.getHitbox().x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
        int enemyTileY = (int) (hitbox.y / Game.TILES_SIZE);

        if (Math.abs(playerTileY - enemyTileY) <= 2)
            if (isPlayerInRange(player))
                if (isSightClear(lvlData, hitbox, player.getHitbox(), enemyTileY))
                    return true;

        return false;
    }

    protected boolean isPlayerInRange(Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - hitbox.x);
        return absValue <= attackDistance * 5;
    }

    protected boolean isPlayerCloseForAttack(Player player) {
        int distance = (int) Math.abs(player.getHitbox().x - hitbox.x);
        return distance <= attackDistance + (hitbox.width / 2);
    }

    protected void newState(int enemyState) {
        this.enemyState = enemyState;
        animationTick = 0;
        animationIndex = 0;
    }

    public void hurt(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0)
            newState(DEAD);
        else
            newState(HIT);
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Player player) {
        if (attackBox.intersects(player.getHitbox()))
            player.changeHealth(-getEnemyDamage());
    }

    protected void updateAnimationTick(int[] spriteAmount) {
        animationTick++;
        if (animationTick >= ANI_SPEED) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= spriteAmount[enemyState]) {
                animationIndex = 0;
                switch (enemyState) {
                    case ATTACK, HIT -> newState(IDLE);
                    case DEAD -> active = false;
                }
            }
        }
    }

    protected void initHitbox(float width, float height) {
        float groundedY = y - (int)(height * Game.SCALE) + Game.TILES_SIZE;

        hitbox = new Rectangle2D.Float(x, groundedY,
                (int)(width * Game.SCALE),
                (int)(height * Game.SCALE));
    }

    protected void changeWalkDir() {
        if (walkDir == LEFT) {
            walkDir = LEFT;
        } else
            walkDir = RIGHT;
    }

    public boolean isActive() {
        return active;
    }
}
