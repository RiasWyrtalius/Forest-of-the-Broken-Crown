package Entities.Skills.Sylvara;

import Audio.AudioPlayer;
import Entities.Player;
import Entities.Skills.Skill;
import Main.Core.Game;
import Main.GameState;

import java.awt.*;

import static Utils.Constants.PlayerConstants.DOUBLEJUMP;

public class ZephyrBound implements Skill {
    private Player player;
    private final int MANA_COST = 4;
    private boolean canDoubleJump = true;

    public ZephyrBound(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "Zephyr Bound"; }
    @Override public String getSkillDescription() {
        return "Harness a burst of magical wind to kick off the air, allowing for a second jump mid-flight.";
    }

    @Override
    public void update() {
        if (GameState.state != GameState.PLAYING) return;
        if (!player.isInAir()) {
            canDoubleJump = true;
        }
    }

    @Override
    public void activate() {
        if (GameState.state != GameState.PLAYING) return;
        if (player.isInAir() && canDoubleJump && player.getManaBottles() >= MANA_COST) {
            Game.getInstance().getAudioPlayer().playEffect(AudioPlayer.SYL_SKILL);
            player.setAirSpeed(player.getJumpSpeed() * 0.8f);
            player.gainMana(-MANA_COST);
            player.setPlayerAction(DOUBLEJUMP);
            canDoubleJump = false;
        }
    }

    @Override public void deactivate() {}
    @Override public void render(Graphics g, int lvlOffset, int yLvlOffset) {}
}
