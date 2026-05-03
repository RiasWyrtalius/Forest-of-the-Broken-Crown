package Entities.Boss;

import Entities.Player;
import Main.Core.Game;
import Utils.LoadSave;

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

    public Embryn(float x, float y) {
        super(x, y, EMBRYN_WIDTH, EMBRYN_HEIGHT, EMBRYN);
        loadAnimations();
        initHitbox(bossData.hbWidth, bossData.hbHeight);
        initAttackBox();
        this.attackDistance = 0.8f * Game.TILES_SIZE;
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.draw(g, xLvlOffset, yLvlOffset);

        if (attackBox != null) {
            g.setColor(Color.BLUE);
            g.drawRect((int) attackBox.x - xLvlOffset,
                    (int) attackBox.y - yLvlOffset,
                    (int) attackBox.width,
                    (int) attackBox.height);
        }
    }

    protected void loadAnimations() {
        BufferedImage img = Utils.LoadSave.getSpriteAtlas(Utils.LoadSave.EMBRYN_ATLAS);

        int[] spriteAmounts = {
                bossData.spriteA_IDLE,          // Row 0
                bossData.spriteA_WALKLEFT,      // Row 1
                bossData.spriteA_WALKRIGHT,     // Row 2
                bossData.spriteA_DMGLEFT,       // Row 3
                bossData.spriteA_DMGRIGHT,      // Row 4
                bossData.spriteA_DIELEFT,       // Row 5
                bossData.spriteA_DIERIGHT,      // Row 6
                bossData.spriteA_DETECTLEFT,    // Row 7
                bossData.spriteA_DETECTRIGHT,   // Row 8
                bossData.spriteA_RUNLEFT,       // Row 9
                bossData.spriteA_RUNRIGHT       // Row 10
        };

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

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y,
                (int) (100 * Game.SCALE),
                (int) (40 * Game.SCALE));
    }

    public void update(int[][] lvlData, Player player) {
        updateHealthStatus();
        updateBehavior(lvlData, player);
        int[] liveCounts = new int[7];

        liveCounts[IDLE] = bossData.spriteA_IDLE;
        liveCounts[RUNNING] = (walkDir == RIGHT) ? bossData.spriteA_RUNRIGHT : bossData.spriteA_RUNLEFT;
        liveCounts[ATTACK] = (walkDir == RIGHT) ? bossData.spriteA_WALKRIGHT : bossData.spriteA_WALKLEFT;
        liveCounts[HIT] = (walkDir == RIGHT) ? bossData.spriteA_DMGRIGHT : bossData.spriteA_DMGLEFT;
        liveCounts[DEAD] = (walkDir == RIGHT) ? bossData.spriteA_DIERIGHT : bossData.spriteA_DIELEFT;
        liveCounts[DETECT] = (walkDir == RIGHT) ? bossData.spriteA_DETECTRIGHT : bossData.spriteA_DETECTLEFT;

        updateAnimationTick(liveCounts);
    }

    private void updateAttackBox() {
        if (walkDir == RIGHT)
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * -40f);
        else
            attackBox.x = hitbox.x - attackBox.width - (int) (Game.SCALE * -40f);

        attackBox.y = hitbox.y + (Game.SCALE * 40f);
    }

    private void updateBehavior(int[][] lvlData, Player player) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        updateAttackBox();

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
                case ATTACK -> checkEnemyHit(attackBox, player);
                case DEAD -> {
                    if (!defeatMsgSent) {
                        Game.getInstance().getUi().setBossDefeatMsg("EMBRYN DEFEATED");
                        defeatMsgSent = true;
                    }
                }
            }
        }
    }
}
