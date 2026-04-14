package Utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class LoadSave {

    //Hearts
    public static final String Hearts_Atlas = "Characters/Hero/Lives/Life.png";

    //Character Selection
    public static final String CSelection_Atlas = "Characters/CharacterSelectBG.png";

    //Characters
    public static final String Sylvara_Atlas = "Characters/Hero/Sylvara/SylvaraSpriteSheet.png";
    public static final String Kaelthron_Atlas = "Characters/Hero/Kaelthorn/KaelthornSpriteSheet.png";
    public static final String Embjorn_Atlas = "Characters/Hero/Embjorn/EmbjornSpriteSheet.png";

    //Level 1
    public static final String Level_Atlas = "Levels/Level1/TileSheetFloor1.png";
    public static final String LEVEL_ONE_DATA = "Levels/Level1/level_one_data.png";
    public static final String PLAYING_BACKGROUND_IMAGE = "Levels/Level1/Forest.jpeg";

    //Bosses
    public static final String EMBRYN_ATLAS = "Characters/Enemy/Embryn.png";

    //Objects
    public static final String VASE_ATLAS = "Levels/Objects/Vase/vase_object.png";
    public static final String SPIKE_ATLAS = "Levels/Level1/spikes_object.png";

    public static BufferedImage getSpriteAtlas(String fileName) {
        BufferedImage img = null;
        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {
            if (is == null) throw new Exception("File not found");
            img = ImageIO.read(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    public static int[][] getLevelData() {
        BufferedImage img = getSpriteAtlas(LEVEL_ONE_DATA);

        if (img == null) {
            System.err.println("Level data image is null. Returning empty array.");
            return new int[0][0]; 
        }

        int[][] lvlData = new int[img.getHeight()][img.getWidth()];

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int redValue = color.getRed();
                int blueValue = color.getBlue();

                if (blueValue == 130) { // vase - 130
                    lvlData[j][i] = 18; // empty
                }

                else if (blueValue == 131) { // spike - 131
                    lvlData[j][i] = 18; // empty
                }

                else { // regular terrain
                    if (redValue >= 48) redValue = 0;
                    lvlData[j][i] = redValue;
                }
            }
        }
        return lvlData;
    }
}