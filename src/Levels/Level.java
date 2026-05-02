package Levels;

import Main.Core.Game;
import Utils.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Utils.LoadSave.getSpriteAtlas;


public class Level {

    private BufferedImage img;
    private BufferedImage backgroundImg;
    private String atlasPath;
    private int[][] lvlData;
    private int lvlTilesWide;
    private int maxLvlOffsetX;
    private String bgPath;
    private Point playerSpawn = new Point(0, 0);

    public Level(BufferedImage img, String atlasPath, String bgPath) {
        this.img = img;
        this.atlasPath = atlasPath;
        this.bgPath = bgPath;
        this.backgroundImg = getSpriteAtlas(bgPath);
        this.lvlData = HelpMethods.GetLevelData(img, playerSpawn);
        calculateLevelOffsets();
    }

    private void calculateLevelOffsets() {
        lvlTilesWide = img.getWidth();
        maxLvlOffsetX = (lvlTilesWide - Game.TILES_IN_WIDTH) * Game.TILES_SIZE;
    }

    public Point getPlayerSpawn() { return playerSpawn; }
    public int getSpriteIndex(int x, int y) {
        if (y < 0 || y >= lvlData.length || x < 0 || x >= lvlData[y].length) return 0;
        return lvlData[y][x];
    }
    public int[][] getLevelData(){ return lvlData; }
    public BufferedImage getLevelDataImg() { return img; }
    public String getBackgroundPath() { return bgPath; }
    public BufferedImage getLevelDataImage() { return img; }
    public BufferedImage getBackgroundImage() { return backgroundImg; }
    public String getAtlasPath() { return atlasPath; }
}