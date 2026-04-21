package Objects;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Entities.NPC;
import Entities.Player;
import Main.Core.Game;
import Utils.LoadSave;

import static Utils.Constants.NPCConstants.NINO_TQ;
import static Utils.Constants.ObjectConstants.*;


public class ObjectManager {

    private ArrayList<Vase> vases;
    private ArrayList<Spike> spikes;
    private ArrayList<NPC> npcs;
    private BufferedImage[] spikeImgs;
    private Game game;

    public ObjectManager(Game game) {
        this.game = game;
        vases = new ArrayList<>();
        spikes = new ArrayList<>();
        npcs = new ArrayList<>();
        loadSpikeImgs();
    }

    public void update(Player player) {
        for (Vase v : vases) {
            if (v.isActive() || v.isBreaking()) {
                v.update();
                checkPlayerObjectInteraction(player, v);
            }
        }

        for (Spike s : spikes) {
            if (s.getHitbox().intersects(player.getHitbox())) {
                player.loseLife();
            }
        }

        for (NPC npc : npcs) {
            npc.update();

            float playerX = player.getHitbox().x + (player.getHitbox().width / 2);
            float npcX = npc.getHitbox().x + (npc.getHitbox().width / 2);

            if (Math.abs(playerX - npcX) < 100) { // interaction range
                npc.setHovered(true);
            } else {
                npc.setHovered(false);
            }
        }
    }

    public void resetAllObjects() {
        for (Vase v : vases) {
            v.reset();
        }
    }

    public void loadObjects(Levels.Level level) {
        vases.clear();
        spikes.clear();
        npcs.clear();

        BufferedImage img = level.getLevelDataImg();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                int npcID = color.getGreen();

                if (value == VASE_COLOR) {
                    vases.add(new Vase(i * Game.TILES_SIZE, j * Game.TILES_SIZE, VASE));
                } else if (value == SPIKE_COLOR) {
                    int spikeIndex = color.getGreen();
                    spikes.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, SPIKE, spikeImgs, spikeIndex));
                } else if (value == NINO_TQ) {
                    //System.out.println("NPC Spawned at: " + i + ", " + j);
                    String[] lines = Utils.DialogueData.getLinesFor(npcID);

                    int centeredX = (i * Game.TILES_SIZE) - 22;
                    int groundedY = (j * Game.TILES_SIZE) - 32;

                    npcs.add(new NPC(centeredX, groundedY, 64, 64, npcID, lines));
                }
            }
        }
    }

    private void loadSpikeImgs() {
        BufferedImage temp = LoadSave.getSpriteAtlas(LoadSave.SPIKE_ATLAS);
        spikeImgs = new BufferedImage[6];

        for (int j = 0; j < 3; j++) {
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
                v.setBreaking(true);
                player.setAirSpeed(player.getJumpSpeed() * 0.6f); // Bounce effect

                //TODO: implement breaking audio here.
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        for (Vase v : vases) {
            if (v.isActive()) {
                v.draw(g, xLvlOffset);
            }
        }

        for (Spike s : spikes) {
            s.draw(g, xLvlOffset);
        }

        for (NPC npc : npcs) {
            npc.draw(g, xLvlOffset);
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
}
