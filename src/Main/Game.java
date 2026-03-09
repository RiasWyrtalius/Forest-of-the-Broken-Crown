package Main;

import Entities.Player;
import Levels.LevelHandler;

import java.awt.*;

public class Game implements Runnable{

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private Player player;
    private LevelHandler levelHandler;

    public final static int TILES_DEFAULT_SIZE = 32;
    private final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    public void initClasses() {
        player = new Player(200, 200);
        levelHandler = new LevelHandler(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        player.update();
        levelHandler.update();
    }

    public void render(Graphics g) {
        levelHandler.draw(g);
        player.render(g);
    }

    /**
     * NOTE by Charlz:
     * Usages of UPS(Update Per Second) & FPS(Frames Per Second)
     *
     * UPS is used to handle Game Logic, while the FPS is for Graphics Rendering.
     * UPS is set to 200, just for a higher frequency, simple terms it just ensures we have good movement and controls is responsive.
     * Its now handled like this because we don't want our GameLoop to handle both graphics and game logic, that's bad(ahh pc).
     * deltaU is used to catch up incase the user's pc lags and will continue to calculate the logic before draws the next frame(repaint()),
     * it's just to make it sync better.
     *
     * if you wanna discuss the math, don't please, im braindead. t-t
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

    public void windowFocusLost() {
        player.resetDirectionBooleans();
    }

    public Player getPlayer() {return player;}

}
