package Entities.Boss;

import Entities.Player;
import Main.Core.Game;
import Utils.LoadSave;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import static Utils.Constants.EnemyConstants.*;

public class Sylthra extends Boss {

    private int summonTick = 0;
    private int projectileTick = 0;
    private Kaelor summonedKaelor = null;

    private ArrayList<Star> activeStars = new ArrayList<>();
    private BufferedImage[] starImgs;
    private BufferedImage[][] projectileImgs;
    private ArrayList<SylthraProjectile> projectiles = new ArrayList<>();

    private int starsCollected = 0;
    public static ArrayList<Point> globalStarSpawnPoints = new ArrayList<>(); // Populated by EnemyManager

    public Sylthra(float x, float y) {
        super(x, y, SYLTHRA_WIDTH, SYLTHRA_HEIGHT, SYLTHRA);

        this.maxHealth = BossData.SYLTHRA.getHealth();
        this.currentHealth = maxHealth;
        loadAnimations();
        enemyState = IDLE;
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.SYLTHRA_ATLAS);
        animations = new BufferedImage[5][17]; // Max 17 frames on death row
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < getRowLength(j); i++)
                animations[j][i] = img.getSubimage(i * SYLTHRA_WIDTH_DEFAULT, j * SYLTHRA_WIDTH_DEFAULT, SYLTHRA_WIDTH_DEFAULT, SYLTHRA_HEIGHT_DEFAULT );

        // Load Stars
        BufferedImage sImg = LoadSave.getSpriteAtlas(LoadSave.STARS_ATLAS);
        starImgs = new BufferedImage[6];
        for (int i = 0; i < 6; i++) starImgs[i] = sImg.getSubimage(i * 64, 0, 64, 64);

        // Load Projectiles
        BufferedImage pImg = LoadSave.getSpriteAtlas(LoadSave.SYLTHRA_ATK_ATLAS);
        projectileImgs = new BufferedImage[2][3];
        for (int j = 0; j < 2; j++)
            for (int i = 0; i < 3; i++)
                projectileImgs[j][i] = pImg.getSubimage(i * 64, j * 64, 64, 64);
    }

    private int getRowLength(int row) {
        return switch(row) {
            case 0->7;
            case 1->6;
            case 2->5;
            case 3->3;
            case 4->17;
            default->1;
        };
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        if (!active) return;
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
            spawnStars();
        }
        if (inAir) updateInAir(lvlData);

        // 1. Handle Kaelor Summoning Timer (2000 ticks = 10 sec)
        if (summonedKaelor != null && summonedKaelor.isActive()) {
            summonedKaelor.update(lvlData, player); // Pause timer while Kaelor is alive
        } else {
            summonTick++;
            if (summonTick >= 2000 && enemyState == IDLE) {
                newState(PRE_SUMMON);
                summonTick = 0;
            }
        }

        // 2. Handle Projectiles (2000 ticks = 10 sec)
        projectileTick++;
        if (projectileTick >= 2000 && enemyState == IDLE) {
            launchProjectiles(player);
            projectileTick = 0;
        }
        for (int i = 0; i < projectiles.size(); i++) {
            SylthraProjectile p = projectiles.get(i);
            p.update(player);
            if (!p.isActive()) projectiles.remove(i);
        }

        // 3. Handle Stars
        for (Star s : activeStars) {
            s.update();
            if (!s.collected && player.getHitbox().intersects(s.hitbox)) {
                s.collected = true;
                starsCollected++;

                // When the 3rd star is grabbed...
                if (starsCollected >= 3) {

                    // 1. DEDUCT BOSS HP!
                    currentHealth -= 1;

                    // 2. Play the Hit animation (or Die if HP is 0)
                    if (currentHealth <= 0) {
                        newState(DEAD);
                    } else {
                        newState(HIT);
                    }

                    // 3. Reset the wave
                    spawnStars();
                    break; // Stop the loop so we don't crash
                }
            }
        }

        updateAnimationTick();
    }

    // --- OVERRIDE HURT TO MAKE HIM IMMUNE WITHOUT STARS ---
    @Override public void hurt(int amount) {super.hurt(amount);}

    private void spawnStars() {
        activeStars.clear();
        if (globalStarSpawnPoints.isEmpty()) return;

        // Shuffle the spawn points and pick the first 3
        Collections.shuffle(globalStarSpawnPoints);
        int spawnCount = Math.min(3, globalStarSpawnPoints.size());

        for (int i = 0; i < spawnCount; i++) {
            Point p = globalStarSpawnPoints.get(i);
            activeStars.add(new Star(p.x, p.y, starImgs));
        }
    }

    private void launchProjectiles(Player player) {
        projectiles.add(new SylthraProjectile(hitbox.x, hitbox.y + 20, projectileImgs));
        projectiles.add(new SylthraProjectile(hitbox.x, hitbox.y + 60, projectileImgs));
        projectiles.add(new SylthraProjectile(hitbox.x, hitbox.y + 100, projectileImgs));
    }

    // Custom Animation loop to chain PRE_SUMMON -> SUMMON -> Kaelor Spawn
    @Override
    protected void updateAnimationTick() {
        animationTick++;
        if (animationTick >= ANI_SPEED) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= getSpriteAmount()) {
                animationIndex = 0;
                switch (enemyState) {
                    case PRE_SUMMON -> newState(SUMMON);
                    case SUMMON -> {
                        summonedKaelor = new Kaelor(hitbox.x, hitbox.y); // Spawns Kaelor
                        newState(IDLE);
                    }
                    case HIT -> newState(IDLE);
                    case DEAD -> active = false;
                }
            }
        }
    }

    @Override
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Star s : activeStars) s.draw(g, xLvlOffset, yLvlOffset);
        for (SylthraProjectile p : projectiles) p.draw(g, xLvlOffset, yLvlOffset);
        if (summonedKaelor != null && summonedKaelor.isActive()) summonedKaelor.draw(g, xLvlOffset, yLvlOffset);

        super.draw(g, xLvlOffset, yLvlOffset);

        //drawHitbox(g, xLvlOffset, yLvlOffset);
    }

    @Override protected int getAnimationRow() {
        return switch(enemyState) { case IDLE->0; case PRE_SUMMON->1; case SUMMON->2; case HIT->3; case DEAD->4; default->0; };
    }

    @Override public int getSpriteAmount() {
        return switch(enemyState) { case IDLE->7; case PRE_SUMMON->6; case SUMMON->5; case HIT->3; case DEAD->17; default->7; };
    }
}