package Utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class LoadSave {

    public static final String Sylvara_Atlas = "Characters/Hero/Sylvara/SylvaraSpriteSheet.png";
    public static final String Level_Atlas = "Levels/Level1/TileSheetFloor1.png";
    public static final String LEVEL_ONE_DATA = "Levels/Level1/level_one_data.png";
    public static final String PLAYING_BACKGROUND_IMAGE = "Levels/Level1/Forest.jpeg";
    public static final String EMBRYN_ATLAS = "Characters/Hero/Enemies/Embryn.png";

    public static BufferedImage getSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

        if (is == null) {
            String[] fallbackPaths = new String[] {
                "Assets" + File.separator + fileName,
                "Forest-of-the-Broken-Crown" + File.separator + "Assets" + File.separator + fileName,
                System.getProperty("user.dir") + File.separator + "Forest-of-the-Broken-Crown" + File.separator + "Assets" + File.separator + fileName
            };
            for (String path : fallbackPaths) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        is = new FileInputStream(file);
                        break;
                    }
                } catch (IOException ignored) {
                }
            }
        }

        if (is == null) {
            System.err.println("ERROR: Could not find file at path: /" + fileName);
            System.err.println("Check if the file is in your 'src' or 'Assets' folder and spelled correctly.");
            return null;
        }

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static int[][] getLevelData() {
        BufferedImage img = getSpriteAtlas(LEVEL_ONE_DATA);
        
        // Safety check: if image is null, stop here to avoid further NullPointerExceptions
        if (img == null) {
            System.err.println("Level data image is null. Returning empty array.");
            return new int[0][0]; 
        }

        int[][] lvlData = new int[img.getHeight()][img.getWidth()];

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getRed();

                if (value >= 48) value = 0;
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }
}