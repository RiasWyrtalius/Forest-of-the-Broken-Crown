package Entities.Passives;

import Entities.Passive;

public class KaelthornPassive implements Passive {
    public String getName() { return "Feral Mending"; }
    public String getDescription() {
        return "Every 15 seconds, Kaelthorn’s knightly resolve and lupine spirit harmonize, restoring 1 health";
    }
}
