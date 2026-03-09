package Utils;

import Main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {

    public static final String Sylvara_Atlas = "Characters/Hero/Sylvara/SylvaraSpriteSheet.png";
    public static final String Level_Atlas = "Levels/tempTiles/Floor Tiles1.png";
    public static final String LEVEL_ONE_DATA = "Levels/tempTiles/Decor.png";

    public static BufferedImage getSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static int[][] getLevelData() {
        BufferedImage img = getSpriteAtlas(LEVEL_ONE_DATA);
        int[][] lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];

        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                // Check to ensure we don't go out of bounds of the tiny map image
                if (i < img.getWidth() && j < img.getHeight()) {
                    Color color = new Color(img.getRGB(i, j));
                    int value = color.getRed();

                    // If Red is 52 or higher, it's an invalid tile, so make it 0
                    if (value >= 52) {
                        value = 0;
                    }
                    lvlData[j][i] = value;
                } else {
                    lvlData[j][i] = 0; // Default to empty space
                }
            }
        }
        return lvlData;
    }
}
