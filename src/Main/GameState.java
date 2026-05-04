package Main;

public enum GameState {
    MENU,
    PLAYING,
    SLOTS,
    PAUSED,
    DEATH,
    CHARACTER_SELECT,
    CUTSCENE;

    // The current active state
    public static GameState state = MENU;
}