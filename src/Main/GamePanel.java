package Main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private int moveX = 1, moveY = 1;
    private BufferedImage img, subImg;

    public GamePanel() {
        mouseInputs = new MouseInputs(this);
        importImg();
        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    private void importImg() {
        String path = "/Characters/Hero/Soldier-Walk.png";
        InputStream is = getClass().getResourceAsStream(path);

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPanelSize() {
        Dimension size = new Dimension(1280, 800);
        setPreferredSize(size);
    }

    public void changeX(int value) {
        this.moveX += value;
    }
    public void changeY(int value) {
        this.moveY += value;
    }

    public void setPosition(int x, int y) {
        this.moveX = x;
        this.moveY = y;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        subImg = img.getSubimage(1, 1, 64, 64);
        g.drawImage(subImg, moveX, moveY, 130, 150, null);
    }
}
