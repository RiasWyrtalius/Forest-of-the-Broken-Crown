package Objects;

import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Utils.Constants.ObjectConstants.AIR;
import static Utils.Constants.ObjectConstants.CRUMBLING_TILE;

class CrumblingTile extends GameObject {

    // Sprite sheet layout: 512x128 px, 32x32 per frame, 16 columns x 4 rows
    private static final int SPRITE_W    = 32;
    private static final int SPRITE_H    = 32;
    private static final int COLS        = 16;
    private static final int ROWS        = 4;
    private static final int FRAME_SPEED = 16; // ticks between animation frames

    // Timings (game runs at ~200 UPS)
    private static final int SHAKE_TICKS   = 80;  // ~0.4s of shaking before crumble begins
    private static final int CRUMBLE_TICKS = 160; // ~0.8s for the crumble animation to finish
    private static final int RESPAWN_TICKS = 800; // ~4s before the tile comes back

    private static BufferedImage[][] sprites; // [row][col], loaded once for all instances

    private enum State { IDLE, SHAKING, CRUMBLING, GONE }
    private State state = State.IDLE;

    private int shakeTick   = 0;
    private int crumbleTick = 0;
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

    public CrumblingTile(float x, float y) {
        super(x, y, CRUMBLING_TILE);
        initHitbox(Game.TILES_DEFAULT_SIZE, Game.TILES_DEFAULT_SIZE);
        this.tileGridX = (int) (x / Game.TILES_SIZE);
        this.tileGridY = (int) (y / Game.TILES_SIZE);
        loadSprites();
    }

    // -----------------------------------------------------------------------
    // Sprite loading (shared across all instances)
    // -----------------------------------------------------------------------

    private static void loadSprites() {
        if (sprites != null) return;

        BufferedImage sheet = LoadSave.getSpriteAtlas(LoadSave.CRUMBLING_TILE_ATLAS);
        if (sheet == null) return;

        sprites = new BufferedImage[ROWS][COLS];
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++)
                sprites[row][col] = sheet.getSubimage(
                        col * SPRITE_W, row * SPRITE_H, SPRITE_W, SPRITE_H);
    }

    // -----------------------------------------------------------------------
    // Update - called every game tick from ObjectManager
    // -----------------------------------------------------------------------

    public void update(int[][] lvlData) {
        switch (state) {
            case IDLE      -> updateIdle();
            case SHAKING   -> updateShaking();
            case CRUMBLING -> updateCrumbling(lvlData);
            case GONE      -> updateGone(lvlData);
        }
    }

    private void updateIdle() {
        drawRow = 0;
        advanceFrame();
    }

    private void updateShaking() {
        shakeTick++;

        // Wobble left/right a couple of pixels
        shakeDirTick++;
        if (shakeDirTick >= 5) {
            shakeDirTick = 0;
            shakeOffset  = (shakeOffset == -2) ? 2 : -2;
        }

        drawRow = 0;
        advanceFrame();

        if (shakeTick >= SHAKE_TICKS)
            beginCrumbling();
    }

    private void updateCrumbling(int[][] lvlData) {
        crumbleTick++;

        // Map crumble progress to rows 1-3
        float progress = (float) crumbleTick / CRUMBLE_TICKS;
        drawRow = 1 + Math.min((int) (progress * (ROWS - 1)), ROWS - 2);

        advanceFrame();

        if (crumbleTick >= CRUMBLE_TICKS) {
            state       = State.GONE;
            active      = false;
            shakeOffset = 0;
            punchHole(lvlData, AIR); // player can now fall through
        }
    }

    private void updateGone(int[][] lvlData) {
        respawnTick++;
        if (respawnTick >= RESPAWN_TICKS)
            respawn(lvlData);
    }

    // -----------------------------------------------------------------------
    // State transitions
    // -----------------------------------------------------------------------

    private void beginCrumbling() {
        state       = State.CRUMBLING;
        crumbleTick = 0;
        shakeOffset = 0;
        drawRow     = 1;
        drawCol     = 0;
        frameTick   = 0;
    }

    private void respawn(int[][] lvlData) {
        state       = State.IDLE;
        active      = true;
        shakeTick   = 0;
        crumbleTick = 0;
        respawnTick = 0;
        shakeOffset = 0;
        drawRow     = 0;
        drawCol     = 0;
        frameTick   = 0;
        punchHole(lvlData, 0); // restore to solid tile (index 0)
    }

    // -----------------------------------------------------------------------
    // Called by ObjectManager each tick the player is standing on this tile
    // -----------------------------------------------------------------------

    public void onPlayerStanding() {
        if (state == State.IDLE) {
            state     = State.SHAKING;
            shakeTick = 0;
        }
    }

    // -----------------------------------------------------------------------
    // Draw
    // -----------------------------------------------------------------------

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (state == State.GONE || sprites == null) return;

        int safeRow = Math.min(drawRow, ROWS - 1);
        int safeCol = Math.min(drawCol, COLS - 1);

        g.drawImage(
                sprites[safeRow][safeCol],
                (int) (hitbox.x - xLvlOffset) + shakeOffset,
                (int) (hitbox.y - yLvlOffset),
                Game.TILES_SIZE,
                Game.TILES_SIZE,
                null
        );
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Advance animation frame column cyclically. */
    private void advanceFrame() {
        frameTick++;
        if (frameTick >= FRAME_SPEED) {
            frameTick = 0;
            drawCol   = (drawCol + 1) % COLS;
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

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    public boolean isGone()      { return state == State.GONE;      }
    public boolean isShaking()   { return state == State.SHAKING;   }
    public boolean isCrumbling() { return state == State.CRUMBLING; }
}
