package Entities.Skills.Embjorn;

import Entities.Player;
import Entities.Skills.Skill;

import java.awt.*;

import static Utils.Constants.PlayerConstants.JUMP;
import static Utils.Constants.PlayerConstants.WALKL;

public class VenomDash implements Skill {
    private Player player;
    private final int MANA_COST = 1;

    private final float FLING_FORCE = 12.0f; // High initial horizontal burst
    private final float UPWARD_BOOST = -3.5f; //the upward lift
    private final int DASH_DURATION = 15;

    private int dashTick = 0;
    private float dashDirection = 0;
    private boolean isDashing = false;
    private boolean canDash = true;

    public VenomDash(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "Venom Dash";}
    @Override public String getSkillDescription() { return "A quick horizontal dash to slip past hazards."; }

    @Override
    public void render(Graphics g, int lvlOffset, int yLvlOffset) {
        if (!player.isInAir()) {
            canDash = true;
        }
    }

    @Override
    public void activate() {
        if (canDash && player.getManaBottles() >= MANA_COST) {
            float dir = 0;
            if (player.isLeft()) {
                dir = -1f;
            } else if (player.isRight()) {
                dir = 1f;
            } else {
                if (player.getFaceDirection() == WALKL) {
                    dir = -1f;
                } else {
                    dir = 1f;
                }
            }

            player.fling(dir * FLING_FORCE);
            player.setAirSpeed(UPWARD_BOOST);
            player.startAirborne();

            player.gainMana(-MANA_COST);
            player.setPlayerAction(JUMP);

            canDash = false;
        }
    }

    @Override public void update() {
        if (!player.isInAir()) {
            canDash = true;
        }

        if (isDashing) {
            if (dashTick < DASH_DURATION) {
                player.getHitbox().x += (dashDirection * FLING_FORCE);
                dashTick++;
            } else {
                stopDash();
            }
        }
    }

    private void stopDash() {
        isDashing = false;
        dashTick = 0;
    }

    @Override public void deactivate() {}
}
