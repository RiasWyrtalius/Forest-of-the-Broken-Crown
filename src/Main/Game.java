package Main;

import Audio.AudioPlayer;
import Entities.Boss;
import Entities.Player;
import Entities.Projectiles.Projectile;
import Levels.LevelHandler;
import Objects.ObjectManager;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Game implements Runnable {

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int lvlTilesWide = LoadSave.getLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;

    private MainMenu mainMenu;
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private Player player;
    private Boss boss;
    private LevelHandler levelHandler;
    private SlotScreen slotScreen;
    private PauseScreen pauseScreen;
    private AudioPlayer audioPlayer;


    // this generates "Saved Game!" on screen
    private String saveMessage = "";
    private long saveMessageTimer = 0;
    private final long MESSAGE_DURATION = 2000; // this shows the message for 2 seconds

    // this makes the transition to black
    private boolean fadingOut = false; // this fades to black
    private boolean fadingIn  = false; // this fades back from black
    private int fadeAlpha     = 0;
    private int fadeTarget    = 0;
    private final int FADE_SPEED = 5;

    public final static int TILES_DEFAULT_SIZE = 32;
    public final static int SPRITE_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    private BufferedImage backgroundImg;

    private UI ui;

    private ObjectManager objectManager;

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();

        backgroundImg = LoadSave.getSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMAGE);
    }

    public void initClasses() {
        levelHandler = new LevelHandler(this);
        objectManager = new ObjectManager();
        //TODO: spawnpoint & fix resizing of character
        player = new Player(200, 200, 80, 80, levelHandler.getCurrentLevel().getLevelData());
        int[][] lvlData = levelHandler.getCurrentLevel().getLevelData();
        int bossWidth = 250;
        int bossHeight = 250;
        // Hard-coded boss position: 50 blocks right and 5 blocks down from player spawn (200, 200)
        float bossX = 200 + 53 * TILES_SIZE;
        float bossY = 200 + 4* TILES_SIZE;
        boss = new Boss(bossX, bossY, bossWidth, bossHeight, lvlData);
        ui = new UI(this);
        audioPlayer = new AudioPlayer();
        mainMenu = new MainMenu(this);
        slotScreen = new SlotScreen(this);
        pauseScreen = new PauseScreen(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {

        // FADE OUT — go from transparent to black
        if (fadingOut) {
            fadeAlpha += FADE_SPEED;
            if (fadeAlpha >= 255) {
                fadeAlpha  = 255;
                fadingOut  = false;
                fadingIn   = true;             // start fading back in
                GameState.state = fadeTarget;  // switch to the new state
            }
        }

        // FADE IN — go from black back to transparent
        if (fadingIn) {
            fadeAlpha -= FADE_SPEED;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                fadingIn  = false;             // fade fully done
            }
        }

        if (GameState.state == GameState.PLAYING) {
            player.update();
            boss.update(player);
            levelHandler.update();
            objectManager.update(levelHandler.getCurrentLevel().getLevelData(), player);
            checkCloseToBorder();

            // Check player projectiles hitting boss
            for (Projectile p : player.getProjectiles()) {
                if (p.isActive() && new Rectangle(p.getX(), p.getY(), 48, 48).intersects(boss.getHitbox())) {
                    boss.takeDamage(1);
                    p.setActive(false);
                }
            }

            // Check boss projectiles hitting player
            for (Projectile p : boss.getProjectiles()) {
                if (p.isActive() && new Rectangle(p.getX(), p.getY(), 48, 48).intersects(player.getHitbox())) {
                    player.loseLife();
                    p.setActive(false);
                }
            }
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder) {
            xLvlOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLvlOffset += diff - leftBorder;
        }

        if (xLvlOffset > maxLvlOffsetX){
            xLvlOffset = maxLvlOffsetX;
        } else if (xLvlOffset < 0){
            xLvlOffset = 0;
        }
    }

    public void render(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        if (GameState.state == GameState.MENU) {
            mainMenu.draw(g);

        } else if (GameState.state == GameState.SLOTS) {
            // draw the game behind the slot screen so it doesn't look empty
            levelHandler.draw(g, xLvlOffset);
            player.render(g, xLvlOffset);
            boss.render(g, xLvlOffset);
            slotScreen.draw(g);
        } else if (GameState.state == GameState.PAUSED) {
            levelHandler.draw(g, xLvlOffset);
            player.render(g, xLvlOffset);
            boss.render(g, xLvlOffset);
            pauseScreen.draw(g);
        } else {
            levelHandler.draw(g, xLvlOffset);
            objectManager.draw(g, xLvlOffset);
            player.render(g, xLvlOffset);
            boss.render(g, xLvlOffset);

            ui.draw(g);

            // this shows the "Saved Game!" message on screen
            if (!saveMessage.isEmpty()) {
                long elapsed = System.currentTimeMillis() - saveMessageTimer;
                if (elapsed < MESSAGE_DURATION) {
                    g.setFont(new Font("Arial", Font.BOLD, 18));
                    FontMetrics fm = g.getFontMetrics();
                    int msgX = (GAME_WIDTH / 2) - (fm.stringWidth(saveMessage) / 2);
                    g.setColor(Color.BLACK);
                    g.drawString(saveMessage, msgX, 40);
                } else {
                    saveMessage = ""; // this clears the message after 2 seconds
                }
            }
        }
        // it draws a fade overlay on top of everything
        if (fadingOut || fadingIn || fadeAlpha > 0) {
            g.setColor(new Color(0, 0, 0, Math.min(fadeAlpha, 255)));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }
    }

    public void resetAll() {
        player.resetAll();
        objectManager.resetAllObjects();
        //TODO: enemy reset can go here.
    }

    public void startFadeTo(int targetState) {
        fadingOut  = true;
        fadingIn   = false;
        fadeAlpha  = 0;
        fadeTarget = targetState;
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
                frames = 0;
                updates = 0;
            }
        }
    }

    public void setSaveMessage(String msg) {
        saveMessage = msg;
        saveMessageTimer = System.currentTimeMillis();
    }

    public void windowFocusLost() {
        player.resetDirectionBooleans();
    }

    public Player getPlayer() {return player;}
    public MainMenu getMainMenu() {return mainMenu;}
    public LevelHandler getLevelHandler() {return levelHandler;}
    public SlotScreen getSlotScreen()     {return slotScreen;}
    public PauseScreen getPauseScreen() {return pauseScreen;}
    public AudioPlayer getAudioPlayer() {return audioPlayer;}
}