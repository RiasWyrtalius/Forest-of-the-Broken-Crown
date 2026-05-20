package Entities.Boss;

import Entities.Player;
import Levels.Level;
import Main.Core.Game;
import Main.GameStates.Playing;
import Utils.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utils.Constants.EnemyConstants.DEAD;
import static Utils.Constants.EnemyConstants.STAR_SPAWN_ID;

public class EnemyManager {

    private Playing playing;
    private ArrayList<Boss> bosses = new ArrayList<>();
    private ArrayList<Boss> droppedLoot = new ArrayList<>(); // tracks who already dropped

    public EnemyManager(Playing playing) {
        this.playing = playing;
    }

    public void loadEnemies(Level level) {
        bosses.clear();
        Sylthra.globalStarSpawnPoints.clear();

        BufferedImage img = level.getLevelDataImage();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color c = new Color(img.getRGB(i, j));
                int green = c.getGreen();
                int blue = c.getBlue();

                // check if boss layer value
                if (HelpMethods.isBossPixel(blue)) {

                    int bossType = HelpMethods.getBossType(green);

                    if (bossType != -1) {
                        float spawnX = i * Game.TILES_SIZE;
                        float spawnY = j * Game.TILES_SIZE - (30 * Game.SCALE);
                        bosses.add(BossFactory.CreateBoss(bossType, spawnX, spawnY));
                    }

                    // if sylthra
                    if (green == STAR_SPAWN_ID) {
                        float spawnX = i * Game.TILES_SIZE;
                        float spawnY = j * Game.TILES_SIZE;
                        Sylthra.globalStarSpawnPoints.add(new Point((int)spawnX, (int)spawnY));
                    }
                }
            }
        }
    }

    public void update(int[][] lvlData, Player player) {
        for (Boss b : bosses) {
            if (b.isActive()) {
                b.update(lvlData, player);
            } else {
                // Boss just became inactive (died) — drop loot once
                if (!droppedLoot.contains(b)) {
                    droppedLoot.add(b);
                    Game.getInstance().getObjectManager().spawnPotionDrop(
                            b.getHitbox().x, b.getHitbox().y, 3, 3
                    );
                }
            }
        }

        checkPlayerStomp(player);
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Boss b : bosses) {
            b.draw(g, xLvlOffset, yLvlOffset);
            //b.drawHitbox(g, xLvlOffset, yLvlOffset);
        }
    }

    public void reset() {
        bosses.clear();
        droppedLoot.clear();
    }

    public void killAllBosses() {
        for (Boss b : bosses) {
            if (b.isActive()) {
                if (b instanceof Sylthra s) {
                    s.dealStarDamage();
                } else {
                    b.hurt(9999);
                }
            }
        }
    }

    public void checkPlayerStomp(Player player) {
        for (Boss b : bosses) {
            if (b.isActive() && b.enemyState != DEAD) {
                if (player.getHitbox().intersects(b.getHitbox())) {

                    //land on boss
                    if (player.getAirSpeed() > 0 &&
                            player.canStomp() &&
                            (player.getHitbox().y + player.getHitbox().height) < b.getHitbox().y + (b.getHitbox().height / 2)) {

                        if (!b.invincible) {
                            b.hurt(1);
                            //System.out.println("STOMPED! Boss HP: " + b.currentHealth);
                        }

                        player.triggerStompCooldown();
                        player.setAirSpeed(player.getJumpSpeed());
                        player.startAirborne();

                    } else {

                        if (player.getAirSpeed() < 0 && player.getHitbox().y < b.getHitbox().y) {
                            return;
                        }

                        player.changeHealth(-b.getEnemyDamage());
                    }
                }
            }
        }
    }

    public boolean isBossTypeDefeated(int bossType) {
        boolean found = false;
        for (Boss b : bosses) {
            if (b.getEnemyType() == bossType) {
                found = true;
                if (b.isActive() && b.enemyState != DEAD) return false;
            }
        }
        return found; // returns false if no boss of that type was ever loaded
    }
}
