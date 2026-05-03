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
    protected boolean attackChecked = false;
    protected boolean firstUpdate = true;
    protected int enemyState;
    protected float walkSpeed = 0.35f * Game.SCALE;
    protected int animationTick, animationIndex;
    protected int walkDir = LEFT;
    protected float attackDistance;
    protected int maxHealth, currentHealth;

    //invis frames
    public boolean invincible = false;
    public int invincibleCounter = 0;
    private final int INVINCIBILITY_TIME = 100;

    //flag
    protected boolean playerInSight = false;
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
            if (IsFloor(hitbox, xSpeed, lvlData)) {
                hitbox.x += xSpeed;
                return;
            }
        }
        changeWalkDir();
    }

    protected void newState(int enemyState) {
        if (this.enemyState == enemyState)
            return;

        this.enemyState = enemyState;
        animationTick = 0;
        animationIndex = 0;

        if (enemyState == ATTACK) {
            attackChecked = false;
        }
    }

    public void hurt(int amount) {

        if (invincible) return;

        currentHealth -= amount;

        invincible = true;
        invincibleCounter = 0;

        if (currentHealth <= 0)
            newState(DEAD);
        else
            newState(HIT);
    }

    protected void updateHealthStatus() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > INVINCIBILITY_TIME) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Player player) {
        if (!attackChecked && attackBox.intersects(player.getHitbox())) {
            player.changeHealth(-getEnemyDamage());
            attackChecked = true;
        }
    }

    protected void updateAnimationTick(int[] spriteAmount) {
        animationTick++;
        if (animationTick >= ANI_SPEED) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= spriteAmount[enemyState]) {
                animationIndex = 0;

                switch (enemyState) {
                    case DETECT -> newState(RUNNING);
                    case ATTACK, HIT -> {
                        newState(IDLE);
                    }
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
            walkDir = RIGHT;
        } else {
            walkDir = LEFT;
        }
    }

    public boolean isActive() {
        return active;
    }

    protected void turnTowardsPlayer(Player player) {
        if (player.getHitbox().x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    //detection logic
    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerFeetTileY = (int) ((player.getHitbox().y + player.getHitbox().height - 1) / Game.TILES_SIZE);
        int enemyFeetTileY = (int) ((hitbox.y + hitbox.height - 1) / Game.TILES_SIZE);

        if (Math.abs(playerFeetTileY - enemyFeetTileY) <= 1) {
            if (isPlayerInRange(player)) {
                if (isSightClear(lvlData, hitbox, player.getHitbox(), enemyFeetTileY)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isPlayerInRange(Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - hitbox.x);
        return absValue <= attackDistance * 20;
    }

    protected boolean isPlayerCloseForAttack(Player player) {
        float bossCenterX = hitbox.x + (hitbox.width / 2);
        float playerCenterX = player.getHitbox().x + (player.getHitbox().width / 2);

        int distance = (int) Math.abs(bossCenterX - playerCenterX);

        return distance <= (hitbox.width / 2) + (player.getHitbox().width / 2) + attackDistance;
    }
}
