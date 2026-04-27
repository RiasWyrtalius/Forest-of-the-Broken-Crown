package Utils;

import Main.Core.Game;

public class Constants {

    public static final float GRAVITY = 0.03f * Game.SCALE;
    public static final int ANIMATION_SPEED = 30;
    public static final int LEFT  = 0;
    public static final int RIGHT = 1;

    public static class PlayerConstants {
        public static final int IDLE = 0;
        public static final int WALKR = 1;
        public static final int WALKL = 2;
        public static final int JUMP = 3;
        public static final int AIRBORNE = 4;
        public static final int LANDING = 5;
        public static final int DOUBLEJUMP = 6;

        public static final int PLAYER_SPAWN = 100;
    }

    public static class ObjectConstants {
        public static final int VASE = 0;
        public static final int VASE_COLOR = 130;
        public static final int SPIKE = 1;
        public static final int SPIKE_COLOR = 131;

        public static final int SPIKE_FLOOR_MID = 0;
        public static final int SPIKE_FLOOR_RIGHT = 1;
        public static final int SPIKE_FLOOR_LEFT = 5;

        public static final int SPIKE_LEFT = 3;
        public static final int SPIKE_CEILING = 4;

        public static final int HEALTH_POTION = 0;
        public static final int MANA_POTION = 1;
        public static final int HEAL_POTION_VALUE = 1;
        public static final int MANA_POTION_VALUE = 5;

        //40% chance, theres nothing.
        public static final int POTION_DROP_CHANCE = 60;

        public static final int HEALTH_CHANCE = 25; // Hearts are rare/valuable
        public static final int MANA_CHANCE = 75;   // Mana is more common
    }

    public static class NPCConstants {
        public static final int NINO_TQ = 200;
        public static final int NINO_ID = 200;
    }

    public class EnemyConstants {

        public static final int BOSS_LAYER = 199;

        public static final int EMBRYN = 0;
        //TODO: future bosses

        public static final int IDLE    = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK  = 2;
        public static final int HIT     = 3;
        public static final int DEAD    = 4;

        public static final int ANI_SPEED = 25;

        public static final int EMBRYN_WIDTH_DEFAULT  = 128;
        public static final int EMBRYN_HEIGHT_DEFAULT = 128;
        public static final int EMBRYN_WIDTH  = (int) (180 * Game.SCALE);
        public static final int EMBRYN_HEIGHT = (int) (180 * Game.SCALE);
    }
}
