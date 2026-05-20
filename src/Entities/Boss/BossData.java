package Entities.Boss;

public enum BossData {

    EMBRYN(0,
            150, 90,
            10f, 45f,
            10, 1),

    KAELOR(1,
            120, 180,
            65f, 54f,
            15, 1),

    SYLTHRA(2,
            120, 180,
            200f, 212f,
            15, 1);

    public final int bossType;
    public final int hbWidth, hbHeight;
    public final float drawOffX, drawOffY;
    public final int maxHealth;
    public final int damage;

    BossData(int type, int hbW, int hbH, float offX, float offY, int health, int dmg) {
        this.bossType = type;
        this.hbWidth = hbW;
        this.hbHeight = hbH;
        this.drawOffX = offX;
        this.drawOffY = offY;
        this.maxHealth = health;
        this.damage = dmg;
    }

    public static BossData getByOrder(int type) {
        for (BossData data : BossData.values()) {
            if (data.bossType == type) return data;
        }
        return EMBRYN; //fallback
    }

    public int getHealth() {
        return maxHealth;
    }
}