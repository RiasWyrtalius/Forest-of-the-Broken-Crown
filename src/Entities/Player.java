package Entities;

import Entities.Projectiles.Projectile;
import Main.Game;
import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.*;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//TODO: ATTACK ANIMATION / ACTION - hold off for now since no sprites yet.
/**
 * NOTE : PROJECTILE will be replaced with an animated one.
 * */

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 30;
    private int playerAction = IDLE;
    private boolean moving = false;
    private boolean up, down, left, right, jump;
    private int faceDirection = WALKR;
    private float playerSpeed = 1.5f * Game.SCALE;

    //Sylvara
    private int sylvara_HitboxWidth = 33;
    private int sylvara_HitboxHeight = 45;

    //Hitbox
    private int[][] lvlData;
    private float xDrawOffset = 15 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    //Attack
    private long lastAttackTime;
    private long atkCd = 500;
    private boolean attacking = false;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    //Gravity / Jumping
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.23f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    //Lives
    public int maxLife = 5;
    public int life = maxLife;
    public boolean invincible = false;
    public int invincibleCounter = 0;
    private final int INVINCIBILITY_TIME = 200; // 200 UPS = 1 sec

    //TODO: implement death screen when lives == 0

    public Player(float x, float y, int width, int height, int[][] lvlData) {
        super(x, y, width, height);
        this.lvlData = lvlData;
        loadAnimations();
        initHitbox(x, y, sylvara_HitboxWidth * Game.SCALE, sylvara_HitboxHeight * Game.SCALE);
    }

    public void loseLife() {
        if (!invincible) {
            life--;
            invincible = true;

            if (life <= 0) {
                System.out.println("GAME OVER");
            }
        }
    }

    public void update() {
        updateAnimationTick();
        setAnimation();
        updatePos();

        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 50) { // quarter of a second at 200 UPS
                invincible = false;
                invincibleCounter = 0;
            }
        }

        if (attacking) shoot();
        updateProjectiles();
    }

    private void updateHealthStatus() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > INVINCIBILITY_TIME) { //cause 60fps
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

        //drawHitbox(g);
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

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
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

    public void setDirection(int direction) {
        moving = true;
    }

    public void updateAnimationTick() {
        animationTick++;
        if(animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if(animationIndex >= GetSpriteAmount(playerAction)) {
                animationIndex = 0;
            }
        }
    }

    private void setAnimation() {
        int startAni = playerAction;
        if(moving) {
            if(isRight()) playerAction = WALKR;
            if(isLeft()) playerAction = WALKL;
        }else {
            if (faceDirection == WALKL) {
                playerAction = WALKL;
            } else {
                playerAction = WALKR;
            }
        }

        if (startAni != playerAction) {
            animationTick = 0;
            animationIndex = 0;
        }

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
            xSpeed -= playerSpeed;
            faceDirection = WALKL;
        }
        if (right) {
            xSpeed += playerSpeed;
            faceDirection = WALKR;
        }

        if (!inAir) {
            if (!isEntityOnFloor(hitbox, lvlData)) inAir = true;
        }

        // 1. HANDLE VERTICAL
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
            } else {
                hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0) resetInAir();
                else airSpeed = fallSpeedAfterCollision;
            }
        }

        // 2. HANDLE HORIZONTAL
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

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = getEntityXPosNextToWall(hitbox, xSpeed);
        }
    }


    public void resetDirectionBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public boolean isUp() { return up; }
    public void setUp(boolean up) { this.up = up; }

    public boolean isDown() { return down; }
    public void setDown(boolean down) { this.down = down; }

    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }

    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }

    public void setJump (boolean jump) { this.jump = jump; }

    public ArrayList<Projectile> getProjectiles() { return projectiles; }

}
