package Levels;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LevelHandler {

    private Game game;
    private BufferedImage[][] levelSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;
    private int currentLevelNum = 1;

    public LevelHandler(Game game) {
        this.game = game;
        //importOutSideSprites();
        levels = new ArrayList<>();
        buildAllLevels();
        importAllLevelsAtlases();
    }

    private void importAllLevelsAtlases() {
        // Manually create the size based on your known levels
        levelSprite = new BufferedImage[2][];

        // Load specifically from your constants
        levelSprite[0] = splitAtlas(LoadSave.getSpriteAtlas(LoadSave.Level_Atlas));
        levelSprite[1] = splitAtlas(LoadSave.getSpriteAtlas(LoadSave.LevelTwo_Atlas));
    }

    private void buildAllLevels() {
        // Level 1: Forest
        levels.add(new Level(LoadSave.getSpriteAtlas(LoadSave.LEVEL_ONE_DATA), LoadSave.Level_Atlas));

        // Level 2: Cave
        levels.add(new Level(LoadSave.getSpriteAtlas(LoadSave.LEVEL_TWO_DATA), LoadSave.LevelTwo_Atlas));
    }

    private BufferedImage[] splitAtlas(BufferedImage atlas) {
        int columns = atlas.getWidth() / Game.TILES_DEFAULT_SIZE;
        int rows = atlas.getHeight() / Game.TILES_DEFAULT_SIZE;
        BufferedImage[] sprites = new  BufferedImage[rows * columns];

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < columns; i++) {
                int index = j * columns + i;
                sprites[index] = atlas.getSubimage(
                        i * Game.TILES_DEFAULT_SIZE,
                        j * Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE
                );
            }
        }
        return sprites;
    }


    public void draw(Graphics g, int lvlOffset) {

        // 1. Check if the level list actually has data
        if (levels.isEmpty() || lvlIndex >= levels.size()) return;

        // 2. Check if the sprite array for this level is empty or null
        if (levelSprite == null || levelSprite.length <= lvlIndex || levelSprite[lvlIndex] == null) {
            return;
        }

        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < levels.get(lvlIndex).getLevelData()[0].length; i++) {
                int index = levels.get(lvlIndex).getSpriteIndex(i, j);
                g.drawImage(levelSprite[lvlIndex][index],
                        Game.TILES_SIZE * i - lvlOffset,
                        Game.TILES_SIZE * j,
                        Game.TILES_SIZE,
                        Game.TILES_SIZE,
                        null);
            }
        }
    }


    public void update() {}
    public void loadLevel(int levelNum) {
        this.currentLevelNum = levelNum;
        this.lvlIndex = levelNum - 1;
        if (lvlIndex >= levels.size()) lvlIndex = 0;
    }
    public Level getCurrentLevel() { return levels.get(lvlIndex); }
    public int getAmountOfLevels() { return levels.size(); }
    public int getCurrentLevelNum() { return currentLevelNum; }
}