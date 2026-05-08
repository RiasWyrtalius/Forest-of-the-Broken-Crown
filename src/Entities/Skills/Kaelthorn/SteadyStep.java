package Entities.Skills.Kaelthorn;

import Audio.AudioPlayer;
import Entities.Skills.Skill;

import java.awt.*;

import Entities.Player;
import Main.Core.Game;
import Main.GameState;

public class SteadyStep implements Skill {
    private Player player;
    private boolean charging = false;
    private float chargeAmount = 0f;
    private final float MAX_CHARGE = 3f; // Cap so he doesn't go to the skies
    private final float CHARGE_SPEED = 0.05f;
    private final int MANA_COST = 1;

    public SteadyStep(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "Steady Step"; }
    @Override public String getSkillDescription() {
        return "Hold to charge Kaelthorn’s strength. Release to leap upward; height increases with charge time.";
    }

    @Override
    public void update() {
        if (GameState.state != GameState.PLAYING) {
            charging = false;
            chargeAmount = 0;
            return;
        }

        if (charging && player.getManaBottles() >= MANA_COST) {
            if (chargeAmount < MAX_CHARGE) {
                chargeAmount += CHARGE_SPEED;
            }
        }
    }

    @Override
    public void activate() {
        if (GameState.state != GameState.PLAYING) return;
        if (player.getManaBottles() >= MANA_COST) {
            charging = true;
        }
    }

    @Override
    public void deactivate() {
        if (charging) {
            Game.getInstance().getAudioPlayer().playEffect(AudioPlayer.KAEL_SKILL);
            float totalJumpForce = player.getJumpSpeed() - chargeAmount;
            player.setAirSpeed(totalJumpForce);
            player.startAirborne();
            player.gainMana(-MANA_COST);
            charging = false;
            chargeAmount = 0;
        }
    }

    @Override
    public void render(Graphics g, int lvlOffset, int yLvlOffset) {
        if (charging) {
            int barMaxWidth = 40;
            float progress = chargeAmount / MAX_CHARGE;
            int currentWidth = (int) (progress * barMaxWidth);

            int x = (int) (player.getHitbox().x - lvlOffset);
            int y = (int) (player.getHitbox().y - yLvlOffset) - 15;

            //LIMIT
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, barMaxWidth, 6);

            //CHARGE
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, currentWidth, 6);

            //OUTLINE
            g.setColor(Color.WHITE);
            g.drawRect(x, y, barMaxWidth, 6);
        }
    }
}
