package Entities.Boss;

import Entities.Player;
import Levels.LevelHandler;
import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.LEFT;
import static Utils.Constants.RIGHT;
import static Utils.LoadSave.getFont;

public class Kaelor extends Boss {
    private LevelHandler lh;
    private Font customFont;

    private int survivalTick = 150 * 200; // 150 seconds * 200 UPS = 30,000 ticks
    private boolean survivalEnded = false;
    private boolean timerStarted = false;
    private int antiCheeseTick = 0;
    private final int ANTI_CHEESE_MAX = 4 * 200;
    private int rewardTick = 0;
    private final int REWARD_INTERVAL = 30 * 200;

    private CopyOnWriteArrayList<Rock> rocks = new CopyOnWriteArrayList<>();
    private int rockCDTick = 0;
    private final int ROCK_CD_MAX = 2 * 200; // 2 seconds
    private Random rng = new Random();
    private Rectangle2D.Float attackBox;
    private float rockSpread = 1200 * Game.SCALE;

    private boolean powerupTriggered = false;

    public Kaelor(float x, float y) {
        super(x, y, KAELOR_WIDTH, KAELOR_HEIGHT, KAELOR);
        loadAnimations();
        initHitbox(bossData.hbWidth, bossData.hbHeight);

        this.enemyState = IDLE;
        this.attackDistance = Game.TILES_SIZE * 1.5f;
        this.walkSpeed = 0.6f * Game.SCALE;

        this.lh = Game.getInstance().getLevelHandler();
        initAttackBox();
        customFont = getFont("Font/VCR.ttf").deriveFont(28f);
    }

    private void drawTimer(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setFont(customFont);
        g.setColor(Color.WHITE);

        String timeText = getTimeLeftString();

        int textWidth = g.getFontMetrics().stringWidth(timeText);
        int xPos = (int) (hitbox.x - xLvlOffset + (hitbox.width / 2) - (textWidth / 2));
        int yPos = (int) (hitbox.y - yLvlOffset - 20); //above boss

        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(timeText, xPos + 2, yPos + 2);

        g.setColor(Color.YELLOW);
        g.drawString(timeText, xPos, yPos);
    }

    protected void loadAnimations() {
        BufferedImage img = Utils.LoadSave.getSpriteAtlas(LoadSave.KAELOR_ATLAS);

        // Rows:
        // 0=IdleR, 1=IdleL
        // 2=AtkR, 3=AtkL
        // 4=WalkR, 5=WalkL
        // 6=ShutdownL, 7=ShutdownR
        // 8=DeadL, 9=DeadR
        // 10=DamageL, 11=DamageR
        // 12=Powerup
        int[] spriteAmounts = { 6, 6,
                                6, 6,
                                8, 8,
                                7, 7,
                                1, 1,
                                3, 3,
                                8};

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
       //System.out.println("Level: " + lh.getLevelIndex() + " | State: " + enemyState + " | inAir: " + inAir);

        if (enemyState == DEAD) {
            return;
        }

        if (enemyState == SHUTDOWN) {
            updateAnimationTick();
            if (animationIndex == 0 && animationTick == 0) {
                newState(DEAD);
                //TODO: FIX NO LOOT DROPS
            }
            return; // stops everything
        }

        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }
        if (inAir) {
            updateInAir(lvlData);
        }

        if (lh.getLevelIndex() == 3) { // KAELOR BOSS AREA

            //waiting for player
            if (!powerupTriggered) {
                newState(POWERUP);
                animationIndex = 0;
                animationTick = 0;
                checkPowerupTrigger(player);
                return;
            }

            //player entered then player animation once
            else if (enemyState == POWERUP && !timerStarted) {
                updateAnimationTick();
                if (animationIndex == 0 && animationTick == 0) {
                    timerStarted = true;
                    newState(IDLE);
                }
                return;
            }

            //fight started
            else if (timerStarted && !survivalEnded) {
                updateSurvivalTimer(player);

                if (!survivalEnded) {
                    updateStationaryBehavior(player);
                    updateRocks(lvlData, player);
                }
            }
        } else {
            updateMobileBehavior(lvlData, player);
        }


        if (enemyState == HIT) {
            updateAnimationTick();
            if (animationIndex >= getSpriteAmount() - 1) {
                newState(IDLE);
            }
            return;
        }

