package Entities.Passives;

import Entities.Passive;
import Entities.Player;

public class KaelthornPassive implements Passive {

    private int regenTick = 0;
    private final int REGEN_THRESHOLD = 15 * 200; // 15 seconds * 200 UPS

    public String getName() { return "Feral Mending"; }
    public String getDescription() {
        return "Every 15 seconds, Kaelthorn’s knightly resolve and lupine spirit harmonize, restoring 1 health";
    }

    @Override
    public void update(Player player) {
        if (player.getLife() < player.getMaxLife()) {
            regenTick++;

            if (regenTick >= REGEN_THRESHOLD) {
                player.changeHealth(1);
                regenTick = 0;
                System.out.println("Kaelthorn's Feral Mending triggered!");
            }
        } else {
            regenTick = 0;
        }
    }
}
