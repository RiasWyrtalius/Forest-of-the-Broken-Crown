package Levels;

import Main.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LevelHandler {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelHandler(Game game) {
        this.game = game;
        importOutSideSprites();
        levelOne = new Level(LoadSave.getLevelData());
    }

    public void importOutSideSprites() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.Level_Atlas);
        levelSprite = new BufferedImage[48];

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 12; i++) {
                int index = j * 12 + i;
                levelSprite[index] = img.getSubimage(
                        i * Game.SPRITE_DEFAULT_SIZE,
                        j * Game.SPRITE_DEFAULT_SIZE,
                        Game.SPRITE_DEFAULT_SIZE,
                        Game.SPRITE_DEFAULT_SIZE
                );
            }
        }
    }



    public void draw(Graphics g) {
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], Game.TILES_SIZE * i, Game.TILES_SIZE * j, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
        }
    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levelOne;
    }
}
