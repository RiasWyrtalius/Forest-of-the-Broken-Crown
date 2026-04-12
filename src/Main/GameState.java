package Main;

public class GameState {
    public static final int MENU = 0;
    public static final int PLAYING = 1;
    public static final int SLOTS = 2; // this shows the save/load slot screen
    public static final int PAUSED = 3; // this shows the pause menu screen
    public static final int DEATH = 4; // this shows the death screen

    public static int state = MENU;
}