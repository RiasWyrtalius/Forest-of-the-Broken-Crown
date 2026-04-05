package Utils;

import Main.Game;
import java.awt.geom.Rectangle2D;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        //-1 to ensure we check the exact edge of the body.
        if (!IsSolid(x, y, lvlData)) // Top Left
            if (!IsSolid(x + width - 1, y, lvlData)) // Top Right
                if (!IsSolid(x, y + height - 1, lvlData)) // Bottom Left
                    if (!IsSolid(x + width - 1, y + height - 1, lvlData)) // Bottom Right
                        return true;

        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] lvlData) {
        if (x < 0 || x >= Game.GAME_WIDTH) return true;
        if (y < 0 || y >= Game.GAME_HEIGHT) return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];
        return value != 18; // 16 is air
    }

    public static float getEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        if (xSpeed > 0) {
            // Moving Right: Calculate tile index of the RIGHT edge
            int currentTile = (int) ((hitbox.x + hitbox.width - 1) / Game.TILES_SIZE);
            return (currentTile + 1) * Game.TILES_SIZE - hitbox.width - 1;
        } else {
            // Moving Left: Calculate tile index of the LEFT edge
            int currentTile = (int) (hitbox.x / Game.TILES_SIZE);
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static float getEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        if (airSpeed > 0) {
            // Falling: Calculate tile index of the BOTTOM edge
            int currentTile = (int) ((hitbox.y + hitbox.height - 1) / Game.TILES_SIZE);
            return (currentTile + 1) * Game.TILES_SIZE - hitbox.height - 1;
        } else {
            // Jumping: Calculate tile index of the TOP edge
            int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
            if (!IsSolid(hitbox.x + hitbox.width - 1, hitbox.y + hitbox.height + 1, lvlData))
                return false;
        return true;
    }
}