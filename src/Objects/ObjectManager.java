package Objects;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


import Audio.AudioPlayer;
import Entities.NPC;
import Entities.Player;
import Levels.Level;
import Main.Core.Game;
import Utils.Constants;
import Utils.LoadSave;

import static Utils.Constants.NPCConstants.*;
import static Utils.Constants.ObjectConstants.*;


public class ObjectManager {

    private ArrayList<Vase> vases;
    private ArrayList<Spike> spikes;
    private ArrayList<NPC> npcs;
    private BufferedImage[] spikeImgs;
    private CopyOnWriteArrayList<Potion> potions;
    private ArrayList<CrumblingTile> crumblingTiles;
    private ArrayList<Platform> platforms;
    private Game game;

    public ObjectManager(Game game) {
        this.game = game;
        vases = new ArrayList<>();
        spikes = new ArrayList<>();
        npcs = new ArrayList<>();
        potions = new CopyOnWriteArrayList<>();
        crumblingTiles = new ArrayList<>();
        platforms = new ArrayList<>();
        loadSpikeImgs();
    }

    public void update(Player player) {
        for (Vase v : vases) {
            if (v.isActive() || v.isBreaking()) {
                v.update();
                checkPlayerObjectInteraction(player, v);
            }
        }

        for (NPC npc : npcs) {
            npc.update();

            float playerX = player.getHitbox().x + (player.getHitbox().width / 2);
            float npcX = npc.getHitbox().x + (npc.getHitbox().width / 2);

            // interaction range
            npc.setHovered(Math.abs(playerX - npcX) < 100);
        }

        for (Potion p : potions) {
            if (p.isActive()) {
                p.update();
            }
        }

        int[][] lvlData = game.getLevelHandler().getCurrentLevel().getLevelData();

        for (CrumblingTile ct : crumblingTiles) {
            ct.update(lvlData);
        }

        checkPlayerStandingOnCrumblingTile(player);
        checkPlayerPickup(player);
    }

    public void resetAllObjects() {
        for (Vase v : vases) {
            v.reset();
        }

        int[][] lvlData = game.getLevelHandler().getCurrentLevel().getLevelData();
        for (CrumblingTile ct : crumblingTiles) {
            ct.forceRestore(lvlData);
        }

        // Simplest safe approach: reload objects fresh from the level image
        loadObjects(game.getLevelHandler().getCurrentLevel());
    }

    // Checks if the player is standing on top of a crumbling tile and triggers it
    private void checkPlayerStandingOnCrumblingTile(Player player) {
        float playerBottom = player.getHitbox().y + player.getHitbox().height;
        float playerLeft   = player.getHitbox().x;
        float playerRight  = player.getHitbox().x + player.getHitbox().width;

        for (CrumblingTile ct : crumblingTiles) {
            if (ct.isGone()) continue;

            float tileTop   = ct.getHitbox().y;
            float tileLeft  = ct.getHitbox().x;
            float tileRight = ct.getHitbox().x + ct.getHitbox().width;

            // Player is standing on top: feet are just at tile surface, and horizontally overlapping
            boolean feetOnTile = Math.abs(playerBottom - tileTop) <= (8 * Game.SCALE);
            boolean horizontalOverlap = playerRight > tileLeft && playerLeft < tileRight;

            // Only trigger if the player is not falling (airSpeed >= 0 means grounded or falling)
            boolean isGrounded = player.getAirSpeed() >= 0 && !player.isInAir();

            if (feetOnTile && horizontalOverlap && isGrounded) {
                ct.onPlayerStanding();
            }
        }
    }

