package Levels;

import Main.Core.Game;
import java.awt.image.BufferedImage;
import static Utils.HelpMethods.GetLevelData;

public class Level {

    private BufferedImage img;
    private String atlasPath;
    private int[][] lvlData;
    private int lvlTilesWide;
    private int maxTilesOffset;
    private int maxLvlOffsetX;
    private String bgPath;

    public Level(BufferedImage img, String atlasPath, String bgPath) {
        this.img = img;
        this.atlasPath = atlasPath;
        this.bgPath = bgPath;
        createLevelData();
        //createBoss(); //TODO: IMPLEMENT
        calculateLevelOffsets();
    }

    private void calculateLevelOffsets() {
        lvlTilesWide = img.getWidth();
        maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;
    }

    private void createLevelData() {
        lvlData = GetLevelData(img);
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }
    public int getLvlOffset() { return maxLvlOffsetX; }
    public int[][] getLevelData(){ return lvlData; }
    public BufferedImage getLevelDataImg() { return img; }
    public String getBackgroundPath() { return bgPath; }
}