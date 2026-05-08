package Entities.Boss;

import Entities.Player;
import Main.Core.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.LEFT;
import static Utils.Constants.RIGHT;

public class Embryn extends Boss {
    private Rectangle2D.Float attackBox;
    private float chaseSpeed = 2.5f * Game.SCALE;
    private float roamSpeed = 1.2f * Game.SCALE;

    private int chargeTimer = 0;
    private int maxChargeTime = 5 * 200;
    private boolean isTired = false;
    private int tiredTimer = 0;
    private int tiredMaxTime = 3 * 200;

    private boolean defeatMsgSent = false;

    // Enraged system
    private int hitsReceived = 0;
    private final int HITS_TO_ENRAGE = 2;
    private boolean isEnraged = false;
    private int enrageDuration = 4 * 200; // 4 seconds
    private int enrageTimer = 0;
    private float enrageSpeed = 4.5f * Game.SCALE;

    public Embryn(float x, float y) {
        super(x, y, EMBRYN_WIDTH, EMBRYN_HEIGHT, EMBRYN);
        loadAnimations(); // Loads her specific 13 rows
        initHitbox(bossData.hbWidth, bossData.hbHeight);
        initAttackBox();
        this.attackDistance = 0.8f * Game.TILES_SIZE;
    }

    protected void loadAnimations() {
        BufferedImage img = Utils.LoadSave.getSpriteAtlas(Utils.LoadSave.EMBRYN_ATLAS);

        // Rows:
        // 0    =   Idle,
        // 1    =   WalkL,     2   =    WalkR,
        // 3    =   DmgL,      4   =    DmgR,
        // 5    =   DieL,      6   =    DieR,
        // 7    =   DetectL,   8   =    DetectR,
        // 9    =   RunL,      10  =    RunR,
        // 11   =   AtkL,      12  =    AtkR
        int[] spriteAmounts = { 6, 8, 8, 4, 4, 8, 8, 14, 14, 6, 6, 6, 6 };

        animations = new BufferedImage[spriteAmounts.length][];

        for (int j = 0; j < animations.length; j++) {
            animations[j] = new BufferedImage[spriteAmounts[j]];
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(
                        i * EMBRYN_WIDTH_DEFAULT,
                        j * EMBRYN_HEIGHT_DEFAULT,
                        EMBRYN_WIDTH_DEFAULT,
                        EMBRYN_HEIGHT_DEFAULT);
            }
        }
    }

    @Override
    protected int getAnimationRow() {
        return switch (enemyState) {
            case RUNNING -> {

                if (isTired) {
                    yield (walkDir == RIGHT) ? 2 : 1; // walk animation
                }

                else {
                    yield (walkDir == RIGHT) ? 9 : 10; // run animation
                }
            }
            case ATTACK -> (walkDir == RIGHT) ? 12 : 11;
            case DETECT -> (walkDir == RIGHT) ? 7 : 8;
            case HIT ->    (walkDir == RIGHT) ? 4 : 3;
            case DEAD ->   (walkDir == RIGHT) ? 6 : 5;
            default -> 0; // IDLE
        };
    }

    @Override
    public int getSpriteAmount() {
        return switch (enemyState) {
            case RUNNING -> {
                if (isTired) yield 8; // walk
                else yield 6; //run
            }
            case DETECT -> 14;
            case HIT -> 4;
            case DEAD -> 8;
            default -> 6; // IDLE and ATTACK
        };
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y,
                (int) (100 * Game.SCALE),
                (int) (40 * Game.SCALE));
    }

    private void updateAttackBox() {
        if (walkDir == RIGHT)
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * -40f);
        else
            attackBox.x = hitbox.x - attackBox.width - (int) (Game.SCALE * -40f);

        attackBox.y = hitbox.y + (Game.SCALE * 40f);
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.draw(g, xLvlOffset, yLvlOffset);

//        if (attackBox != null) {
//            g.setColor(Color.BLUE);
//            g.drawRect((int) attackBox.x - xLvlOffset,
//                    (int) attackBox.y - yLvlOffset,
//                    (int) attackBox.width,
//                    (int) attackBox.height);
//        }
    }

    @Override
    public void hurt(int amount) {
        super.hurt(amount);

        if (active && enemyState != DEAD) {
            hitsReceived++;
            if (hitsReceived >= HITS_TO_ENRAGE && !isEnraged) {
                isEnraged = true;
                enrageTimer = 0;
                hitsReceived = 0;
                walkSpeed = enrageSpeed;
            }
        }
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        updateHealthStatus();
        updateBehavior(lvlData, player);
        if (isActive() && enemyState != DEAD) {
            checkBodyCollision(player);
        }

        updateAnimationTick();
    }

    private void updateBehavior(int[][] lvlData, Player player) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        updateAttackBox();

        if (isEnraged) {
            enrageTimer++;
            walkSpeed = enrageSpeed;
            if (enrageTimer >= enrageDuration) {
                isEnraged = false;
                enrageTimer = 0;
                hitsReceived = 0;
                walkSpeed = roamSpeed;
            }
        }

        if (isTired) {
            tiredTimer++;
            walkSpeed = roamSpeed;
            move(lvlData);
            if (tiredTimer >= tiredMaxTime) {
                isTired = false;
                tiredTimer = 0;
            }
            return; // skip logic if tired
        }

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case IDLE -> newState(RUNNING);

                case RUNNING -> {
                    playerInSight = canSeePlayer(lvlData, player);

                    if (playerInSight) {
                        // Start Charge sequence
                        if (walkSpeed != chaseSpeed) {
                            newState(DETECT);
                            walkSpeed = chaseSpeed;
                            chargeTimer = 0;
                            return;
                        }

                        // CHARGE TIMER
                        chargeTimer++;
                        if (chargeTimer >= maxChargeTime) {
                            // GO TO COOLDOWN
                            isTired = true;
                            walkSpeed = roamSpeed;
                            chargeTimer = 0;
                            newState(RUNNING); // Reset to roaming
                            System.out.println("Embryn is exhausted! Roaming now.");
                            return;
                        }

                        if (isPlayerCloseForAttack(player)) {
                            newState(ATTACK);
                            chargeTimer = 0;
                            return;
                        } else {
                            turnTowardsPlayer(player);
                        }
                    } else {
                        walkSpeed = roamSpeed;
                        chargeTimer = 0;
                    }

                    move(lvlData);
                }

                case DETECT -> turnTowardsPlayer(player);

                case ATTACK -> {
                    if (animationIndex >= 3 && animationIndex <= 5) {
                        checkEnemyHit(attackBox, player);
                    }
                }

                case DEAD -> {
                    if (!defeatMsgSent) {
                        Game.getInstance().getUi().setBossDefeatMsg("EMBRYN DEFEATED!");
                        defeatMsgSent = true;
                    }
                }
            }
        }
    }
}