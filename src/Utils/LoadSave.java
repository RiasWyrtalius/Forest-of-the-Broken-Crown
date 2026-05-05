package Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class LoadSave {

    //CUTSCENE
    public static final String Intro1 = "Scenes/Introduction/scene1_intro.png";
    public static final String Intro2 = "Scenes/Introduction/scene2_intro.png";
    public static final String Intro3 = "Scenes/Introduction/scene3_intro.png";

    public static final String Outro1 = "Scenes/Introduction/scene1_outro.png";
    public static final String Outro2 = "Scenes/Introduction/scene2_outro.png";
    public static final String Outro3 = "Scenes/Introduction/scene3_outro.png";

    public static final String Kael1 = "Scenes/Kaelthorn/KaelthornCutscene.jpeg";
    public static final String Kael2_3 = "Scenes/Kaelthorn/KaelthornCutscene2.jpeg";

    public static final String Syl1 = "Scenes/Sylvara/SylvaraCutscene.jpeg";
    public static final String Syl2_3 = "Scenes/Sylvara/SylvaraCutscene2.jpeg";

    public static final String Emb1 = "Scenes/Embjorn/EmbjornCutscene.jpeg";
    public static final String Emb2_3 = "Scenes/Embjorn/EmbjornCutscene2.jpeg";

    //ICON
    public static final String GAME_ICON = "Icon.jpg";

    //Stats
    public static final String Hearts_Atlas = "Characters/Hero/Stats/Life.png";
    public static final String Mana_Atlas = "Characters/Hero/Stats/Mana.png";

    //Character Selection
    public static final String CSelection_Atlas = "Characters/CharacterSelectBG.png";

    //Pause
    public static final String PauseBg = "Pause/PauseBg.png";

    //NPC
    public static final String Nino_Atlas = "Characters/NPC/TheQueerSS.png";
    public static final String Chad_Atlas = "Characters/NPC/TheBraveSS.png";
    public static final String Charlz_Atlas = "Characters/NPC/TheSageSS.png";
    public static final String Riley_Atlas = "Characters/NPC/TheZephyrSS.png";
    public static final String Denver_Atlas = "Characters/NPC/TheCursedSS.png";

    //Characters
    public static final String Sylvara_Atlas = "Characters/Hero/Sylvara/SylvaraSpriteSheet.png";
    public static final String Kaelthron_Atlas = "Characters/Hero/Kaelthorn/KaelthornSpriteSheet.png";
    public static final String Embjorn_Atlas = "Characters/Hero/Embjorn/EmbjornSpriteSheet.png";

    //Level 1
    public static final String Level_Atlas = "Levels/Level1/TSFloor1.png";
    public static final String LEVEL_ONE_DATA = "Levels/Level1/level_one_data.png";
    public static final String LEVELONE_BACKGROUND_IMAGE = "Levels/Level1/Forest.jpeg";

    //Level 1 - Boss
    public static final String LEVEL_ONE_BOSS_DATA = "Levels/Level1_Boss/level_one_boss_data.png";

    //Level 2
    public static final String LevelTwo_Atlas = "Levels/Level2/TSFloor2.png";
    public static final String LEVEL_TWO_DATA = "Levels/Level2/level_two_data.png";
    public static final String LEVELTWO_BACKGROUND_IMAGE = "Levels/Level2/Cave.jpeg";

    //Level 2 - Boss
    public static final String LEVEL_TWO_BOSS_DATA = "Levels/Level2_Boss/level_two_boss_data.png";

    //Level 3
    public static final String LevelThree_Atlas = "Levels/Level3/TSFloor3.png";
    public static final String LEVEL_THREE_DATA = "Levels/Level3/level_three_data.png";
    public static final String LEVELTHREE_BACKGROUND_IMAGE = "Levels/Level3/Castle.jpeg";

    //Level 3 - Boss
    public static final String LEVEL_THREE_BOSS_DATA = "Levels/Level3_Boss/level_three_boss_data.png";

    //Bosses
    public static final String EMBRYN_ATLAS = "Characters/Enemy/Embryn.png";

    //Objects
    public static final String VASE_ATLAS = "Levels/Objects/Vase/vase_object.png";
    public static final String SPIKE_ATLAS = "Levels/Level1/spikes_object.png";
    public static final String LADDER_ATLAS = "Levels/Objects/Ladder/LadderSprite.png";
    public static final String CRUMBLING_TILE_ATLAS = "Levels/Objects/CrumblingTile/CrumblingTile.png";

    //Potions
    public static final String HEALTH_POTION_ATLAS = "Levels/Objects/DropHealthSS.png";
    public static final String MANA_POTION_ATLAS = "Levels/Objects/DropManaSS.png";


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

    public static Font getFont(String filename) {
        Font customFont = null;

        InputStream is = LoadSave.class.getResourceAsStream("/" + filename);
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        return customFont != null ? customFont : new Font("Arial", Font.PLAIN, 12);
    }

    public static BufferedImage[] getAllLevels() {
        List<BufferedImage> levelImages = new ArrayList<>();
        URL url = LoadSave.class.getResource("/Levels");

        try {
            File root = new File(url.toURI());
            addImagesFromFolder(root, levelImages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return levelImages.toArray(new BufferedImage[0]);
    }

    private static void addImagesFromFolder(File folder, List<BufferedImage> images) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                addImagesFromFolder(f, images);
            } else if (f.getName().toLowerCase().endsWith(".png")) {
                String absolutePath = f.getAbsolutePath();

                //looks for where "Levels" starts and take everything after it
                String resourcePath = absolutePath.substring(absolutePath.indexOf("Levels")).replace("\\", "/");

                //Load using the clean path
                images.add(getSpriteAtlas(resourcePath));
                System.out.println("Successfully Loaded: " + resourcePath);
            }
        }
    }
}