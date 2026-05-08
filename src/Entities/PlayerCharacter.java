package Entities;

import Entities.Passives.EmbjornPassive;
import Entities.Passives.KaelthornPassive;
import Entities.Passives.SylvaraPassive;
import Entities.Skills.Embjorn.VenomDash;
import Entities.Skills.Kaelthorn.SteadyStep;
import Entities.Skills.Skill;
import Entities.Skills.Sylvara.ZephyrBound;
import Main.Core.Game;

import java.util.function.Function;

import static Audio.AudioPlayer.*;
import static Utils.Constants.GRAVITY;

/**
 * Previous Hitbox Values:
 * - Kael: 30, 50
 * - Syl : 32, 45
 * - Emb : 40, 55
 */

public enum PlayerCharacter {
    KAELTHORN(4, 6, 6, 5, 1, 6,
            0,1,2,3,4,5,
            30, 50,
            34f, 30f,
            5, 10,
            1.0f, GRAVITY,
            new KaelthornPassive(), SteadyStep::new,
            KAEL_HURT, KAEL_SKILL,
            "A broken oathbearer and former noble knight who was banished as a traitor after his liege turned into a tyrant. " +
                    "He specializes in precision platforming with a high-reaching Wolf Leap and controlled, steady jumps."),

    SYLVARA(4, 9, 9, 6, 1, 7, 5,
            0, 1, 2, 3, 4, 5, 6,
            30, 45,
            38f, 30f,
            3, 20,
            1.3f, 0.02f * Game.SCALE,
            new SylvaraPassive(), ZephyrBound::new,
            SYL_HURT, SYL_SKILL,
            "A once-bright mystic whose magic fractured into a shadow known as the Fallen Owl Witch. " +
                    "She is a ranged specialist who uses Gust magic to strike from a distance and can gracefully glide over collapsing terrain."),

    EMBJORN(4, 6, 6, 5, 1, 6,
            0, 1, 2, 3, 4, 5,
            30, 55,
            32f, 20f,
            6, 10,
            1.2f, GRAVITY,
            new EmbjornPassive(), VenomDash::new,
            EMB_HURT, EMB_SKILL,
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

    private final Passive passive;
    private final Function<Player, Skill> skillFactory;

    private final int hurtSoundID;
    private final int skillSoundID;
    public String description = "";

    //For Sylvara specifically
    PlayerCharacter (int spriteA_IDLE, int spriteA_WALKR, int spriteA_WALKL, int spriteA_JUMP, int spriteA_AIRBORNE, int spriteA_DOUBLEJUMP, int spriteA_LANDING,
                     int rowIDLE, int rowWALKR, int rowWALKL, int rowJUMP, int rowAIRBORNE, int rowDOUBLEJUMP, int rowLANDING,
                     int hitboxWidth, int hitboxHeight,
                     float xOff, float yOff,
                     int lives, int mana,
                     float speedMult, float gravity,
                     Passive passive, Function<Player, Skill> skillFactory,
                     int hurtSoundID, int skillSoundID,
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

        this.passive = passive;
        this.skillFactory = skillFactory;

        this.hurtSoundID = hurtSoundID;
        this.skillSoundID = skillSoundID;

        this.description = desc;
    }

    //For KaelThorn & Embjorn specifically
    PlayerCharacter (int spriteA_IDLE, int spriteA_WALKR, int spriteA_WALKL, int spriteA_JUMP, int spriteA_AIRBORNE, int spriteA_LANDING,
                     int rowIDLE, int rowWALKR, int rowWALKL, int rowJUMP, int rowAIRBORNE, int rowLANDING,
                     int hitboxWidth, int hitboxHeight,
                     float xOff, float yOff,
                     int lives, int mana,
                     float speedMult, float gravity,
                     Passive passive, Function<Player, Skill> skillFactory,
                     int hurtSoundID, int skillSoundID,
                     String desc) {
        this(spriteA_IDLE, spriteA_WALKR, spriteA_WALKL, spriteA_JUMP, spriteA_AIRBORNE, 0, spriteA_LANDING,
                rowIDLE, rowWALKR, rowWALKL, rowJUMP, rowAIRBORNE, -1, rowLANDING,
                hitboxWidth, hitboxHeight,
                xOff, yOff,
                lives, mana,
                speedMult, gravity,
                passive, skillFactory,
                hurtSoundID, skillSoundID,
                desc);
    }

    public int getSpriteAmountIDLE() { return spriteA_IDLE; }
    public int getRowIDLE() { return rowIDLE; }
    public int getLives() { return defaultLives; }
    public int getMana() { return defaultManaBottles; }
    public Passive getPassive() { return passive; }
    public String getDescription() { return description; }
    public Skill getSkill(Player player) { return skillFactory.apply(player); }

    public int getHurtSoundID() { return hurtSoundID; }
    public int getSkillSoundID() { return hurtSoundID; }
}
