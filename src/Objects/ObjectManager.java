package Objects;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Entities.Player;
import Main.Core.Game;
import Utils.LoadSave;

import static Utils.Constants.PlayerConstants.ObjectConstants.*;


public class ObjectManager {

    private ArrayList<Vase> vases;
    private ArrayList<Spike> spikes;
    private BufferedImage[] spikeImgs;

    public ObjectManager() {
        vases = new ArrayList<>();
        spikes = new ArrayList<>();
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
    }

    public void resetAllObjects() {
        for (Vase v : vases) {
            v.reset();
        }
    }

    public void loadObjects(Levels.Level level) {
        vases.clear();
        spikes.clear();

        BufferedImage img = level.getLevelDataImg();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                int spikeIndex = color.getGreen();

                if (value == VASE_COLOR) {
                    vases.add(new Vase(i * Game.TILES_SIZE, j * Game.TILES_SIZE, VASE));
                } else if (value == SPIKE_COLOR) {
                    spikes.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, SPIKE, spikeImgs, spikeIndex));
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
    }
}
