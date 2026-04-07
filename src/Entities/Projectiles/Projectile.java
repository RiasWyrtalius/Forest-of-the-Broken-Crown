package Entities.Projectiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Projectile {
    private int x, y;
    private int speed;
    private int direction;
    private boolean active = true;

    // SPRITE
    private BufferedImage sheetLeft, sheetRight;
    private int aniTick, aniIndex, aniSpeed = 15;
    private final int SPRITE_SIZE = 32;
    private final int TOTAL_FRAMES = 16;
    private final int DISPLAY_WIDTH = 48;
    private final int DISPLAY_HEIGHT = 48;

    public Projectile(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = 5;
        loadImages();
    }

    private void loadImages() {
        sheetRight = importImage("/Characters/Hero/Sylvara/sylvara_atk/sylvara_atk_right.png");
        sheetLeft = importImage("/Characters/Hero/Sylvara/sylvara_atk/sylvara_atk_left.png");
    }

    private BufferedImage importImage(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("Could not find file: " + path);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update() {
        x += (speed * direction);
        updateAnimation();
    }

    private void updateAnimation() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= TOTAL_FRAMES) {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g) {
        if (!active) return;

        BufferedImage currentSheet = (direction > 0) ? sheetRight : sheetLeft;

        if (currentSheet != null) {
            int sourceX = aniIndex * SPRITE_SIZE;

            g.drawImage(currentSheet,
                    x, y,
                    x + DISPLAY_WIDTH,
                    y + DISPLAY_HEIGHT,
                    sourceX,
                    0,
                    sourceX + SPRITE_SIZE, SPRITE_SIZE,
                    null);
        }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getX() { return x; }
    public int getY() { return y; }
}