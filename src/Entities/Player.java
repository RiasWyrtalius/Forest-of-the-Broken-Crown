package Entities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import static Utils.Constants.PlayerConstants.*;
import static Utils.Constants.PlayerConstants.IDLE;

//TODO: ATTACK ANIMATION / ACTION - hold off for now since no sprites yet.

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 30;
    private int playerAction = IDLE;
    private boolean moving = false;
    private boolean up, down, left, right;
    private float playerSpeed = 2.0f;

    public Player(float x, float y) {
        super(x, y);
        loadAnimations();
    }

    public void update() {
        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g) {
        g.drawImage(animations[playerAction][animationIndex], (int) x, (int) y, 150, 150, null);
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
        }
        else playerAction = IDLE;

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
        } else if (right && !left) {
            x += playerSpeed;
            moving = true;
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
