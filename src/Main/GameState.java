package Main;

public enum GameState {
    MENU,
    PLAYING,
    SLOTS,
    PAUSED,
    DEATH,
    CHARACTER_SELECT,
    CUTSCENE,
    CREDITS,
    OPTIONS,
    WORLD_SELECT,
    LEADERBOARD,
    NAME_INPUT;


    // The current active state
    public static GameState state = MENU;
}