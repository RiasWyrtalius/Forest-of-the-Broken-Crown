package Entities.Boss;

import Entities.Player;
import Main.Core.Game;
import Utils.LoadSave;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static Utils.Constants.EnemyConstants.*;

public class Embryn extends Boss {

    private Rectangle2D.Float attackBox;
    private boolean attackChecked = false;

    public Embryn(float x, float y) {
        super(x, y, EMBRYN_WIDTH, EMBRYN_HEIGHT, EMBRYN);
        loadAnimations();
        initHitbox(bossData.hbWidth, bossData.hbHeight);
        initAttackBox();
    }

    protected void loadAnimations() {
        BufferedImage img = Utils.LoadSave.getSpriteAtlas(Utils.LoadSave.EMBRYN_ATLAS);

        int[] spriteAmounts = {
                bossData.spriteA_IDLE,
                bossData.spriteA_WALKRIGHT,
                bossData.spriteA_WALKLEFT,
                bossData.spriteA_DMGLEFT,
                bossData.spriteA_DMGRIGHT,
                bossData.spriteA_DIELEFT,
                bossData.spriteA_DIERIGHT
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

    public void update(int[][] lvlData, Player player) {
        updateBehavior(lvlData, player);
        int[] liveCounts = new int[5]; // Matches IDLE, RUNNING, ATTACK, HIT, DEAD

        liveCounts[IDLE] = bossData.spriteA_IDLE;
        liveCounts[RUNNING] = (walkDir == Utils.Constants.RIGHT) ? bossData.spriteA_WALKRIGHT : bossData.spriteA_WALKLEFT;
        liveCounts[ATTACK] = bossData.spriteA_IDLE;
        liveCounts[HIT] = (walkDir == Utils.Constants.RIGHT) ? bossData.spriteA_DMGRIGHT : bossData.spriteA_DMGLEFT;
        liveCounts[DEAD] = (walkDir == Utils.Constants.RIGHT) ? bossData.spriteA_DIERIGHT : bossData.spriteA_DIELEFT;

        updateAnimationTick(liveCounts);
    }

    private void initAttackBox() {
        // Wide attack box to cover Embryn's reach on both sides
        attackBox = new Rectangle2D.Float(x, y,
                (int) (100 * Game.SCALE),
                (int) (40  * Game.SCALE));
    }

    private void updateAttackBox() {
        attackBox.x = hitbox.x - (int) (25 * Game.SCALE);
        attackBox.y = hitbox.y;
    }

    private void updateBehavior(int[][] lvlData, Player player) {
        //System.out.println("Embryn logic is working! State: " + enemyState);
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        updateAttackBox();

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case IDLE -> {
                    newState(RUNNING);
                }

                case RUNNING -> {
                    if (canSeePlayer(lvlData, player)) {
                        turnTowardsPlayer(player);  // only steer when visible
                    }
                    if (isPlayerCloseForAttack(player)) {
                        System.out.println("DEBUG: Player is close! Switching to ATTACK state.");
                        newState(ATTACK);
                    } else {
                        move(lvlData);  // always keep moving
                    }
                }

                case ATTACK -> {
                    if (animationIndex == 0)
                        attackChecked = false;
                    // Hit on frame 4 of the attack animation (adjust as needed)
                    if (animationIndex == 4 && !attackChecked)
                        checkEnemyHit(attackBox, player);
                }

                case HIT -> {
                    // waits for animation to finish (handled in updateAnimationTick)
                }

                case DEAD -> {
                    // waits for animation to finish, then active = false
                }
            }
        }
    }

    public Rectangle2D.Float getAttackBox() { return attackBox; }
}
