// - Added EnemyConstants class with CRABBY type, states (IDLE, RUNNING, ATTACK, HIT, DEAD), and GetSpriteAmount() method for enemy animations.

package Utils;

public class Constants {
    public static class Directions
    {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class EnemyConstants {
        public static final int CRABBY = 0;

        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK = 2;
        public static final int HIT = 3;
        public static final int DEAD = 4;

        public static int GetSpriteAmount(int enemy_type, int enemy_state) {
            switch (enemy_type) {
                case CRABBY:
                    switch (enemy_state) {
                        case IDLE:
                            return 9;
                        case RUNNING:
                            return 6;
                        case ATTACK:
                            return 7;
                        case HIT:
                            return 4;
                        case DEAD:
                            return 5;
                    }
            }
            return 0;
        }
    }

        public static class PlayerConstants {
        public static final int IDLE  = 0;
        public static final int WALKR = 1;
        public static final int WALKL = 2;

        public static int GetSpriteAmount(int playerAction) {
            switch (playerAction) {
                case IDLE:  return 9;
                case WALKR: return 9;
                case WALKL: return 9;
                default:    return 0;
            }
        }
    }
}
