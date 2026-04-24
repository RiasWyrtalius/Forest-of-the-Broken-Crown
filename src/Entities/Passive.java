package Entities;

public interface Passive {

    String getName();
    String getDescription();

    default int getMaxJumps() { return 1; }
    default void update(Player player) {}
    default void onJump(Player player) {}
    default void onUpdateMana(Player player) {}
}
