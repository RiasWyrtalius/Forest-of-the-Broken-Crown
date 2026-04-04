// Changes made by AI Assistant:
// - Created Crabby.java as a concrete enemy class extending Enemy.
// - Implements walking AI, gravity, collision detection, and animation.
// - Added null checks in loadAnimations() and render() to handle missing sprites gracefully.
// - Fixed method name case for getEntityYPosUnderRoofOrAboveFloor().

package Entities;

import Main.Game;
import static Utils.Constants.Directions.*;
import static Utils.Constants.EnemyConstants.*;
import static Utils.HelpMethods.*;
import Utils.LoadSave;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Crabby extends Enemy {

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 25;
    private int enemyState = IDLE;
    private int enemyType;
    private boolean firstUpdate = true;
    private int walkDir = LEFT;
    private int tileY;
    private float walkSpeed = 0.5f * Game.SCALE;

    public Crabby(float x, float y) {
        super(x, y, (int)(43 * Game.SCALE), (int)(46 * Game.SCALE), 10, 1);
        enemyType = CRABBY;
        loadAnimations();
        initHitbox(x, y, (int)(43 * Game.SCALE), (int)(46 * Game.SCALE));
    }

    public void update(int[][] lvlData) {
        updateMove(lvlData);
        updateAnimationTick();
    }

    private void updateMove(int[][] lvlData) {
        if (firstUpdate) {
            if (!isEntityOnFloor(hitbox, lvlData))
                inAir = true;
            firstUpdate = false;
        }

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
            } else {
                inAir = false;
                hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                tileY = (int) (hitbox.y / Game.TILES_SIZE);
            }
        } else {
            switch (enemyState) {
                case IDLE:
                    enemyState = RUNNING;
                    break;
                case RUNNING:
                    float xSpeed = 0;

                    if (walkDir == LEFT)
                        xSpeed = -walkSpeed;
                    else
                        xSpeed = walkSpeed;

                    if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
                        if (IsFloor(hitbox, xSpeed, lvlData)) {
                            hitbox.x += xSpeed;
                            return;
                        }

                    changeWalkDir();
                    break;
            }
        }
    }

    private void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    public void render(Graphics g) {
        if (animations != null) {
           g.drawImage(animations[enemyState][animationIndex], (int) hitbox.x, (int) hitbox.y, (int)(43 * Game.SCALE), (int)(46 * Game.SCALE), null);
        }
        drawHitbox(g);
    }

    private void updateAnimationTick() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animationIndex = 0;
            }
        }
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.Crabby_Atlas);
        if (img != null) {
            int rows = img.getHeight() / Game.SPRITE_DEFAULT_SIZE;
            animations = new BufferedImage[rows][];
            for (int j = 0; j < animations.length; j++) {
                animations[j] = new BufferedImage[GetSpriteAmount(CRABBY, j)];
                for (int i = 0; i < animations[j].length; i++) {
                    animations[j][i] = img.getSubimage(
                            i * Game.SPRITE_DEFAULT_SIZE,
                            j * Game.SPRITE_DEFAULT_SIZE,
                            Game.SPRITE_DEFAULT_SIZE,
                            Game.SPRITE_DEFAULT_SIZE
                    );
                }
            }
        } else {
            System.err.println("Failed to load Crabby atlas: " + LoadSave.Crabby_Atlas);
        }
    }

    public int flipX() {
        if (walkDir == LEFT)
            return 0;
        else
            return width;
    }

    public int flipW() {
        if (walkDir == LEFT)
            return 1;
        else
            return -1;
    }
}