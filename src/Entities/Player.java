package Entities;

import Entities.Projectiles.Projectile;
import Main.Game;

import static Utils.Constants.ANIMATION_SPEED;
import static Utils.Constants.GRAVITY;
import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.*;

import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int playerAction = IDLE;
    private boolean moving = false;
    private boolean left, right, jump;
    private int faceDirection = WALKR;

    //Sylvara
    private final int SYLVARA_HITBOX_WIDTH = 33;
    private final int SYLVARA_HITBOX_HEIGHT = 45;

    //Hitbox
    private final float xDrawOffset = 15 * Game.SCALE;
    private final float yDrawOffset = 4 * Game.SCALE;

    private int[][] lvlData;

    //Attack
    private long lastAttackTime;
    private long atkCd = 500;
    private boolean attacking = false;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    //Gravity / Jumping
    private float jumpSpeed = -2.23f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    //Lives
    public boolean invincible = false;
    public int invincibleCounter = 0;
    private final int INVINCIBILITY_TIME = 50; // 200 UPS = 1 sec / Quarter of a second

    //TODO: implement death screen when lives == 0

    public Player(float x, float y, int width, int height, int[][] lvlData) {
        super(x, y, width, height);
        this.lvlData = lvlData;
        this.maxLife = 5;
        this.life = maxLife;
        this.walkSpeed = 1.5f * Game.SCALE;
        loadAnimations();
        initHitbox(SYLVARA_HITBOX_WIDTH, SYLVARA_HITBOX_HEIGHT);
    }

    public void loseLife() {

        if (invincible) return;

        life--;
        invincible = true;
        invincibleCounter = 0;

        if (life <= 0) {
            resetAll(); //TODO: show game-over screen
        } else {
            hitbox.x = x;
            hitbox.y = y;
            inAir = true;
        }
    }

    public void update() {
        updateAnimationTick();
        setAnimation();
        updatePos();
        updateHealthStatus();

        /**
        * This can remove if projectiles isn't an option as an attack.
        * */
        if (attacking) shoot();
        updateProjectiles();
    }

    private void updateHealthStatus() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > INVINCIBILITY_TIME) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    /**
     * spawnX is used to determine the direction of the player.
     * spawnY is the spawnpoint of the projectile.
     * based on those two, it will update where the projectile will head.
      */
    public void shoot() {

        int projectileDirection = (faceDirection == WALKL) ? -1 : 1;

        long currTime = System.currentTimeMillis();
        if (currTime - lastAttackTime >= atkCd) {

            float spawnX = hitbox.x;
            if (projectileDirection == 1) {
                spawnX += hitbox.width;
            }

            float spawnY = hitbox.y + (hitbox.height) / 4;

            projectiles.add(new Projectile((int)spawnX, (int)spawnY, projectileDirection));
            lastAttackTime = currTime;
        }
    }

    public void render(Graphics g, int lvlOffset) {

        Graphics2D g2 = (Graphics2D) g;

        if (invincible) { // if invincible, transparency is 50%
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        g.drawImage(animations[playerAction][animationIndex], (int)(hitbox.x - xDrawOffset) - lvlOffset, (int)(hitbox.y - yDrawOffset), 80, 80, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        drawHitbox(g);
        for (Projectile p : projectiles) {
            p.draw(g);
        }
    }

    private void updateProjectiles() {
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

    public void loadAnimations() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.Sylvara_Atlas);
        animations = new BufferedImage[3][9];

        for(int i = 0; i < animations.length; i++) {
            for(int j = 0; j < animations[i].length; j++) {
                animations[i][j] = img.getSubimage(
                        j * Game.SPRITE_DEFAULT_SIZE,
                        i * Game.SPRITE_DEFAULT_SIZE,
                        Game.SPRITE_DEFAULT_SIZE,
                        Game.SPRITE_DEFAULT_SIZE
                );
            }
        }
    }

    public void updateAnimationTick() {
        animationTick++;
        if(animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            animationIndex++;
            if(animationIndex >= GetSpriteAmount(playerAction)) {
                animationIndex = 0;
            }
        }
    }

    private void setAnimation() {
        int prev = playerAction;

        if (moving) playerAction = isRight() ? WALKR : WALKL;
        else        playerAction = (faceDirection == WALKL) ? WALKL : WALKR;

        if (prev != playerAction) {
            animationTick = 0;
            animationIndex = 0;
        }

        //TODO: uncomment when ATK sprite (Jump Sprite) is ready.
//        if (attacking) {
//            playerAction = ATK_1;
//        }
    }

    private void updatePos() {
        moving = false;
        if (jump) jump();

        if (!left && !right && !inAir) return;

        float xSpeed = 0;
        if (left) {
            xSpeed -= walkSpeed;
            faceDirection = WALKL;
        }
        if (right) {
            xSpeed += walkSpeed;
            faceDirection = WALKR;
        }

        if (!inAir) {
            if (!isEntityOnFloor(hitbox, lvlData)) inAir = true;
        }

        // VERTICAL
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
            } else {
                hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0) resetInAir();
                else airSpeed = fallSpeedAfterCollision;
            }
        }

        // HORIZONTAL
        if (xSpeed != 0) {
            if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x += xSpeed;
                moving = true;
            } else {
                hitbox.x = getEntityXPosNextToWall(hitbox, xSpeed);
            }
        }
    }

    private void jump() {
        if (!inAir) {
            inAir = true;
            airSpeed = jumpSpeed;
        }
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    public void resetAll() {
        resetDirectionBooleans();
        inAir        = true;
        attacking    = false;
        moving       = false;
        playerAction = IDLE;
        life         = maxLife;

        // TODO: make this the location of the Player's save
        hitbox.x = x;
        hitbox.y = y;

        if (!isEntityOnFloor(hitbox, lvlData)) inAir = true;
    }

    public void resetDirectionBooleans() { left = right = false; }

    public void loadLvlData(int[][] lvlData) { this.lvlData = lvlData; }

    public float getAirSpeed() { return airSpeed; }
    public void setAirSpeed(float airSpeed) { this.airSpeed = airSpeed; }
    public float getJumpSpeed() { return jumpSpeed; }

    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }

    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }

    public void setJump (boolean jump) { this.jump = jump; }

    public ArrayList<Projectile> getProjectiles() { return projectiles; }

}
