package Entities;

public interface Passive {

    String getName();
    String getDescription();
    default void update(Player player) {}
}
