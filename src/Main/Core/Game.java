
package Main.Core;

import Audio.AudioPlayer;
import Entities.Boss.EnemyManager;
import Entities.Player;
import Entities.PlayerCharacter;
import Levels.Level;
import Levels.LevelHandler;
import Main.GameState;
import Main.GameStates.*;
import Main.GameStates.Scene.CutsceneState;
import Main.UI.UI;
import Objects.ObjectManager;
import Utils.LoadSave;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;


public class Game implements Runnable {


    private GameWindow gameWindow;
    private Playing playing;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;
    private static Game instance;

    private Credits credits;
    private MainMenu mainMenu;
    private Player player;
    private LevelHandler levelHandler;
    private SlotScreen slotScreen;
    private PauseScreen pauseScreen;
    private WorldSelect worldSelect;
    private DeathScreen deathScreen;
    private AudioPlayer audioPlayer;
    private CutsceneState cutsceneState;
    private OptionsScreen optionsScreen;

    // this makes the transition to black
    private boolean fadingOut       = false; // this fades to black
    private boolean fadingIn        = false; // this fades back from black
    private int fadeAlpha           = 0;
    private GameState fadeTarget    = GameState.MENU;
    private final int FADE_SPEED    = 5;

    public final static int TILES_DEFAULT_SIZE  = 32;
    public final static int SPRITE_DEFAULT_SIZE = 64;
    public final static float SCALE             = 1.5f;
    public final static int TILES_IN_WIDTH      = 26;
    public final static int TILES_IN_HEIGHT     = 14;
    public final static int TILES_SIZE          = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH          = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT         = TILES_SIZE * TILES_IN_HEIGHT;

    private BufferedImage backgroundImg;
    private UI ui;
    private ObjectManager objectManager;
    private CharacterSelect characterSelect;
    private int lastFPS = 0;
    private Font fpsFont = Utils.LoadSave.getFont("Font/VCR.ttf").deriveFont(Font.BOLD, 18f);
    private long speedrunTicks = 0;

    public Game() {
        LoadSave.getAllLevels();
        initClasses();
        levelHandler.updateBackground();
        gamePanel = new GamePanel(this);
        worldSelect = new WorldSelect(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        startGameLoop();
        instance = this;
    }

    public void initClasses() {
        levelHandler = new LevelHandler(this);
        Level currentLvl = levelHandler.getCurrentLevel();
        Point spawn = currentLvl.getPlayerSpawn();
        player = new Player(spawn.x, spawn.y, 160, 160, levelHandler.getCurrentLevel().getLevelData(), PlayerCharacter.SYLVARA);
        objectManager = new ObjectManager(this);
        objectManager.loadObjects(levelHandler.getCurrentLevel());
        ui = new UI(this);
        audioPlayer = new AudioPlayer();
        mainMenu = new MainMenu(this);
        characterSelect = new CharacterSelect(this);
        slotScreen = new SlotScreen(this);
        pauseScreen = new PauseScreen(this);
        deathScreen = new DeathScreen(this);
        playing = new Playing(this);
        optionsScreen = new OptionsScreen(this);
        cutsceneState = new CutsceneState(this);
        credits = new Credits(this);
    }

    public void initPlayerCharacter(PlayerCharacter selectedChar, int levelNum) {
        levelHandler.loadLevel(levelNum);
        Level currentLevel = levelHandler.getCurrentLevel();

        player = new Player(0, 0, 160, 160, currentLevel.getLevelData(), selectedChar);
        setupLevel(levelNum);
        player.updateLevelData(currentLevel.getLevelData());

        updateLevelOffsets();
        playing.setPlayer(player);
        playing.resetCamera();

        startFadeTo(GameState.PLAYING);
    }

    public void setupLevel(int levelNum) {
        levelHandler.loadLevel(levelNum);
        playing.loadEnemiesForLevel(levelNum);
        Level cur = levelHandler.getCurrentLevel();
        Point spawn = cur.getPlayerSpawn();

        player.setX(spawn.x);
        player.setY(spawn.y - (player.getHitbox().height - TILES_SIZE));
        player.updateLevelData(cur.getLevelData());
        int savedLife = player.getLife();   // save lives before reset
        player.resetAll();                  // reset position/state
        player.changeHealth(savedLife - player.getMaxLife()); // restore lives

        updateBackground();
        playSongForLevel(levelNum);
        updateLevelOffsets();
        objectManager.loadObjects(cur);
        playing.resetCamera();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        switch (GameState.state) {
            case WORLD_SELECT       -> worldSelect.update();
            case MENU               -> mainMenu.update();
            case PLAYING            -> {
                playing.update();
                speedrunTicks++;
            }
            case CHARACTER_SELECT   -> characterSelect.update();
            case DEATH              -> deathScreen.update();
            case CUTSCENE           -> cutsceneState.update();
            case CREDITS            -> credits.update();
            case OPTIONS            -> optionsScreen.update();
            case SLOTS              -> slotScreen.update();
            case PAUSED             -> {}
        }

        handleFadeLogic();
    }

    public void updateBackground() {
        String bgPath = levelHandler.getCurrentLevel().getBackgroundPath();
        this.backgroundImg = Utils.LoadSave.getSpriteAtlas(bgPath);
    }

    public void render(Graphics g) {
        switch (GameState.state) {
            case WORLD_SELECT -> worldSelect.draw(g);
            case MENU               -> mainMenu.draw(g);
            case PLAYING            -> playing.draw(g);
            case CHARACTER_SELECT   -> characterSelect.draw(g);
            case CREDITS            -> credits.draw(g);
            case CUTSCENE           -> cutsceneState.draw(g);
            case OPTIONS            -> optionsScreen.draw(g);
            case SLOTS -> {
                playing.draw(g);
                slotScreen.draw(g);
            }
            case PAUSED -> {
                playing.draw(g);
                pauseScreen.draw(g);
            }
            case DEATH -> {
                playing.draw(g);
                deathScreen.draw(g);
            }
        }
        if (OptionsScreen.showFPS) {
            g.setColor(Color.YELLOW);
            g.setFont(fpsFont);
            g.drawString("FPS: " + lastFPS, 10, 20);
        }

        // it draws a fade overlay on top of everything
        if (fadingOut || fadingIn || fadeAlpha > 0) {
            g.setColor(new Color(0, 0, 0, Math.min(fadeAlpha, 255)));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }
    }

    public void resetGame() {
        speedrunTicks = 0;
        audioPlayer.playSong(AudioPlayer.MENU_1);
        player.resetAll();
        objectManager.resetAllObjects();
        playing.resetCamera();
        resetSpeedrunTimer();
        levelHandler.loadLevel(1);
        levelHandler.updateBackground();
        player.loadLvlData(levelHandler.getCurrentLevel().getLevelData());
        objectManager.loadObjects(levelHandler.getCurrentLevel());
    }

    public void startFadeTo(GameState targetState) {
        fadingOut  = true;
        fadingIn   = false;
        fadeAlpha  = 0;
        fadeTarget = targetState;
    }

    public void cancelFade() {
        this.fadingOut = false;
        this.fadingIn = false;
        this.fadeAlpha = 0;
    }

    /**
     * NOTE by Charlz:
     * Usages of UPS(Update Per Second) & FPS(Frames Per Second)
     * UPS is used to handle Game Logic, while the FPS is for Graphics Rendering.
     * UPS is set to 200, just for a higher frequency, simple terms it just ensures we have good movement and controls is responsive.
     * Its now handled like this because we don't want our GameLoop to handle both graphics and game logic, that's bad(ahh pc).
     * deltaU is used to catch up in case the user's pc lags and will continue to calculate the logic before draws the next frame(repaint()),
     * it's just to make it sync better.
     * */
    @Override
    public void run() {
        boolean isEnabled = true;
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long prevTime = System.nanoTime();

        long lastCheck = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        double deltaU = 0;
        double deltaF = 0;

        while(isEnabled) {
            long currTime = System.nanoTime();

            deltaU += (currTime - prevTime) / timePerUpdate;
            deltaF += (currTime - prevTime) / timePerFrame;
            prevTime = currTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS " + updates);
                lastFPS = frames;
                frames = 0;
                updates = 0;
            }
        }
    }

