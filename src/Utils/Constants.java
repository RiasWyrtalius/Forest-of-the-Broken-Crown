package Utils;

import Main.Game;

public class Constants {

    public static final float GRAVITY = 0.04f * Game.SCALE;
    public static final int ANIMATION_SPEED = 30;

    public static class Directions {
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
        public static final int PLAYER_SPAWN = 100;

        public static int GetSpriteAmount(int player_action) {
            switch (player_action) {
                case IDLE:
                    return 4;
                case WALKR:
                    return 9;
                case WALKL:
                    return 9;
                case ATK_1:
                    return 1;
                default:
                    return 1;
            }
        }

        public static class ObjectConstants {
            public static final int VASE = 0;
            public static final int VASE_COLOR = 130;
            public static final int SPIKE = 1;
            public static final int SPIKE_COLOR = 131;
        }
    }
}
