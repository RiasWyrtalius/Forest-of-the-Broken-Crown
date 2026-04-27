package Entities.Boss;

public enum BossData {
    EMBRYN( 0,
            150, 90,
            10f, 45f,
            5, 1,
            6,
            8, 8,
            4, 4,
            8, 8,
            0,
            2 , 1,
            3, 4,
            5, 6);

    public int bossType;
    public int hbWidth, hbHeight;
    public float drawOffX, drawOffY;
    public int maxHealth;
    public int damage;

    //Amount of Sprites
    int spriteA_IDLE;
    int spriteA_WALKRIGHT;
    int spriteA_WALKLEFT;
    int spriteA_DMGLEFT;
    int spriteA_DMGRIGHT;
    int spriteA_DIELEFT;
    int spriteA_DIERIGHT;

    //ROW Indices
    int rowIDLE;
    int rowWALKRIGHT;
    int rowWALKLEFT;
    int rowDMGLEFT;
    int rowDMGRIGHT;
    int rowDIELEFT;
    int rowDIERIGHT;

    BossData(int type,
             int hbW, int hbH,
             float offX, float offY,
             int health, int dmg,
             int spriteA_IDLE,
             int spriteA_WALKRIGHT, int spriteA_WALKLEFT,
             int spriteA_DMGLEFT, int spriteA_DMGRIGHT,
             int spriteA_DIELEFT, int spriteA_DIERIGHT,
             int rowIDLE,
             int rowWALKRIGHT, int rowWALKLEFT,
             int rowDMGLEFT, int rowDMGRIGHT,
             int rowDIELEFT, int rowDIERIGHT) {
        this.bossType = type;
        this.hbWidth = hbW;
        this.hbHeight = hbH;
        this.drawOffX = offX;
        this.drawOffY = offY;
        this.maxHealth = health;
        this.damage = dmg;

        this.spriteA_IDLE = spriteA_IDLE;
        this.spriteA_WALKRIGHT = spriteA_WALKRIGHT;
        this.spriteA_WALKLEFT = spriteA_WALKLEFT;
        this.spriteA_DMGLEFT = spriteA_DMGLEFT;
        this.spriteA_DMGRIGHT = spriteA_DMGRIGHT;
        this.spriteA_DIELEFT = spriteA_DIELEFT;
        this.spriteA_DIERIGHT = spriteA_DIERIGHT;

        this.rowIDLE = rowIDLE;
        this.rowWALKRIGHT = rowWALKRIGHT;
        this.rowWALKLEFT = rowWALKLEFT;
        this.rowDMGLEFT = rowDMGLEFT;
        this.rowDMGRIGHT = rowDMGRIGHT;
        this.rowDIELEFT = rowDIELEFT;
        this.rowDIERIGHT = rowDIERIGHT;
    }

    public static BossData getByOrder(int type) {
        for (BossData data : BossData.values()) {
            if (data.bossType == type) return data;
        }
        return EMBRYN; // Default
    }

    public int getDamage() { return damage; }
    public int getHealth() { return maxHealth; }
}
