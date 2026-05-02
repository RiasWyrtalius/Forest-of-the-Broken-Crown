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
        for (int i = 0; i < levels.size(); i++) {
            String atlas = levels.get(i).getAtlasPath();
            if (atlas != null) {
                levelSprite[i] = splitAtlas(LoadSave.getSpriteAtlas(atlas));
            }
        }
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
        int currentIdx = lvlIndex; // capture once so it can't change mid-draw

        if (levels.isEmpty() || currentIdx >= levels.size()) return;
        if (levelSprite == null || levelSprite.length <= currentIdx) return;
        if (levelSprite[currentIdx] == null) return;

        int[][] data = levels.get(currentIdx).getLevelData();
        if (data == null || data.length == 0) return;

        BufferedImage[] sprites = levelSprite[currentIdx]; // capture reference too

        for (int j = 0; j < data.length; j++) {
            for (int i = 0; i < data[j].length; i++) {
                int index = levels.get(currentIdx).getSpriteIndex(i, j);

                if (index == LADDER_COLOR) {
                    if (ladderSprite != null) {
                        g.drawImage(ladderSprite, Game.TILES_SIZE * i - xOffset, Game.TILES_SIZE * j - yOffset, Game.TILES_SIZE, Game.TILES_SIZE, null);
                    }
                    continue;
                }

                if (index >= 0 && index < sprites.length) {
                    g.drawImage(sprites[index],
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