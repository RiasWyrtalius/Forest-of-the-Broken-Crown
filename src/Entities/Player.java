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

    private PlayerCharacter characterData;

    //Hitbox
    private float xDrawOffset;
    private float yDrawOffset;

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

    public Player(float x, float y, int width, int height, int[][] lvlData, PlayerCharacter characterData) {
        super(x, y, width, height);
        this.characterData = characterData;
        this.lvlData = lvlData;
        this.xDrawOffset = characterData.xOffset * Game.SCALE;
        this.yDrawOffset = characterData.yOffset * Game.SCALE;
        this.maxLife = 5;
        this.life = maxLife;
        this.walkSpeed = 1.5f * Game.SCALE;
        loadAnimations();
        initHitbox(characterData.hitboxWidth, characterData.hitboxHeight);
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
        setAnimation();        // set the action first
        updateAnimationTick(); // then tick based on the correct action
        updatePos();
        updateHealthStatus();
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

        if (animations == null || animations[playerAction][animationIndex] == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        if (invincible) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        //airborne flip because it only has right
        int flipX = 0, flipW = 1;
        if (faceDirection == WALKL) {
            if (playerAction != WALKL) {
                flipX = width;
                flipW = -1;
            }
        }

        g.drawImage(animations[playerAction][animationIndex],
                (int)(hitbox.x - xDrawOffset) - lvlOffset + flipX,
                (int)(hitbox.y - yDrawOffset),
                width * flipW, height, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        drawHitbox(g, lvlOffset);
        for (Projectile p : projectiles) {
            p.draw(g, lvlOffset);
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

        String atlastPath = "";
        switch (characterData) {
            case KAELTHORN -> atlastPath = LoadSave.Kaelthron_Atlas;
            case SYLVARA -> atlastPath = LoadSave.Sylvara_Atlas;
            case EMBJORN -> atlastPath = LoadSave.Embjorn_Atlas;
        }

        BufferedImage img = LoadSave.getSpriteAtlas(atlastPath);
        this.animations = new BufferedImage[7][10];

        loadSingleAnimation(img, IDLE, characterData.rowIDLE, characterData.spriteA_IDLE);
        loadSingleAnimation(img, WALKR, characterData.rowWALKR, characterData.spriteA_WALKR);
        loadSingleAnimation(img, WALKL, characterData.rowWALKL, characterData.spriteA_WALKL);
        loadSingleAnimation(img, JUMP, characterData.rowJUMP, characterData.spriteA_JUMP);

        if (isValidRow(img, characterData.rowAIRBORNE))
            loadSingleAnimation(img, AIRBORNE, characterData.rowAIRBORNE, characterData.spriteA_AIRBORNE);

        if (isValidRow(img, characterData.rowDOUBLEJUMP))
            loadSingleAnimation(img, DOUBLEJUMP, characterData.rowDOUBLEJUMP, characterData.spriteA_DOUBLEJUMP);

        if (isValidRow(img, characterData.rowLANDING))
            loadSingleAnimation(img, LANDING, characterData.rowLANDING, characterData.spriteA_LANDING);
    }

    private boolean isValidRow(BufferedImage img, int row) {
        if (row == -1) return false;
        // Check if (row * size) is still inside the image height
        return (row * Game.SPRITE_DEFAULT_SIZE) + Game.SPRITE_DEFAULT_SIZE <= img.getHeight();
    }

    private void loadSingleAnimation(BufferedImage atlas, int actionType, int row, int amount) {
        for (int i = 0; i < amount; i++) {
            animations[actionType][i] = atlas.getSubimage(
                    i * Game.SPRITE_DEFAULT_SIZE,
                    row * Game.SPRITE_DEFAULT_SIZE,
                    Game.SPRITE_DEFAULT_SIZE,
                    Game.SPRITE_DEFAULT_SIZE
            );
        }
    }

    public void updateAnimationTick() {
        animationTick++;

        int speed = (playerAction == LANDING) ? 15 : ANIMATION_SPEED;

        if(animationTick >= speed) {
            animationTick = 0;
            animationIndex++;
            if(animationIndex >= getAmountByAction(playerAction)) {
                animationIndex = 0;
            }
        }
    }

    private int getAmountByAction(int action) {
        return switch (action) {
            case IDLE -> characterData.spriteA_IDLE;
            case WALKR -> characterData.spriteA_WALKR;
            case WALKL -> characterData.spriteA_WALKL;
            case JUMP -> characterData.spriteA_JUMP;
            case AIRBORNE -> characterData.spriteA_AIRBORNE;
            case DOUBLEJUMP -> characterData.spriteA_DOUBLEJUMP;
            case LANDING -> characterData.spriteA_LANDING;
            default -> 1;
        };
    }

    private void setAnimation() {
        int prev = playerAction;

        if (playerAction == LANDING) {
            if (animationIndex < characterData.spriteA_LANDING - 1) {
                return;
            }
        }

        if (moving) playerAction = isRight() ? WALKR : WALKL;
        else        playerAction = (faceDirection == WALKL) ? WALKL : WALKR;

        if (inAir) {
            if (airSpeed < 0) {
                playerAction = JUMP;
            } else {
                playerAction = AIRBORNE;
            }
        }

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

        if (airSpeed > 0) {
            playerAction = LANDING;
            animationTick = 0;
            animationIndex = 0;
        }

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
