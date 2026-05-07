package Entities.Boss;

import Entities.Player;
import Levels.LevelHandler;
import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.LEFT;
import static Utils.Constants.RIGHT;

public class Kaelor extends Boss {
    private LevelHandler lh;

    private int survivalTimer = 30000; // 150 seconds * 200 UPS = 30,000 ticks
    private java.util.concurrent.CopyOnWriteArrayList<Rock> rocks = new java.util.concurrent.CopyOnWriteArrayList<>();
    private int rockCDTick = 0;
    private final int ROCK_CD_MAX = 2 * 200; // 2 seconds
    private Random rng = new Random();
    private Rectangle2D.Float attackBox;
    private float rockSpread = 1200 * Game.SCALE;

    public Kaelor(float x, float y) {
        super(x, y, KAELOR_WIDTH, KAELOR_HEIGHT, KAELOR);
        loadAnimations();
        initHitbox(bossData.hbWidth, bossData.hbHeight);
        hitbox.y += (28 * Game.SCALE); // lower hitbox
        this.enemyState = IDLE;
        this.attackDistance = Game.TILES_SIZE * 1.5f;
        this.lh = Game.getInstance().getLevelHandler();
        initAttackBox();
    }

    protected void loadAnimations() {
        BufferedImage img = Utils.LoadSave.getSpriteAtlas(LoadSave.KAELOR_ATLAS);

        // Kaelor's Rows:
        // 0=IdleL, 1=IdleR,
        // 2=AtkL, 3=AtkR,
        // 4=WalkL,
        int[] spriteAmounts = { 6, 6, 6, 6, 8 };

        animations = new BufferedImage[spriteAmounts.length][];

        for (int j = 0; j < animations.length; j++) {
            animations[j] = new BufferedImage[spriteAmounts[j]];
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(
                        i * KAELOR_WIDTH_DEFAULT,
                        j * KAELOR_HEIGHT_DEFAULT,
                        KAELOR_WIDTH_DEFAULT,
                        KAELOR_HEIGHT_DEFAULT);
            }
        }
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(80 * Game.SCALE), hitbox.height);
    }

    private void updateAttackBox() {
        int xOffset = (int)(20 * Game.SCALE);

        if (walkDir == RIGHT) {
            attackBox.x = hitbox.x + hitbox.width - xOffset;
        } else {
            attackBox.x = hitbox.x - attackBox.width + xOffset;
        }
        attackBox.y = hitbox.y;
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        int currentLvl = lh.getLevelIndex();


        if (currentLvl == 3) { // Level 2 Boss (Index 3)
            updateStationaryBehavior(player);
            updateRocks(lvlData, player);
        } else { // Level 3 Enemy (Mobile)
            updateMobileBehavior(lvlData, player);
        }

        updateAnimationTick();
        updateAttackBox();
    }

    private void updateStationaryBehavior(Player player) {
        if (player.getHitbox().x < hitbox.x) walkDir = LEFT;
        else walkDir = RIGHT;

        if (isPlayerCloseForAttack(player)) {
            newState(ATTACK);

            if (animationIndex >= 3 && animationIndex <= 5) {
                checkEnemyHit(attackBox, player);
            }
        } else {
            newState(IDLE); // Stay still
        }
    }

    private void updateMobileBehavior(int[][] lvlData, Player player) {
        move(lvlData);

        if (isPlayerCloseForAttack(player)) {
            newState(ATTACK);

            if (animationIndex >= 3 && animationIndex <= 5) {
                checkEnemyHit(attackBox, player);
            }
        } else {
            newState(RUNNING);
        }
    }

    private void updateRocks(int[][] lvlData, Player player) {
        rockCDTick++;

        if (rockCDTick >= ROCK_CD_MAX) {
            spawnRockWave();
            rockCDTick = 0; // reset
        }

        for (int i = rocks.size() - 1; i >= 0; i--) {
            Rock r = rocks.get(i);
            r.update(lvlData);

            if (r.getHitbox().intersects(player.getHitbox())) {
                player.changeHealth(-1);
                player.applyKnockback(r.getHitbox().x);
                rocks.remove(i);
            } else if (!r.isActive()) {
                rocks.remove(i);
            }
        }
    }

    private void spawnRockWave() {
        int rockCount = 5;
        float totalSpread = rockSpread;
        float laneWidth = totalSpread / rockCount;
        float startX = (hitbox.x + hitbox.width / 2) - (totalSpread / 2);

        for (int i = 0; i < rockCount; i++) {
            float jiggle = (float) (rng.nextDouble() * laneWidth);
            float spawnX = startX + (i * laneWidth) + jiggle;

            if (spawnX < 0) spawnX = 0;

            float randomSize = 0.5f + (rng.nextFloat() * 0.8f);
            float spawnY = -rng.nextInt(400) - 100;

            rocks.add(new Rock(spawnX, spawnY, randomSize));
        }
    }

    @Override
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.draw(g, xLvlOffset, yLvlOffset);

        for (Rock r : rocks) {
            r.draw(g, xLvlOffset, yLvlOffset);
        }

        if (attackBox != null) {
            g.setColor(Color.BLUE);
            g.drawRect((int) (attackBox.x - xLvlOffset), (int) (attackBox.y - yLvlOffset), (int) attackBox.width, (int) attackBox.height);
        }
    }

    @Override
    protected int getAnimationRow() {
        return switch (enemyState) {
            case ATTACK -> (walkDir == RIGHT) ? 3 : 2;
            case RUNNING -> (walkDir == RIGHT) ? 5 : 4;
            default -> (walkDir == RIGHT) ? 1 : 0;
        };
    }

    @Override
    public int getSpriteAmount() {
        return switch (enemyState) {
            case RUNNING -> 8; // left/right
            default -> 6;      // Idle and Attack
        };
    }
}