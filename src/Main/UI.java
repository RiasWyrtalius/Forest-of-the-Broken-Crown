package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
            var is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("FILE NOT FOUND: Make sure the file is at: " + path);
                System.err.println("Current Path being checked: " + getClass().getResource(path));
                return;
            }
            BufferedImage spriteSheet = ImageIO.read(is);

            int width = spriteSheet.getWidth() / 2;
            int height = spriteSheet.getHeight();

            heartSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
            heartSprite[1] = spriteSheet.getSubimage(width, 0, width, height);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {

        int maxLife = game.getPlayer().maxLife;
        int currentLife = game.getPlayer().life;

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
