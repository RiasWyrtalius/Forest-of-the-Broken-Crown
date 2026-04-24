package Entities.Passives;

import Entities.Passive;

public class SylvaraPassive implements Passive {

    public String getName() { return "Flap and Float"; }
    public String getDescription() {
        return "Her wings grant her a graceful double jump and slower descent, allowing her to glide through collapsing platforms.";
    }

    @Override public int getMaxJumps() { return 2; }
}
