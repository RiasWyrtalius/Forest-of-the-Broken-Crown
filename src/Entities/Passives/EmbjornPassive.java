package Entities.Passives;

import Entities.Passive;
import Entities.Player;
import Main.Core.Game;

public class EmbjornPassive implements Passive {

    public String getName() { return "Slither Step"; }
    public String getDescription() {
        return "Embjorn moves slightly faster on narrow platforms, giving him an edge in tight spaces.";
    }

    @Override
    public void update(Player player) {
        float baseSpeed = 1.0f * Game.SCALE * player.getCharacterData().speedMultiplier;
        player.setWalkSpeed(baseSpeed * 2.0f);
    }
}
