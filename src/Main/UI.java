package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UI {
    private Game game;
    private BufferedImage[] heartSprite = new BufferedImage[2]; // 0 = full, 1 = empty

    public UI(Game game) {
        this.game = game;
        loadHeartImages();
    }

    private void loadHeartImages() {

        String path = "/Characters/Hero/Lives/Life.png";

        try {
            BufferedImage spriteSheet;
            var is = getClass().getResourceAsStream(path);
            if (is != null) {
                spriteSheet = ImageIO.read(is);
            } else {
                File[] fallbackFiles = new File[] {
                    new File("Assets" + File.separator + "Characters" + File.separator + "Hero" + File.separator + "Lives" + File.separator + "Life.png"),
                    new File("Forest-of-the-Broken-Crown" + File.separator + "Assets" + File.separator + "Characters" + File.separator + "Hero" + File.separator + "Lives" + File.separator + "Life.png"),
                    new File(System.getProperty("user.dir") + File.separator + "Forest-of-the-Broken-Crown" + File.separator + "Assets" + File.separator + "Characters" + File.separator + "Hero" + File.separator + "Lives" + File.separator + "Life.png")
                };
                File foundFile = null;
                for (File file : fallbackFiles) {
                    if (file.exists()) {
                        foundFile = file;
                        break;
                    }
                }
                if (foundFile == null) {
                    System.err.println("FILE NOT FOUND: Make sure the file is at: " + path);
                    System.err.println("Current Path being checked: " + getClass().getResource(path));
                    return;
                }
                spriteSheet = ImageIO.read(foundFile);
            }

            int width = spriteSheet.getWidth() / 2;
            int height = spriteSheet.getHeight();

            heartSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
            heartSprite[1] = spriteSheet.getSubimage(width, 0, width, height);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {

        int maxLife = game.getPlayer().getMaxLife();
        int currentLife = game.getPlayer().getLife();

        for (int i = 0; i < maxLife; i++) {
            int x = 15 + (i * (Game.TILES_SIZE + 5)); //spacing
            int y = 15;

            if (i < currentLife) {
                // Draw Full Heart
                g.drawImage(heartSprite[0], x, y, Game.TILES_SIZE, Game.TILES_SIZE, null);
            } else {
                // Draw Empty Heart
                g.drawImage(heartSprite[1], x, y, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
        }
    }

}
