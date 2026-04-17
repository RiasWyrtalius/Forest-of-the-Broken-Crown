package Entities;

import Main.Core.Game;

import static Utils.Constants.GRAVITY;

public enum PlayerCharacter {
    KAELTHORN(4, 6, 6, 5, 1, 6,
            0,1,2,3,4,5,
            30, 50,
            34f, 30f,
            5, 10,
            1.0f, GRAVITY),

    SYLVARA(4, 9, 9, 6, 1, 7, 5,
            0, 1, 2, 3, 4, 5, 6,
            32, 45,
            38f, 30f,
            3, 20,
            1.3f, 0.02f * Game.SCALE),

    EMBJORN(4, 6, 6, 5, 1, 6,
            0, 1, 2, 3, 4, 5,
            40, 55,
            37f, 20f,
            6, 10,
            1.2f, GRAVITY);

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

    public int defaultManaBottles;
    public float defaultGravity;

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
                     int hitboxWidth, int hitboxHeight,
                     float xOff, float yOff,
                     int lives, int mana,
                     float speedMult, float gravity) {

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

        //HP, Mana, Speed, Gravity
        this.defaultLives = lives;
        this.defaultManaBottles = mana;
        this.speedMultiplier = speedMult;
        this.defaultGravity = gravity;
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
                     int hitboxWidth, int hitboxHeight,
                     float xOff, float yOff,
                     int lives, int mana,
                     float speedMult, float gravity) {
        this(spriteA_IDLE, spriteA_WALKR, spriteA_WALKL, spriteA_JUMP, spriteA_AIRBORNE, 0, spriteA_LANDING,
                rowIDLE, rowWALKR, rowWALKL, rowJUMP, rowAIRBORNE, -1, rowLANDING,
                hitboxWidth, hitboxHeight,
                xOff, yOff,
                lives, mana,
                speedMult, gravity);
    }

    public int getSpriteAmountIDLE() { return spriteA_IDLE; }
    public int getRowIDLE() { return rowIDLE; }
}
