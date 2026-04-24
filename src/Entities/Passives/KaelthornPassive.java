package Entities.Passives;

import Entities.Passive;

public class KaelthornPassive implements Passive {
    public String getName() { return "Steady Step"; }
    public String getDescription() {
        return "Kaelthorn’s movement is slightly slower, but his jumps are more controlled, making him easier to land safely on platforms.";
    }
}