    public void loadObjects(Level level) {
        vases.clear();
        potions.clear();
        spikes.clear();
        npcs.clear();
        crumblingTiles.clear();
        platforms.clear();

        BufferedImage img = level.getLevelDataImg();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                int npcID = color.getGreen();
                int redValue = color.getRed();

                if (value == VASE_COLOR) {
                    vases.add(new Vase(i * Game.TILES_SIZE, j * Game.TILES_SIZE, VASE));
                } else if (value == CRUMBLING_TILE_COLOR) {
                    crumblingTiles.add(new CrumblingTile(i * Game.TILES_SIZE, j * Game.TILES_SIZE, color.getGreen()));
                } else if (value == SPIKE_COLOR) {
                    int spikeIndex = color.getGreen();
                    spikes.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, spikeIndex, spikeImgs, spikeIndex));
                } else if (value == NINO_TQ ||
                        value == CHAD_TB ||
                        value == CHARLZ_TS ||
                        value == RILEY_TZ ||
                        value == DENVER_TC) {
                    //System.out.println("NPC Spawned at: " + i + ", " + j);
                    String[] lines = Utils.DialogueData.getLinesFor(npcID);
                    boolean canSave = (redValue == 1);

                    int centeredX = (i * Game.TILES_SIZE) - 22;
                    int groundedY = (j * Game.TILES_SIZE) - 32;

                    npcs.add(new NPC(centeredX, groundedY, 64, 64, npcID, lines, canSave));
                } else if (value == PLATFORM_COLOR) {
                    int spriteIndex = color.getGreen();
                    platforms.add(new Platform(i * Game.TILES_SIZE, j * Game.TILES_SIZE, PLATFORM_COLOR, spriteIndex));
                }
            }
        }
    }

    private void loadSpikeImgs() {
        BufferedImage temp = LoadSave.getSpriteAtlas(LoadSave.SPIKE_ATLAS);
        spikeImgs = new BufferedImage[8];

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 2; i++) {
                spikeImgs[j * 2 + i] = temp.getSubimage(
                        i * Game.TILES_DEFAULT_SIZE,
                        j * Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE,
                        Game.TILES_DEFAULT_SIZE
                );
            }
        }
    }

    private void checkPlayerObjectInteraction(Player player, Vase v) {

        if (v.isBreaking() || !v.isActive()) return;

        Rectangle2D.Float pBox = player.getHitbox();
        if (pBox.intersects(v.getHitbox())) {

            //stompy stompy
            float playerBottom = pBox.y + pBox.height;
            float vaseTop = v.getHitbox().y;

            if (player.getAirSpeed() > 0 && playerBottom < vaseTop + (v.getHitbox().height / 2)) {
                if (!v.isBreaking()) {
                    v.setBreaking(true);
                    player.setAirSpeed(player.getJumpSpeed() * 0.6f);
                    generateDrop(v);
                    game.getAudioPlayer().playEffect(AudioPlayer.VASE_BREAK);
                }
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Vase v : vases) {
            if (v.isActive()) {
                v.draw(g, xLvlOffset, yLvlOffset);
            }
        }

        for (Spike s : spikes) {
            s.draw(g, xLvlOffset, yLvlOffset);
        }

        for (NPC npc : npcs) {
            npc.draw(g, xLvlOffset, yLvlOffset);
        }

        for (Potion p : potions) {
            if (p.isActive()) {
                p.draw(g, xLvlOffset, yLvlOffset);
            }
        }

        for (CrumblingTile ct : crumblingTiles) {
            ct.draw(g, xLvlOffset, yLvlOffset);
        }

        for (Platform p : platforms) {
            p.draw(g, xLvlOffset, yLvlOffset);
        }
    }

    private void generateDrop(Vase v) {
        int dropRoll = (int) (Math.random() * 100);

        if (dropRoll < Constants.ObjectConstants.POTION_DROP_CHANCE) {

            int typeRoll = (int) (Math.random() * 100);
            int potionType;

            if (typeRoll < Constants.ObjectConstants.HEALTH_CHANCE) {
                potionType = HEALTH_POTION;
            } else {
                potionType = MANA_POTION;
            }

            float spawnX = v.getHitbox().x;
            float spawnY = v.getHitbox().y - (20 * Game.SCALE);

            potions.add(new Potion((int)spawnX, (int)spawnY, potionType));

            // Debug Check
            //System.out.println("Drop successful! Type: " + (potionType == 0 ? "Health" : "Mana"));
        }
    }

    public void checkSpikesTouched(Player p) {
        for (Spike s : spikes) {
            if (s.getHitbox().intersects(p.getHitbox())) {
                switch (s.getSpriteIndex()) {
                    case SPIKE_FLOOR_MID, SPIKE_FLOOR_LEFT, SPIKE_FLOOR_RIGHT, SPIKE_LEFT, SPIKE_RIGHT -> {
                        p.teleportToSpawn();
                        p.loseLife();
                    }
                    default -> {
                        p.changeHealth(-1); //ceiling/wall spikes
                    }
                }
            }
        }
    }

    public void checkPlayerPickup(Player p) {
        for (Potion pot : potions) {
            if (pot.isActive()) {
                if (pot.canBePickedUp()) {
                    if (pot.getHitbox().intersects(p.getHitbox())) {
                        applyEffect(pot, p);
                        pot.setActive(false);
                    }
                }
            }
        }
    }

    private void applyEffect(Potion pot, Player p) {
        if (pot.getObjType() == HEALTH_POTION) {
            p.changeHealth(Constants.ObjectConstants.HEAL_POTION_VALUE);
        } else {
            p.gainMana(Constants.ObjectConstants.MANA_POTION_VALUE);
        }
        game.getAudioPlayer().playEffect(AudioPlayer.CONSUME_POTION);
    }

    // Called when an enemy dies to drop potions at its location
    public void spawnPotionDrop(float x, float y, int hpCount, int manaCount) {
        for (int i = 0; i < hpCount; i++) {
            float spawnX = x + (i * Game.TILES_SIZE * 0.6f);
            float spawnY = y - (20 * Game.SCALE);
            potions.add(new Potion((int) spawnX, (int) spawnY, Constants.ObjectConstants.HEALTH_POTION));
        }
        for (int i = 0; i < manaCount; i++) {
            float spawnX = x + ((hpCount + i) * Game.TILES_SIZE * 0.6f);
            float spawnY = y - (20 * Game.SCALE);
            potions.add(new Potion((int) spawnX, (int) spawnY, Constants.ObjectConstants.MANA_POTION));
        }
    }

    public NPC getHoveredNPC() {
        for (NPC npc : npcs) {
            float playerX = game.getPlayer().getHitbox().x + (game.getPlayer().getHitbox().width / 2);
            float npcX = npc.getHitbox().x + (npc.getHitbox().width / 2);

            if (Math.abs(playerX - npcX) < 100) {
                return npc;
            }
        }
        return null;
    }

    public ArrayList<Platform> getPlatforms() { return platforms; }
}
