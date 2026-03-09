package Utils;

public class Constants {
    public static class Directions
    {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class PlayerConstants {
        public static final int IDLE = 0;
        public static final int WALKR = 1;
        public static final int WALKL = 2;
        public static final int ATK_1 = 3;

        public static int GetSpriteAmount(int player_action) {
            switch(player_action) {
                case IDLE: return 4;
                case WALKR: return 9;
                case WALKL: return 9;
                default: return 1;
            }
        }
    }
}
