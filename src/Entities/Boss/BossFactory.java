package Entities.Boss;

import static Utils.Constants.EnemyConstants.*;

public class BossFactory {
    public static Boss CreateBoss(int bossType, float x, float y) {
        return switch (bossType) {
            case EMBRYN -> new Embryn(x, y);
            case KAELOR -> new Kaelor(x, y);
            case SYLTHRA -> new Sylthra(x, y);
            default -> null;
        };
    }
}
