package Main.UI;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {
    private Game game;
    private BufferedImage[] heartSprite = new BufferedImage[2]; // 0 = full, 1 = empty
    private BufferedImage[] manaSprite = new BufferedImage[2];

    public UI(Game game) {
        this.game = game;
        loadHeartImages();
        loadManaImages();
    }

    private void loadHeartImages() {
        BufferedImage spriteSheet = LoadSave.getSpriteAtlas(LoadSave.Hearts_Atlas);

        int width = spriteSheet.getWidth() / 2;
        int height = spriteSheet.getHeight();

        heartSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
        heartSprite[1] = spriteSheet.getSubimage(width, 0, width, height);
    }

    private void loadManaImages() {
        BufferedImage spriteSheet = LoadSave.getSpriteAtlas(LoadSave.Mana_Atlas);

        int width = spriteSheet.getWidth() / 2;
        int height = spriteSheet.getHeight();

        manaSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
        manaSprite[1] = spriteSheet.getSubimage(width, 0, width, height);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int maxLife = game.getPlayer().getMaxLife();
        int currentLife = game.getPlayer().getLife();

        int maxMana = game.getPlayer().getMaxManaBottles();
        int currentMana = game.getPlayer().getManaBottles();

        int startX = 20;
        int heartY = 20;
        int manaY = 65;

        int heartSize = Game.TILES_SIZE;
        int manaSize = (int) (Game.TILES_SIZE * 0.7);
        int spacing = 8;

        //Hearts
        for (int i = 0; i < maxLife; i++) {
            int x = startX + (i * (heartSize - spacing));
            int imgIndex = (i < currentLife) ? 0 : 1;
            g.drawImage(heartSprite[imgIndex], x, heartY, heartSize, heartSize, null);
        }

        //Mana Bottles
        for (int i = 0; i < maxMana; i++) {
            int x = (startX + 7) + (i * (manaSize - spacing + 2));

            if (i < currentMana) {
                g2d.drawImage(manaSprite[0], x, manaY, manaSize, manaSize, null);
            } else if (i == currentMana) {
                float pulse = (float) (Math.sin(System.currentTimeMillis() / 200) * 0.35 + 0.65);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
                g2d.drawImage(manaSprite[0], x, manaY, manaSize, manaSize, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
            } else {
                g2d.drawImage(manaSprite[1], x, manaY, manaSize, manaSize, null);
            }
        }
    }

}
