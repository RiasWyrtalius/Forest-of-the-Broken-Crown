package Entities;

import Main.Core.Game;

import static Utils.Constants.GRAVITY;

public enum PlayerCharacter {
    KAELTHORN(4, 6, 6, 5, 1, 6,
            0,1,2,3,4,5,
            30, 50,
            34f, 30f,
            5, 10,
            1.0f, GRAVITY,
            "A broken oathbearer and former noble knight who was banished as a traitor after his liege turned into a tyrant. " +
                    "He specializes in precision platforming with a high-reaching Wolf Leap and controlled, steady jumps."),

    SYLVARA(4, 9, 9, 6, 1, 7, 5,
            0, 1, 2, 3, 4, 5, 6,
            32, 45,
            38f, 30f,
            3, 20,
            1.3f, 0.02f * Game.SCALE,
            "A once-bright mystic whose magic fractured into a shadow known as the Fallen Owl Witch. " +
                    "She is a ranged specialist who uses Gust magic to strike from a distance and can gracefully glide over collapsing terrain."),

    EMBJORN(4, 6, 6, 5, 1, 6,
            0, 1, 2, 3, 4, 5,
            40, 55,
            37f, 20f,
            6, 10,
            1.2f, GRAVITY,
            "A former temple oracle branded a deceiver after his prophecies turned to madness. " +
            "He slithers through the forest with high mobility, using Venom Dashes to slip past hazards and moving faster on narrow platforms.");

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

    public String description = "";

    //For Sylvara specifically
    PlayerCharacter (int spriteA_IDLE, int spriteA_WALKR, int spriteA_WALKL, int spriteA_JUMP, int spriteA_AIRBORNE, int spriteA_DOUBLEJUMP, int spriteA_LANDING,
                     int rowIDLE, int rowWALKR, int rowWALKL, int rowJUMP, int rowAIRBORNE, int rowDOUBLEJUMP, int rowLANDING,
                     int hitboxWidth, int hitboxHeight,
                     float xOff, float yOff,
                     int lives, int mana,
                     float speedMult, float gravity,
                     String desc) {

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

        this.description = desc;
    }

    //For KaelThorn & Embjorn specifically
    PlayerCharacter (int spriteA_IDLE, int spriteA_WALKR, int spriteA_WALKL, int spriteA_JUMP, int spriteA_AIRBORNE, int spriteA_LANDING,
                     int rowIDLE, int rowWALKR, int rowWALKL, int rowJUMP, int rowAIRBORNE, int rowLANDING,
                     int hitboxWidth, int hitboxHeight,
                     float xOff, float yOff,
                     int lives, int mana,
                     float speedMult, float gravity,
                     String desc) {
        this(spriteA_IDLE, spriteA_WALKR, spriteA_WALKL, spriteA_JUMP, spriteA_AIRBORNE, 0, spriteA_LANDING,
                rowIDLE, rowWALKR, rowWALKL, rowJUMP, rowAIRBORNE, -1, rowLANDING,
                hitboxWidth, hitboxHeight,
                xOff, yOff,
                lives, mana,
                speedMult, gravity,
                desc);
    }

    public int getSpriteAmountIDLE() { return spriteA_IDLE; }
    public int getRowIDLE() { return rowIDLE; }
    public int getLives() { return defaultLives; }
    public int getMana() { return defaultManaBottles; }
    public String getDescription() { return description; }
}
