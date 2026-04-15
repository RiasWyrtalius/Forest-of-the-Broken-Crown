package Entities;

public enum PlayerCharacter {
    KAELTHORN(4, 6, 6, 5, 1, 6,
            0,1,2,3,4,5,
            33, 50,
            35f, 30f,
            5,
            1.0f),

    SYLVARA(4, 9, 9, 6, 1, 7, 5,
            0, 1, 2, 3, 4, 5, 6,
            44, 45,
            30f, 30f,
            3,
            1.5f),

    EMBJORN(4, 6, 6, 5, 1, 6,
            0, 1, 2, 3, 4, 5,
            55, 55,
            22f, 20f,
            5,
            1.2f);

    //Amount of Sprites
    int spriteA_IDLE;
    int spriteA_WALKR;
    int spriteA_WALKL;
    int spriteA_JUMP;
    int spriteA_AIRBORNE;
    int spriteA_DOUBLEJUMP;
    int spriteA_LANDING;

    //Row Indices
    int rowIDLE;
    int rowWALKR;
    int rowWALKL;
    int rowJUMP;
    int rowAIRBORNE;
    int rowDOUBLEJUMP;
    int rowLANDING;

    public int hitboxWidth, hitboxHeight;

    public float xOffset, yOffset;

    public int defaultLives;
    public float speedMultiplier;

    //For Sylvara specifically
    PlayerCharacter (int spriteA_IDLE,
                     int spriteA_WALKR,
                     int spriteA_WALKL,
                     int spriteA_JUMP,
                     int spriteA_AIRBORNE,
                     int spriteA_DOUBLEJUMP,
                     int spriteA_LANDING,
                     int rowIDLE,
                     int rowWALKR,
                     int rowWALKL,
                     int rowJUMP,
                     int rowAIRBORNE,
                     int rowDOUBLEJUMP,
                     int rowLANDING,
                     int hitboxWidth,
                     int hitboxHeight,
                     float xOff,
                     float yOff,
                     int lives,
                     float speedMult) {

        // Animation Sprite IDs
        this.spriteA_IDLE = spriteA_IDLE;
        this.spriteA_WALKR = spriteA_WALKR;
        this.spriteA_WALKL = spriteA_WALKL;
        this.spriteA_JUMP = spriteA_JUMP;
        this.spriteA_AIRBORNE = spriteA_AIRBORNE;
        this.spriteA_DOUBLEJUMP = spriteA_DOUBLEJUMP;
        this.spriteA_LANDING = spriteA_LANDING;

        // Sprite Sheet Row Indices
        this.rowIDLE = rowIDLE;
        this.rowWALKR = rowWALKR;
        this.rowWALKL = rowWALKL;
        this.rowJUMP = rowJUMP;
        this.rowAIRBORNE = rowAIRBORNE;
        this.rowDOUBLEJUMP = rowDOUBLEJUMP;
        this.rowLANDING = rowLANDING;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;

        //Sprite Placement
        this.xOffset = xOff;
        this.yOffset = yOff;

        //HP & Speed
        this.defaultLives = lives;
        this.speedMultiplier = speedMult;
    }

    //For KaelThorn & Embjorn specifically
    PlayerCharacter (int spriteA_IDLE,
                     int spriteA_WALKR,
                     int spriteA_WALKL,
                     int spriteA_JUMP,
                     int spriteA_AIRBORNE,
                     int spriteA_LANDING,
                     int rowIDLE,
                     int rowWALKR,
                     int rowWALKL,
                     int rowJUMP,
                     int rowAIRBORNE,
                     int rowLANDING,
                     int hitboxWidth,
                     int hitboxHeight,
                     float xOff,
                     float yOff,
                     int lives,
                     float speedMult) {
        this(spriteA_IDLE, spriteA_WALKR, spriteA_WALKL, spriteA_JUMP, spriteA_AIRBORNE, 0, spriteA_LANDING,
                rowIDLE, rowWALKR, rowWALKL, rowJUMP, rowAIRBORNE, -1, rowLANDING,
                hitboxWidth, hitboxHeight,
                xOff, yOff,
                lives, speedMult);
    }

    public int getSpriteAmountIDLE() { return spriteA_IDLE; }
    public int getRowIDLE() { return rowIDLE; }
}