    public BufferedImage getCharacterAtlas(PlayerCharacter character) {
        return switch (character) {
            case KAELTHORN -> LoadSave.getSpriteAtlas(LoadSave.Kaelthron_Atlas);
            case SYLVARA -> LoadSave.getSpriteAtlas(LoadSave.Sylvara_Atlas);
            case EMBJORN -> LoadSave.getSpriteAtlas(LoadSave.Embjorn_Atlas);
        };
    }

    private void handleFadeLogic() {
        if (fadingOut) {
            fadeAlpha += FADE_SPEED;
            if (fadeAlpha >= 255) {
                fadeAlpha = 255;
                fadingOut = false;
                fadingIn = true;
                GameState.state = fadeTarget;
            }
        }
        if (fadingIn) {
            fadeAlpha -= FADE_SPEED;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                fadingIn = false;
            }
        }
    }
    public void playSongForLevel(int levelNum) {
        switch (levelNum) {
            case 1 -> audioPlayer.playSong(AudioPlayer.WORLD1);
            case 2 -> audioPlayer.playSong(AudioPlayer.WORLD1_BOSS);
            case 3 -> audioPlayer.playSong(AudioPlayer.WORLD2);
            case 4 -> audioPlayer.playSong(AudioPlayer.WORLD2_BOSS);
            case 5 -> audioPlayer.playSong(AudioPlayer.WORLD3);
            case 6 -> audioPlayer.playSong(AudioPlayer.WORLD3_BOSS);
            default -> audioPlayer.playSong(AudioPlayer.MENU_1);
        }
    }

    public void updateLevelOffsets()                          { playing.updateLevelOffsets(); }
    public void windowFocusLost()                             { player.resetDirectionBooleans(); }
    public void setBackgroundImg(BufferedImage backgroundImg) { this.backgroundImg = backgroundImg; }
    public long getSpeedrunTicks()                            { return speedrunTicks; }
    public void resetSpeedrunTimer()                          { this.speedrunTicks = 0; }

    public Player getPlayer()                   { return player;}
    public LevelHandler getLevelHandler()       { return levelHandler;}
    public AudioPlayer getAudioPlayer()         { return audioPlayer;}
    public ObjectManager getObjectManager()     { return objectManager; }
    public UI getUi()                           { return ui; }
    public BufferedImage getBackgroundImg()     { return backgroundImg; }

    //GameStates
    public CharacterSelect getCharacterSelect() { return characterSelect; }
    public MainMenu getMainMenu()               { return mainMenu;}
    public SlotScreen getSlotScreen()           { return slotScreen;}
    public PauseScreen getPauseScreen()         { return pauseScreen;}
    public DeathScreen getDeathScreen()         { return deathScreen;}
    public Playing getPlaying()                 { return playing; }
    public static Game getInstance()            { return instance; }
    public CutsceneState getCutsceneState()     { return cutsceneState; }
    public Credits getCredits()                 { return credits; }
    public OptionsScreen getOptionsScreen()     { return optionsScreen; }
    public WorldSelect getWorldSelect()         { return worldSelect; }
}