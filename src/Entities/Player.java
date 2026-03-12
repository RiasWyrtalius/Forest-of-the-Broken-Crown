package Entities;

import Entities.Projectiles.Projectile;
import Main.Game;
import Utils.LoadSave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.CanMoveHere;
import static Utils.Constants.PlayerConstants.IDLE;

//TODO: ATTACK ANIMATION / ACTION - hold off for now since no sprites yet.
/**
 * NOTE : PROJECTILE SHOOTY THINGY IS TEMPORARY cuz i dont know crap about it.
 * */

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 30;
    private int playerAction = IDLE;
    private boolean moving = false;
    private boolean up, down, left, right;
    private int faceDirection = WALKR;
    private float playerSpeed = 2.0f;
    private int[][] lvlData;

    //temp
    private long lastAttackTime;
    private long atkCd = 200;
    private int lastDirection = 1;
    private boolean attacking = false;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public Player(float x, float y, int width, int height, int[][] lvlData) {
        super(x, y, width, height);
        this.lvlData = lvlData;
        loadAnimations();
    }

    public void update() {
        updatePos();
        updateHitbox();
        updateAnimationTick();
        setAnimation();

        if (attacking) shoot();
        updateProjectiles();
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public void shoot() {

        int projectileDirection = (faceDirection == WALKL) ? -1 : 1;

        long currTime = System.currentTimeMillis();
        if (currTime - lastAttackTime >= atkCd) {
            projectiles.add(new Projectile((int)x, (int)y, projectileDirection));
            lastAttackTime = currTime;
        }
    }

    public void render(Graphics g) {
        g.drawImage(animations[playerAction][animationIndex], (int) x, (int) y, 100, 100, null);
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

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
    }


    public void loadAnimations() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.Sylvara_Atlas);

        animations = new BufferedImage[3][9];

        for(int i = 0; i < animations.length; i++) {
            for(int j = 0; j < animations[i].length; j++) {
                animations[i][j] = img.getSubimage(j * 32, i * 32, 32, 32 );
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
        if(!left && !right && !up && !down)
            return;

        float xSpeed = 0, ySpeed = 0;

        if(left && !right)
            xSpeed = -playerSpeed;
        else if (right && !left)
            xSpeed = playerSpeed;

        if(up && !down)
            ySpeed = -playerSpeed;
        else if (down && !up)
            ySpeed = playerSpeed;

        if (left && !right) {
            x -= playerSpeed;
            moving = true;
            faceDirection = WALKL;
        } else if (right && !left) {
            x += playerSpeed;
            moving = true;
            faceDirection = WALKR;
        }

        if (up && !down) {
            y -= playerSpeed;
            moving = true;
        } else if (down && !up) {
            y += playerSpeed;
            moving = true;
        }


// We subtract from width and height so the 'collision box' is smaller than the visual sprite
// Try subtracting 30 from width and 10 from height as a start
        if (CanMoveHere(x + xSpeed, y + ySpeed, (int)(width - 40), (int)(height - 10), lvlData)) {
            this.x += xSpeed;
            this.y += ySpeed;
            moving = true;
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
}
