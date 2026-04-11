package Levels;

import Main.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LevelHandler {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;
    private int currentLevelNum = 1;

    public LevelHandler(Game game) {
        this.game = game;
        importOutSideSprites();
        levelOne = new Level(LoadSave.getLevelData());
    }

    public void importOutSideSprites() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.Level_Atlas);
        int columns = img.getWidth() / Game.TILES_DEFAULT_SIZE;
        int rows = img.getHeight() / Game.TILES_DEFAULT_SIZE;
        levelSprite = new BufferedImage[rows * columns];

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < columns; i++) {
                int index = j * columns + i;
                levelSprite[index] = img.getSubimage(
                        i * Game.TILES_DEFAULT_SIZE,
                        j * Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE
                );
            }
        }
    }


    public void draw(Graphics g, int lvlOffset) {
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < levelOne.getLevelData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], Game.TILES_SIZE * i - lvlOffset, Game.TILES_SIZE * j, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
        }
    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levelOne;
    }

    public int getCurrentLevelNum() {
        return currentLevelNum;
    }

    public void loadLevel(int levelNum) {
        this.currentLevelNum = levelNum;
    }
}