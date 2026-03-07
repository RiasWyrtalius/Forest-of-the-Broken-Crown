package Main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static utils.Constants.PlayerConstants.*;
import static utils.Constants.Directions.*;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private int moveX = 1, moveY = 1;
    private BufferedImage img;
    private BufferedImage[][] sylvaraAnimation;
    private int animationTick, animationIndex, animationSpeed = 30;
    private int playerAction = IDLE;
    private int playerDirection = -1;
    private boolean moving = false;



    public GamePanel() {
        mouseInputs = new MouseInputs(this);
        importImg();
        loadAnimations();

        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        setFocusable(true);
    }

    public void loadAnimations() {
        sylvaraAnimation = new BufferedImage[3][9];

        for(int i = 0; i < sylvaraAnimation.length; i++) {
            for(int j = 0; j < sylvaraAnimation[i].length; j++) {
               sylvaraAnimation[i][j] = img.getSubimage(j * 32, i * 32, 32, 32 );
            }
        }
    }

    private void importImg() {
        String path = "/Characters/Sylvara/SylvaraSpriteSheet.png";
        InputStream is = getClass().getResourceAsStream(path);

        try {
            img = ImageIO.read(is);
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

    private void setPanelSize() {
        Dimension size = new Dimension(1920, 1080);
        setPreferredSize(size);
    }

    public void setDirection(int direction) {
        this.playerDirection = direction;
        moving = true;
    }

    public void setMoving(boolean moving)
    {
        this.moving = moving;
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
            if(playerDirection == RIGHT) playerAction = WALKR;

            if(playerDirection == LEFT) playerAction = WALKL;
        }
        else playerAction = IDLE;

        if (startAni != playerAction) {
            animationTick = 0;
            animationIndex = 0;
        }
    }

    private void updatePos() {
        if(moving) {
            switch (playerDirection) {
                case LEFT:
                    moveX -= 5;
                    break;
                case UP:
                    moveY -= 5;
                    break;
                case RIGHT:
                    moveX += 5;
                    break;
                case DOWN:
                    moveY +=5;
                    break;
            }
        }
    }

    public void updateGame() {
        updateAnimationTick();
        setAnimation();
        updatePos();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(sylvaraAnimation[playerAction][animationIndex], (int)moveX, (int)moveY, 150, 150, null);
    }


}
