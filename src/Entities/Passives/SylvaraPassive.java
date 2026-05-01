package Entities.Passives;

import Entities.Passive;
import Entities.Player;

public class SylvaraPassive implements Passive {

    public String getName() { return "Sylph’s Descent"; }
    public String getDescription() {
        return "Sylvara naturally catches the wind, falling slowly whenever she is in the air.";
    }

    @Override
    public void update(Player player) {
        if (player.isInAir()) {
            if (!player.isDown()) {
                //GLIDE
                if (player.getAirSpeed() > 0.5f) {
                    player.setAirSpeed(0.5f);
                }
            }
        }
    }
}
