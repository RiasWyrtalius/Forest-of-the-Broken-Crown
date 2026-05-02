package Levels;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utils.Constants.ObjectConstants.LADDER_COLOR;

public class LevelHandler {

    private Game game;
    private BufferedImage[][] levelSprite;
    private BufferedImage ladderSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;
    private int currentLevelNum = 1;

    public LevelHandler(Game game) {
        this.game = game;
        //importOutSideSprites();
        levels = new ArrayList<>();
        buildAllLevels();
        importAllLevelsAtlases();
        loadExtraSprites();
    }

    private void loadExtraSprites() {
        ladderSprite = LoadSave.getSpriteAtlas(LoadSave.LADDER_ATLAS);
    }

    private void importAllLevelsAtlases() {
        levelSprite = new BufferedImage[levels.size()][];
        levelSprite[0] = splitAtlas(LoadSave.getSpriteAtlas(LoadSave.Level_Atlas));
        levelSprite[1] = splitAtlas(LoadSave.getSpriteAtlas(LoadSave.Level_Atlas));
        levelSprite[2] = splitAtlas(LoadSave.getSpriteAtlas(LoadSave.LevelTwo_Atlas));
        levelSprite[3] = splitAtlas(LoadSave.getSpriteAtlas(LoadSave.LevelThree_Atlas));
    }

    private void buildAllLevels() {
        // Level 1: Forest
        levels.add(new Level(LoadSave.getSpriteAtlas(LoadSave.LEVEL_ONE_DATA), LoadSave.Level_Atlas, LoadSave.LEVELONE_BACKGROUND_IMAGE));
        // Level 2: Forest - Boss
        levels.add(new Level(LoadSave.getSpriteAtlas(LoadSave.LEVEL_ONE_BOSS_DATA), LoadSave.Level_Atlas, LoadSave.LEVELONE_BACKGROUND_IMAGE));
        // Level 2: Cave
        levels.add(new Level(LoadSave.getSpriteAtlas(LoadSave.LEVEL_TWO_DATA), LoadSave.LevelTwo_Atlas, LoadSave.LEVELTWO_BACKGROUND_IMAGE));
        // Level 3: Castle
        levels.add(new Level(LoadSave.getSpriteAtlas(LoadSave.LEVEL_THREE_DATA), LoadSave.LevelThree_Atlas, LoadSave.LEVELTHREE_BACKGROUND_IMAGE));
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


    public void draw(Graphics g, int xOffset, int yOffset) {
        if (levels.isEmpty() || lvlIndex >= levels.size()) return;
        if (levelSprite == null || levelSprite.length <= lvlIndex || levelSprite[lvlIndex] == null) return;

        int[][] data = levels.get(lvlIndex).getLevelData();

        for (int j = 0; j < data.length; j++) {
            for (int i = 0; i < data[0].length; i++) {
                int index = levels.get(lvlIndex).getSpriteIndex(i, j);

                if (index == LADDER_COLOR) {
                    if (ladderSprite != null) {
                        g.drawImage(ladderSprite, Game.TILES_SIZE * i - xOffset, Game.TILES_SIZE * j - yOffset, Game.TILES_SIZE, Game.TILES_SIZE, null);
                    }
                    continue;
                }

                if (index >= 0 && index < levelSprite[lvlIndex].length) {
                    g.drawImage(levelSprite[lvlIndex][index],
                            Game.TILES_SIZE * i - xOffset,
                            Game.TILES_SIZE * j - yOffset,
                            Game.TILES_SIZE,
                            Game.TILES_SIZE,
                            null);
                }
            }
        }
    }

    public void updateBackground() {
        BufferedImage newBg = levels.get(lvlIndex).getBackgroundImage();
        game.setBackgroundImg(newBg);
    }

    public void loadLevel(int levelNum) {
        if (levelNum == 0) {
            this.lvlIndex = 0;
        } else {
            this.lvlIndex = levelNum - 1;
        }

        if (lvlIndex < 0) lvlIndex = 0;
        if (lvlIndex >= levels.size()) lvlIndex = levels.size() - 1;

        updateBackground();
        this.currentLevelNum = levelNum;
        importAllLevelsAtlases();

        Level currentLevel = levels.get(lvlIndex);
        game.getObjectManager().loadObjects(currentLevel);

        if (game.getPlaying() != null) {
            game.getPlaying().getEnemyManager().loadEnemies(currentLevel);
            game.getPlaying().updateLevelOffsets();
        }

        Point p = levels.get(lvlIndex).getPlayerSpawn();
        //game.getPlayer().setSpawn(p);
        System.out.println("Loaded Level " + levelNum + " - Spawn: " + p.x + ", " + p.y);
    }

    public void update() {}

    public Level getCurrentLevel() { return levels.get(lvlIndex); }
    public int getAmountOfLevels() { return levels.size(); }
    public int getCurrentLevelNum() { return currentLevelNum; }
    public BufferedImage getLevelDataImage(int levelNum) {
        return switch (levelNum) {
            case 1 -> LoadSave.getSpriteAtlas(LoadSave.LEVEL_ONE_DATA);
            case 2 -> LoadSave.getSpriteAtlas(LoadSave.LEVEL_TWO_DATA);
            case 3 -> LoadSave.getSpriteAtlas(LoadSave.LEVEL_THREE_DATA);
            default -> null;
        };
    }
    public int getLevelIndex() { return lvlIndex; }
}