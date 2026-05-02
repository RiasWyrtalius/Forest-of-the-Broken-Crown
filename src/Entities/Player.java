package Entities;

import Entities.Skills.Skill;
import Main.Core.Game;
import Main.GameState;
import static Utils.Constants.ANIMATION_SPEED;
import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.*;

import Utils.HelpMethods;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int playerAction = IDLE;
    private boolean moving = false;
    private boolean left, right, jump, down;
    private int faceDirection = WALKR;

    private PlayerCharacter characterData;
    private Skill activeSkill;

    //Hitbox
    private float xDrawOffset;
    private float yDrawOffset;

    private int[][] lvlData;

    //Gravity / Jumping
    private float jumpSpeed = -2.41f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    private boolean jumpPressed;
    private float extraHSpeed = 0;

    //Lives
    public boolean invincible = false;
    public int invincibleCounter = 0;
    private final int INVINCIBILITY_TIME = 50; // 200 UPS = 1 sec / Quarter of a second

    //Mana
    private int manaBottles; // temporary
    private int maxManaBottles;
    private int manaRegenTick = 0;
    private final int REGEN_THRESHOLD = 5 * 200;

    public Player(float x, float y, int width, int height, int[][] lvlData, PlayerCharacter characterData) {
        super(x, y, width, height);
        this.characterData = characterData;
        this.lvlData = lvlData;

        this.xDrawOffset = characterData.xOffset * Game.SCALE;
        this.yDrawOffset = characterData.yOffset * Game.SCALE;

        this.maxLife = characterData.defaultLives;
        this.life = maxLife;

        this.maxManaBottles = characterData.defaultManaBottles;
        this.manaBottles = maxManaBottles;

        this.walkSpeed = 1.0f * Game.SCALE * characterData.speedMultiplier;

        loadAnimations();
        initHitbox(characterData.hitboxWidth, characterData.hitboxHeight);

        this.activeSkill = characterData.getSkill(this);
    }

    public void loseLife() {

        if (invincible) return;

        life--;
        invincible = true;
        invincibleCounter = 0;

        if (life <= 0) {
            // Trigger death screen instead of resetting
            GameState.state = GameState.DEATH;
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
        updateMana();
        characterData.getPassive().update(this);
        if (activeSkill != null) {
            activeSkill.update();
        }
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

    private void updateMana() {
        if (manaBottles < maxManaBottles) {
            manaRegenTick++;
            if (manaRegenTick >= REGEN_THRESHOLD) {
                manaBottles++;
                manaRegenTick = 0;
            }
        } else {
            manaRegenTick = 0;
        }
    }

    public void changeHealth(int value) {

        //gain hp
        if (value > 0) {
            life += value;
            if (life > maxLife) life = maxLife;
            return;
        }

        if (invincible) return;

        life += value;
        invincible = true;
        invincibleCounter = 0;

        if (life <= 0) {
            GameState.state = GameState.DEATH;
        }
    }

    public void gainMana(int value) {
        manaBottles += value;
        if (manaBottles > maxManaBottles) {
            manaBottles = maxManaBottles;
        }
    }

    public void render(Graphics g, int lvlOffset, int yLvlOffset) {

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
                (int)(hitbox.y - yDrawOffset) - yLvlOffset,
                width * flipW, height, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        if (activeSkill != null) {
            activeSkill.render(g, lvlOffset, yLvlOffset);
        }

        drawHitbox(g, lvlOffset, yLvlOffset);
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

        int speed = ANIMATION_SPEED;

        // Adjust speed based on action
        if (playerAction == LANDING)            speed = 15;
        else if (playerAction == DOUBLEJUMP)    speed = 10;

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

        //SYLVARA ONLY
        if (playerAction == DOUBLEJUMP) {
            if (animationIndex < characterData.spriteA_DOUBLEJUMP - 1) {
                return;
            }
        }

        if (moving) playerAction = isRight() ? WALKR : WALKL;
        else        playerAction = (faceDirection == WALKL) ? WALKL : WALKR;

        if (inAir) {
            if (airSpeed < 0) playerAction = JUMP;
            else playerAction = AIRBORNE;
        }

        if (prev != playerAction) {
            animationTick = 0;
            animationIndex = 0;
        }
    }

    private void updatePos() {
        moving = false;
        if (jump) jump();

        float xSpeed = 0;

        if (!left && !right && !inAir) return;

        xSpeed += extraHSpeed;
        extraHSpeed *= 0.92f;
        if (Math.abs(extraHSpeed) < 0.1f) extraHSpeed = 0;

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
                airSpeed += characterData.defaultGravity;
            } else {
                hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0) {
                    if (inAir) {
                        playerAction = LANDING;
                        animationTick = 0;
                        animationIndex = 0;
                    }
                    resetInAir();
                } else {
                    airSpeed = fallSpeedAfterCollision;
                }
            }
        }

        // HORIZONTAL
        if (xSpeed != 0) {
            if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x += xSpeed;
                if(extraHSpeed == 0) moving = true;
            } else {
                hitbox.x = getEntityXPosNextToWall(hitbox, xSpeed);
                extraHSpeed = 0;
            }
        }
    }

    private void jump() {
        if (jumpPressed) return;

        if (!inAir) {
            //NORMAL JUMP
            airSpeed = jumpSpeed;
            inAir = true;
            jumpPressed = true;
        } else {
            //SYLVARA's SKILL
            if (activeSkill != null) {
                activeSkill.activate();
                jumpPressed = true;
            }
        }
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    public void resetAll() {
        resetDirectionBooleans();
        inAir        = true;
        moving       = false;
        playerAction = IDLE;
        this.life    = maxLife;



        hitbox.x = x;
        hitbox.y = y;

        // Check if respawn point is in the air
        if (!isEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
    }

    public void teleportToSpawn() {
        resetDirectionBooleans();
        inAir = true;
        moving = false;
        playerAction = IDLE;

        //spawn point
        hitbox.x = x;
        hitbox.y = y;
    }

    public void updateLevelData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!HelpMethods.isEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
    }

    public void setWalkSpeed(float walkSpeed) { this.walkSpeed = walkSpeed; }

    public void loadLvlData(int[][] lvlData) { this.lvlData = lvlData; }
    public PlayerCharacter getCharacterData() { return characterData; }

    public float getAirSpeed() { return airSpeed; }
    public void setAirSpeed(float airSpeed) { this.airSpeed = airSpeed; }
    public boolean isInAir() { return inAir; }
    public void startAirborne() { this.inAir = true; }

    public Skill getActiveSkill() { return activeSkill; }

    public float getJumpSpeed() { return jumpSpeed; }
    public void setJump (boolean jump) {
        this.jump = jump;
        if (!jump) {
            jumpPressed = false; // if released, then jump
        }
    }

    public void setPlayerAction(int action) {
        this.playerAction = action;
        this.animationTick = 0;
        this.animationIndex = 0;
    }

    public void fling(float force) { this.extraHSpeed = force; }
    public int getFaceDirection() { return faceDirection; }

    public void resetDirectionBooleans() { left = right = false; }
    public void setLeft(boolean left) { this.left = left; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }
    public void setDown(boolean down) { this.down = down; }
    public boolean isDown() { return down; }

    public void setY(float y) { this.y = y; }
    public void setX(float x) {this.x = x;}

    public int getMaxManaBottles() { return maxManaBottles; }
    public int getManaBottles() { return manaBottles; }
}
