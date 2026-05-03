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

public class EnemyManager {

    private Playing playing;
    private ArrayList<Boss> bosses = new ArrayList<>();

    public EnemyManager(Playing playing) {
        this.playing = playing;
    }

    public void loadEnemies(Level level) {
        bosses.clear();
        BufferedImage img = level.getLevelDataImage();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color c = new Color(img.getRGB(i, j));
                if (HelpMethods.isBossPixel(c.getBlue())) {
                    int bossType = HelpMethods.getBossType(c.getGreen());
                    //System.out.println("Found Boss Pixel! Green: " + c.getGreen());
                    if (bossType != -1) {
                        float spawnX = i * Game.TILES_SIZE;
                        float spawnY = j * Game.TILES_SIZE - (30 * Game.SCALE);

                        bosses.add(BossFactory.CreateBoss(bossType, spawnX, spawnY));
                    }
                }
            }
        }
    }

    public void update(int[][] lvlData, Player player) {
        for (Boss b : bosses) {
            if (b.isActive()) {
                b.update(lvlData, player);
            }
        }

        checkPlayerStomp(player);
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Boss b : bosses) {
            b.draw(g, xLvlOffset, yLvlOffset);
            b.drawHitbox(g, xLvlOffset, yLvlOffset);
        }
    }

    public void reset() {
        bosses.clear();
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
                            System.out.println("STOMPED! Boss HP: " + b.currentHealth);
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
}
