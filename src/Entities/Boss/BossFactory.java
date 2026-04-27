package Entities.Boss;

import static Utils.Constants.EnemyConstants.*;

public class BossFactory {
    public static Boss CreateBoss(int bossType, float x, float y) {
        switch (bossType) {
            case EMBRYN:
                return new Embryn(x, y);
            //case BOSS_2:
            default:
                return null;
        }
    }
}
