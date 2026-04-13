package Utils;

import Main.Core.Game;

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
        public static final int JUMP = 3;
        public static final int AIRBORNE = 4;
        public static final int LANDING = 5;
        public static final int DOUBLEJUMP = 6;

        //TODO: Implement spawn point
        //public static final int PLAYER_SPAWN = 100;

        public static class ObjectConstants {
            public static final int VASE = 0;
            public static final int VASE_COLOR = 130;
            public static final int SPIKE = 1;
            public static final int SPIKE_COLOR = 131;
        }
    }
}