        updateAnimationTick();
        updateAttackBox();
    }

    private void checkPowerupTrigger(Player player) {
        float centerX = hitbox.x + (hitbox.width / 2);
        float halfSpread = rockSpread / 2;

        float leftBoundary = centerX - halfSpread;
        float rightBoundary = centerX + halfSpread;

        if (player.getHitbox().x >= leftBoundary && player.getHitbox().x <= rightBoundary) {
            powerupTriggered = true;
        }
    }

    private void updateStationaryBehavior(Player player) {
        //lock state if atking
        if (enemyState == ATTACK) {
            if (animationIndex >= 3 && animationIndex <= 5) {
                checkEnemyHit(attackBox, player);
            }
            return;
        }

        // look at player
        if (player.getHitbox().x < hitbox.x) walkDir = LEFT;
        else walkDir = RIGHT;

        if (isPlayerCloseForAttack(player)) {
            newState(ATTACK);
        } else {
            newState(IDLE);
        }
    }

    private void updateMobileBehavior(int[][] lvlData, Player player) {
        int targetDir = (player.getHitbox().x < hitbox.x) ? LEFT : RIGHT;
        walkDir = targetDir;

        if (enemyState == ATTACK) {
            if (animationIndex >= 3 && animationIndex <= 5) {
                checkEnemyHit(attackBox, player);
            }
        } else {
            if (isPlayerCloseForAttack(player)) {
                newState(ATTACK);
            } else {
                newState(RUNNING);
                move(lvlData);
                walkDir = targetDir;
            }
        }
    }

    private void updateRocks(int[][] lvlData, Player player) {
        rockCDTick++;
        antiCheeseTick++;

        //small rocks
        if (rockCDTick >= ROCK_CD_MAX) {
            spawnRockWave();
            rockCDTick = 0;
        }

        //spawns rock on player
        if (antiCheeseTick >= ANTI_CHEESE_MAX) {
            spawnTargetedRock(player);
            antiCheeseTick = 0;
        }

        for (int i = rocks.size() - 1; i >= 0; i--) {
            Rock r = rocks.get(i);
            r.update(lvlData);

            if (r.getHitbox().intersects(player.getHitbox())) {
                player.applyKnockback(r.getHitbox().x);

                if (r.getSizeMult() >= 1.1f) {
                    player.changeHealth(-1);
                }

                rocks.remove(i);
            } else if (!r.isActive()) {
                rocks.remove(i);
            }
        }
    }

    private void spawnRockWave() {
        int rockCount = 4;
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

    private void spawnTargetedRock(Player player) {
        float targetX = (player.getHitbox().x + (player.getHitbox().width / 2));
        float spawnY = -rng.nextInt(300) - 500;
        float size = 1.2f;
        rocks.add(new Rock(targetX, spawnY, size));
    }

    @Override
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.draw(g, xLvlOffset, yLvlOffset);

        drawHitbox(g, xLvlOffset, yLvlOffset);

        if (lh.getLevelIndex() == 3) {
            if (!survivalEnded) {
                drawTimer(g, xLvlOffset, yLvlOffset);
            }

            for (Rock r : rocks) {
                r.draw(g, xLvlOffset, yLvlOffset);
            }
        }


        //atk box hitbox
//        if (attackBox != null) {
//            g.setColor(Color.BLUE);
//            g.drawRect((int) (attackBox.x - xLvlOffset), (int) (attackBox.y - yLvlOffset), (int) attackBox.width, (int) attackBox.height);
//        }
    }

    //TIME
    private void updateSurvivalTimer(Player player) {
        if (!timerStarted) {
            float centerX = hitbox.x + (hitbox.width / 2);
            float halfSpread = rockSpread / 2;

            float leftBoundary = centerX - halfSpread;
            float rightBoundary = centerX + halfSpread;

            if (player.getHitbox().x >= leftBoundary && player.getHitbox().x <= rightBoundary) {
                timerStarted = true;
            }
        }

        // countdown logic
        if (timerStarted && !survivalEnded) {
            if (survivalTick > 0) {
                survivalTick--;
                rewardTick++;
                if (rewardTick >= REWARD_INTERVAL) {
                    applySurvivalReward(player);
                    rewardTick = 0;
                }
            } else {
                survivalEnded = true;
                newState(SHUTDOWN);
            }
        }
    }

    private void applySurvivalReward(Player player) {
        player.changeHealth(1);
        player.addMana(5);
//        System.out.println("Survival Reward granted!");
    }

    private String getTimeLeftString() {
        int totalSeconds = survivalTick / 200; // Convert ticks back to seconds
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected int getAnimationRow() {
        return switch (enemyState) {
            case ATTACK -> (walkDir == RIGHT) ? 2 : 3;
            case RUNNING -> (walkDir == RIGHT) ? 4 : 5;
            case SHUTDOWN -> (walkDir == RIGHT) ? 7 : 6;
            case DEAD -> (walkDir == RIGHT) ? 9 : 8;
            case HIT -> (walkDir == RIGHT) ? 11 : 10;
            case POWERUP -> 12;
            default -> (walkDir == RIGHT) ? 0 : 1; // IDLE
        };
    }

    @Override
    public int getSpriteAmount() {
        return switch (enemyState) {
            case RUNNING, POWERUP -> 8;
            case SHUTDOWN -> 7;
            case DEAD -> 1;
            case HIT -> 4;
            default -> 6; // Idle and Attack
        };
    }
}