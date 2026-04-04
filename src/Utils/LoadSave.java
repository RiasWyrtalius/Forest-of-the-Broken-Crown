// - Added Crabby_Atlas constant for enemy sprite path.
// - Modified getSpriteAtlas() to check if InputStream is null before reading, preventing IllegalArgumentException.
// - Added error message when file is not found.
// -
package Utils;

import Main.Game;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class LoadSave {

    public static final String Sylvara_Atlas = "Assets/Characters/Hero/Sylvara/SylvaraSpriteSheet.png";
    public static final String Level_Atlas = "Assets/Levels/tempTiles/Floor Tiles1.png";
    public static final String LEVEL_ONE_DATA = "Assets/Levels/tempTiles/level_one_data.png";
    public static final String Crabby_Atlas = "Assets/Enemies/Crabby.png";

    public static BufferedImage getSpriteAtlas(String fileName) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException ignored) {
            // If file system access fails, fall back to resource loading.
        }

        if (img == null) {
            InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
            if (is != null) {
                try {
                    img = ImageIO.read(is);
                } catch (IOException e) {
                    System.err.println("Error reading file: /" + fileName);
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("Could not find file: " + fileName);
            }
        }
        return img;
    }

    public static int[][] getLevelData() {
        BufferedImage img = getSpriteAtlas(LEVEL_ONE_DATA);
        int[][] lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];

        if (img != null) {
            for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
                for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                    if (i < img.getWidth() && j < img.getHeight()) {
                        Color color = new Color(img.getRGB(i, j));
                        int value = color.getRed();

                        System.out.print(value + " ");

                        if (value >= 48) value = 0;
                        lvlData[j][i] = value;
                    }
                }
                System.out.println();
            }
        } else {
            System.err.println("Could not load level data image");
        }
        return lvlData;
    }
}
