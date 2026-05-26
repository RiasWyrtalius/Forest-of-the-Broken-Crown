package Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

public class LoadSave {

    private static final String LEADERBOARD_FILE = "leaderboard.txt";

    //CUTSCENE
    public static final String Intro1 = "Scenes/Introduction/scene1_intro.jpeg";
    public static final String Intro2 = "Scenes/Introduction/scene2_intro.jpeg";
    public static final String Intro3 = "Scenes/Introduction/scene3_intro.jpeg";

    //GOOD ENDING
    public static final String GoodEnding2 = "Scenes/Outro/Good/GoodEnding1.jpeg";
    public static final String GoodEnding1 = "Scenes/Outro/Good/GoodEnding2.jpeg";

    //BAD ENDING
    public static final String BadEnding = "Scenes/Outro/Bad/BadEnding.jpeg";

    public static final String Kael1 = "Scenes/Kaelthorn/KaelthornCutscene (1).jpeg";
    public static final String Kael2_3 = "Scenes/Kaelthorn/KaelthornCutscene (2).jpeg";

    public static final String Syl1 = "Scenes/Sylvara/SylvaraCutscene (1).jpeg";
    public static final String Syl2_3 = "Scenes/Sylvara/SylvaraCutscene (2).jpeg";

    public static final String Emb1 = "Scenes/Embjorn/EmbjornCutscene (1).jpeg";
    public static final String Emb2_3 = "Scenes/Embjorn/EmbjornCutscene (2).jpeg";

    //ICON
    public static final String GAME_ICON = "Icon.jpg";

    //Stats
    public static final String Hearts_Atlas = "Characters/Hero/Stats/Life.png";
    public static final String Mana_Atlas = "Characters/Hero/Stats/Mana.png";

    //Character Selection
    public static final String CSelection_Atlas = "Characters/CharacterSelectBG.png";

    //Death Screen
    public static final String DeathScreen = "DeathScreen/DeathScreen.jpeg";

    //Pause
    public static final String PauseBg = "Pause/PauseBg.png";

    //Save
    public static final String SaveBg = "SaveMenu/SaveMenu.png";

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
    public static final String KAELOR_ATLAS = "Characters/Enemy/Kaelor.png";
    public static final String KAELOR_ATK_ATLAS = "Characters/Enemy/RockFalling.png";

    public static final String SYLTHRA_ATLAS = "Characters/Enemy/Sylthra.png";
    public static final String SYLTHRA_ATK_ATLAS = "Characters/Enemy/Sylthra_projectile.png";
    public static final String STARS_ATLAS = "Characters/Enemy/Stars.png";

    //Objects
    public static final String VASE_ATLAS = "Levels/Objects/Vase/vase_object.png";
    public static final String SPIKE_ATLAS = "Levels/Level1/spikes_object.png";
    public static final String LADDER_ATLAS = "Levels/Objects/Ladder/LadderSprite.png";
    public static final String CRUMBLING_TILE_ATLAS = "Levels/Objects/CrumblingTile/CrumblingTile.png";
    public static final String PLATFORM_ATLAS = "Levels/Objects/Platform/RockyPlatform.png";

    //Potions
    public static final String HEALTH_POTION_ATLAS = "Levels/Objects/DropHealthSS.png";
    public static final String MANA_POTION_ATLAS = "Levels/Objects/DropManaSS.png";

    //Leaderboard

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

    //LEADERBOARD
    public static void AddLeaderboardEntry(LeaderboardEntry entry) {
        ArrayList<LeaderboardEntry> list = GetLeaderboard();
        list.add(entry);
        // Automatically sort and cap to top 10 best runs before saving
        list.sort(Comparator.comparingLong(LeaderboardEntry::getRawTicks));
        if (list.size() > 10) {
            list = new ArrayList<>(list.subList(0, 10));
        }
        SaveLeaderboardAsText(list);
    }

    public static ArrayList<LeaderboardEntry> GetLeaderboard() {
        ArrayList<LeaderboardEntry> entries = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) return entries;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    entries.add(new LeaderboardEntry(
                            parts[0],
                            parts[1],
                            parts[2],
                            Integer.parseInt(parts[3]),
                            Long.parseLong(parts[4])
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static void SaveLeaderboardAsText(ArrayList<LeaderboardEntry> entries) {
        entries.sort(Comparator.comparingLong(LeaderboardEntry::getRawTicks));
        if (entries.size() > 10) {
            entries = new ArrayList<>(entries.subList(0, 10));
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (LeaderboardEntry e : entries) {
                writer.println(e.getName() + "," + e.getCharacter() + "," +
                        e.getTime() + "," + e.getDeaths() + "," + e.getRawTicks());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}