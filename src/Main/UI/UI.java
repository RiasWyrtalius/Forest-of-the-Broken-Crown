package Main.UI;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {
    private Game game;
    private BufferedImage[] heartSprite = new BufferedImage[2]; // 0 = full, 1 = empty

    public UI(Game game) {
        this.game = game;
        loadHeartImages();
    }

    private void loadHeartImages() {
        BufferedImage spriteSheet = LoadSave.getSpriteAtlas(LoadSave.Hearts_Atlas);

        int width = spriteSheet.getWidth() / 2;
        int height = spriteSheet.getHeight();

        heartSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
        heartSprite[1] = spriteSheet.getSubimage(width, 0, width, height);
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
