package Entities.Boss;

import Entities.Player;
import Main.Core.Game;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.RIGHT;

public abstract class Boss extends Enemy {
    protected BufferedImage[][] animations;
    protected BossData bossData;

    public Boss(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height, enemyType);
        this.bossData = BossData.getByOrder(enemyType);
        initHealth();
        initHitbox(bossData.hbWidth, bossData.hbHeight);
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (active) {
            int row = getCorrectRow();

            g.drawImage(animations[row][animationIndex],
                    (int) (hitbox.x - xLvlOffset - (bossData.drawOffX * Game.SCALE)),
                    (int) (hitbox.y - yLvlOffset - (bossData.drawOffY * Game.SCALE)),
                    width, height, null);
        }
    }

    private int getCorrectRow() {
        return switch (enemyState) {
            case ATTACK -> (walkDir == RIGHT) ? bossData.rowWALKRIGHT : bossData.rowWALKLEFT;
            case RUNNING -> (walkDir == RIGHT) ? bossData.rowRUNRIGHT : bossData.rowRUNLEFT;
            case DETECT -> (walkDir == RIGHT) ? bossData.rowDETECTRIGHT : bossData.rowDETECTLEFT;
            case HIT -> (walkDir == RIGHT) ? bossData.rowDMGRIGHT : bossData.rowDMGLEFT;
            case DEAD -> (walkDir == RIGHT) ? bossData.rowDIERIGHT : bossData.rowDIELEFT;
            default -> bossData.rowIDLE;
        };
    }

    public abstract void update(int[][] lvlData, Player player);

    @Override
    protected int getEnemyDamage() { return bossData.damage; }

    @Override
    protected int getEnemyMaxHealth() {
        return bossData.getHealth();
    }
}