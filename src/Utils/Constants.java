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
        public static final int AIR = 18;

        public static final int VASE = 0;
        public static final int VASE_COLOR = 130;
        public static final int SPIKE = 1;
        public static final int SPIKE_COLOR = 131;
        public static final int LADDER = 2;
        public static final int LADDER_COLOR = 132;

        public static final int SPIKE_FLOOR_MID = 0;
        public static final int SPIKE_FLOOR_RIGHT = 1;
        public static final int SPIKE_FLOOR_LEFT = 5;

        public static final int SPIKE_LEFT = 3;
        public static final int SPIKE_RIGHT = 6;
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

        public static final int CHAD_TB = 201;
        public static final int CHAD_ID = 201;

        public static final int CHARLZ_TS = 202;
        public static final int CHARLZ_ID = 202;

        public static final int RILEY_TZ = 203;
        public static final int RILEY_ID = 203;

        public static final int DENVER_TC = 204;
        public static final int DENVER_ID = 204;

        public static String getSpritePath(int id) {
            switch (id) {
                case CHAD_TB: return LoadSave.Chad_Atlas;
                case CHARLZ_TS: return LoadSave.Charlz_Atlas;
                case RILEY_TZ: return LoadSave.Riley_Atlas;
                case DENVER_TC: return LoadSave.Denver_Atlas;
                default: return LoadSave.Nino_Atlas;
            }
        }

        public static int getSpriteAmount(int id) {
            switch (id) {
                case NINO_TQ -> {return 5;}
                case CHAD_TB -> {return 14;}
                case CHARLZ_TS, DENVER_TC -> {return 10;}
                case RILEY_TZ -> {return 26;}
                default -> { return 1; }
            }
        }

        public static float getScale(int id) {
            switch (id) {
                case RILEY_TZ:   return 2.0f;
                case CHARLZ_TS, CHAD_TB: return 2.3f;
                case DENVER_TC: return 1.7f;
                case NINO_TQ:   return 1.2f;
                default:     return 1.0f;
            }
        }

        // to prevent tile clipping
        public static int getYOffset(int id) {
            switch(id) {
                case NINO_TQ -> { return 3; }
                case CHAD_TB -> { return 5; }
                case CHARLZ_TS, DENVER_TC -> { return -9; }
                case RILEY_TZ -> { return -16; }
                default -> { return 0; }
            }
        }

        // ARROW OFFSETS
        public static int getHeadNudge(int id) {
            return switch (id) {
                case CHAD_TB -> 50;
                case RILEY_TZ, CHARLZ_TS -> 75;
                case NINO_TQ -> 10;
                case DENVER_TC -> 30;
                default -> 20;
            };
        }

        public static int getSideNudge(int id) {
            return switch (id) {
                case CHAD_TB -> 26;
                case CHARLZ_TS -> 30;
                case RILEY_TZ -> 25;
                case DENVER_TC -> 12;
                case NINO_TQ -> -6;
                default -> 20;
            };
        }

        public static String getName(int id) {
            switch (id) {
                case NINO_TQ -> { return "Niño, The Queer"; }
                case CHAD_TB -> { return "Chad, The Brave"; }
                case CHARLZ_TS -> { return "Charlz, The Sage"; }
                case RILEY_TZ -> { return "Riley, The Zephyr"; }
                case DENVER_TC -> { return "Denver, The Cursed"; }
            }
            return "Name not found!";
        }
    }

    public class EnemyConstants {
        public static final int DETECT = 5;

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
