package Objects;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Utils.Constants.ObjectConstants.*;

class CrumblingTile extends GameObject {
    private static final int COLS        = 8;
    private static final int ROWS        = 6;
    private static final int FRAME_SPEED = 20; //tick between frames

    private int tileType;
    /**
        * 0 - Whole
        * 1 - Left
        * 2 - Right
    * */

    private static final int SHAKE_TICKS   = 80;  // 0.4s
    private static final int RESPAWN_TICKS = 800; // 4s

    private static BufferedImage[][] sprites;

    private enum State { IDLE, SHAKING, CRUMBLING, GONE }
    private State state = State.IDLE;

    private int shakeTick   = 0;
    private int respawnTick = 0;

    // Shake visual wobble
    private int shakeOffset  = 0;
    private int shakeDirTick = 0;

    // Current frame to draw
    private int drawRow   = 0;
    private int drawCol   = 0;
    private int frameTick = 0;

    // Tile grid position - used to punch holes in lvlData on crumble/respawn
    private final int tileGridX;
    private final int tileGridY;

    public CrumblingTile(float x, float y, int tileType) {
        super(x, y, CRUMBLING_TILE);
        this.tileType = tileType;

        //hitbox coords
        initHitbox(Game.TILES_DEFAULT_SIZE, Game.TILES_DEFAULT_SIZE);
        this.tileGridX = (int) (x / Game.TILES_SIZE);
        this.tileGridY = (int) (y / Game.TILES_SIZE);

        loadSprites();
    }

    private static void loadSprites() {
        if (sprites != null) return;

        BufferedImage sheet = LoadSave.getSpriteAtlas(LoadSave.CRUMBLING_TILE_ATLAS);
        if (sheet == null) return;

        sprites = new BufferedImage[ROWS][COLS];
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++)
                sprites[row][col] = sheet.getSubimage(
                        col * Game.TILES_DEFAULT_SIZE,
                        row * Game.TILES_DEFAULT_SIZE
                        , Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE);
    }

    public void update(int[][] lvlData) {
        switch (state) {
            case IDLE      -> updateIdle();
            case SHAKING   -> updateShaking();
            case CRUMBLING -> updateCrumbling(lvlData);
            case GONE      -> updateGone(lvlData);
        }
    }

    private void updateIdle() {
        drawRow = tileType;
        advanceFrame(5);
    }

    private void updateShaking() {
        shakeTick++;
        shakeDirTick++;

        // wobble l/r
        if (shakeDirTick >= 5) {
            shakeDirTick = 0;
            shakeOffset  = (shakeOffset == -2) ? 2 : -2;
        }

        drawRow = tileType;
        advanceFrame(5);

        if (shakeTick >= SHAKE_TICKS) beginCrumbling();
    }

    private void updateCrumbling(int[][] lvlData) {
        drawRow = tileType + 3; // shift row 3,4,5

        frameTick++;
        if (frameTick >= FRAME_SPEED) {
            frameTick = 0;
            drawCol++;

            if (drawCol >= 7) {
                state       = State.GONE;
                active      = false;
                shakeOffset = 0;
                punchHole(lvlData, AIR);
            }
        }
    }

    private void updateGone(int[][] lvlData) {
        respawnTick++;
        if (respawnTick >= RESPAWN_TICKS)
            respawn(lvlData);
    }

    private void beginCrumbling() {
        state       = State.CRUMBLING;
        shakeOffset = 0;
        drawRow     = tileType + 3;
        drawCol     = 0;
        frameTick   = 0;
    }

    private void respawn(int[][] lvlData) {
        state       = State.IDLE;
        active      = true;
        shakeTick   = 0;
        respawnTick = 0;
        shakeOffset = 0;
        drawRow     = tileType;
        drawCol     = 0;
        frameTick   = 0;
        punchHole(lvlData, INVISIBLE_SOLID); // restore to solid tile
    }

    public void onPlayerStanding() {
        if (state == State.IDLE) {
            state     = State.SHAKING;
            shakeTick = 0;
        }
    }

    //Draw
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (state == State.GONE || sprites == null) return;

        g.drawImage(
                sprites[drawRow][drawCol],
                (int) (hitbox.x - xLvlOffset) + shakeOffset,
                (int) (hitbox.y - yLvlOffset),
                Game.TILES_SIZE,
                Game.TILES_SIZE,
                null
        );
    }

    //Helper Methods
    /** Advance animation frame column cyclically. */
    private void advanceFrame(int maxFrames) {
        frameTick++;
        if (frameTick >= FRAME_SPEED) {
            frameTick = 0;
            drawCol++;
            if (drawCol >= maxFrames) {
                drawCol = 0;
            }
        }
    }

    /** Write tileValue into the live level data at this tile's grid position. */
    private void punchHole(int[][] lvlData, int tileValue) {
        if (lvlData == null) return;
        if (tileGridY >= 0 && tileGridY < lvlData.length &&
                tileGridX >= 0 && tileGridX < lvlData[0].length) {
            lvlData[tileGridY][tileGridX] = tileValue;
        }
    }

    public void forceRestore(int[][] lvlData) {
        punchHole(lvlData, INVISIBLE_SOLID);
    }

    //Getter
    public boolean isGone()      { return state == State.GONE;      }
    public boolean isShaking()   { return state == State.SHAKING;   }
    public boolean isCrumbling() { return state == State.CRUMBLING; }
    public int getTileType()     { return tileType; }
}
