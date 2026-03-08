package Entities;

import Entities.Projectiles.Projectile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static Utils.Constants.PlayerConstants.*;
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

    //temp
    private long lastAttackTime;
    private long atkCd = 50;
    private int lastDirection = 1;
    private boolean attacking = false;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public Player(float x, float y) {
        super(x, y);
        loadAnimations();
    }

    public void update() {
        updatePos();
        updateAnimationTick();
        setAnimation();
        updateProjectiles();

        if (attacking) {
            shoot();
        }
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    //temp
    public void shoot() {
        long currTime = System.currentTimeMillis();
        if (currTime - lastAttackTime >= atkCd) {
            projectiles.add(new Projectile((int)x, (int)y, lastDirection));
            lastAttackTime = currTime;
        }
    }

    public void render(Graphics g) {
        g.drawImage(animations[playerAction][animationIndex], (int) x, (int) y, 150, 150, null);
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
        String path = "/Characters/Sylvara/SylvaraSpriteSheet.png";
        InputStream is = getClass().getResourceAsStream(path);

        try {
            BufferedImage img = ImageIO.read(is);

            animations = new BufferedImage[3][9];

            for(int i = 0; i < animations.length; i++) {
                for(int j = 0; j < animations[i].length; j++) {
                    animations[i][j] = img.getSubimage(j * 32, i * 32, 32, 32 );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
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
    }

    private void updatePos() {

        moving = false;

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
    }

    public void resetDirectionBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
