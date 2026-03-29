package Utils;

import Main.Game;

import java.awt.geom.Rectangle2D;

public class HelpMethods {
    public static boolean CanMoveHere(float x, float y, float width, float height, int [][] lvlData){
        if (!IsSolid(x, y, lvlData))
            if(!IsSolid(x + width, y + height, lvlData))
                if(!IsSolid(x + width, y, lvlData))
                    if(!IsSolid(x, y + height, lvlData))
                        return true;
        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] lvlData){

        if(x < 0 || x >= Game.GAME_WIDTH)
            return true;
        if(y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];
//        System.out.println("Tile ID at player: " + value);

        return value != 11;// air indices
    }

    public static float getEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {

        int currTile = (int)(hitbox.x / Game.TILES_SIZE);

        if (xSpeed > 0) { // Right
            int tileXPos = currTile * Game.TILES_SIZE;
            int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else { // Left
            return currTile * Game.TILES_SIZE;
        }
    }

    public static float getEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {

        int currTile = (int)(hitbox.y / Game.TILES_SIZE);

        if (airSpeed > 0) { // Falling
            int tileYPos = currTile * Game.TILES_SIZE;
            int yOffset = (int)(Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else { // Jumping
            return currTile * Game.TILES_SIZE;
        }
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        //Checks pixels below bottomLeft and bottomRight

        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) {
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData)) {
                return false;
            }
        }
        return true;
    }
}
