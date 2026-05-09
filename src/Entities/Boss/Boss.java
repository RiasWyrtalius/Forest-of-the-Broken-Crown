package Entities.Boss;

import Entities.Player;
import Main.Core.Game;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import static Utils.Constants.EnemyConstants.*;

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
            int row = getAnimationRow();

            g.drawImage(animations[row][animationIndex],
                    (int) (hitbox.x - xLvlOffset - (bossData.drawOffX * Game.SCALE)),
                    (int) (hitbox.y - yLvlOffset - (bossData.drawOffY * Game.SCALE)),
                    width, height, null);
        }
    }

    protected void updateAnimationTick() {
        animationTick++;
        if (animationTick >= ANI_SPEED) {
            animationTick = 0;
            animationIndex++;

            // This safely calls the getSpriteAmount() method from Embryn or Kaelor!
            if (animationIndex >= getSpriteAmount()) {
                animationIndex = 0;

                switch (enemyState) {
                    case DETECT -> newState(RUNNING);
                    case ATTACK, HIT -> newState(IDLE);
                    case DEAD -> active = false;
                }
            }
        }
    }

    public abstract void update(int[][] lvlData, Player player);
    protected abstract int getAnimationRow();
    public abstract int getSpriteAmount();
    public int getEnemyType() { return enemyType; }
    @Override protected int getEnemyDamage() { return bossData.damage; }
    @Override protected int getEnemyMaxHealth() { return bossData.getHealth(); }
}