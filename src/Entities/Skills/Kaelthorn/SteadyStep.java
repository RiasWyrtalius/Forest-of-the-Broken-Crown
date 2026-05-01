package Entities.Skills.Kaelthorn;

import Entities.Skills.Skill;

import java.awt.*;

import Entities.Player;

public class SteadyStep implements Skill {
    private Player player;
    private boolean charging = false;
    private float chargeAmount = 0f;
    private final float MAX_CHARGE = 3f; // Cap so he doesn't go to the skies
    private final float CHARGE_SPEED = 0.05f;
    private final int MANA_COST = 2;

    public SteadyStep(Player player) {
        this.player = player;
    }

    @Override
    public void update() {
        if (charging && player.getManaBottles() >= MANA_COST) {
            if (chargeAmount < MAX_CHARGE) {
                chargeAmount += CHARGE_SPEED;
            }
        }
    }

    @Override
    public void activate() {
        if (player.getManaBottles() >= MANA_COST) {
            charging = true;
        }
    }

    @Override
    public void deactivate() {
        if (charging) {
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
