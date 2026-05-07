package Utils;

import Entities.Boss.Embryn;
import Main.Core.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.NPCConstants.*;
import static Utils.Constants.ObjectConstants.*;
import static Utils.Constants.PlayerConstants.PLAYER_SPAWN;

public class HelpMethods {
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        // Check Corners
        if (!IsSolid(x, y, lvlData)) // Top-Left
            if (!IsSolid(x + width, y, lvlData)) // Top-Right
                if (!IsSolid(x, y + height, lvlData)) // Bottom-Left
                    if (!IsSolid(x + width, y + height, lvlData)) // Bottom-Right

                        if (!IsSolid(x, y + (height / 2), lvlData)) // Left-Middle
                            if (!IsSolid(x + width, y + (height / 2), lvlData)) // Right-Middle

                                if (!IsSolid(x + (width / 2), y, lvlData)) // Top-Middle
                                    if (!IsSolid(x + (width / 2), y + height, lvlData)) // Bottom-Middle

                                        return true; //if solid then no return
        return false;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData) {

        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        int maxHeight = lvlData.length * Game.TILES_SIZE;

        if (x < 0 || x >= maxWidth) return true;
        if (y < 0 || y >= maxHeight) return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];
        return value != AIR && value != LADDER_COLOR; // 18 (Air) & 132 (Ladder)
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
        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) {
            if (!IsSolid(hitbox.x + hitbox.width - 1, hitbox.y + hitbox.height + 1, lvlData)) {
                if (!Game.getInstance().getPlaying().getPlayer().isDown()) {
                    for (Objects.Platform p : Game.getInstance().getPlaying().getObjectManager().getPlatforms()) {
                        if (hitbox.x + hitbox.width > p.getHitbox().x && hitbox.x < p.getHitbox().x + p.getHitbox().width) {
                            if (Math.abs((hitbox.y + hitbox.height) - p.getHitbox().y) < 2) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0)
            return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
        else
            return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
    }

    public static int[][] GetLevelData(BufferedImage img, Point playerSpawn) {
        if (img == null) {
            System.err.println("Level data image is null. Returning empty array.");
            return new int[0][0];
        }

        int[][] lvlData = new int[img.getHeight()][img.getWidth()];

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int redValue = color.getRed();
                int greenValue = color.getGreen();
                int blueValue = color.getBlue();

                if (greenValue == PLAYER_SPAWN) { //spawnpoint
                    playerSpawn.x = i * Game.TILES_SIZE;
                    playerSpawn.y = j * Game.TILES_SIZE;
                    System.out.println("Spawn Found at Tile: " + i + ", " + j);
                    lvlData[j][i] = AIR;
                } else if ( blueValue == VASE_COLOR  ||
                        blueValue == SPIKE_COLOR ||
                        blueValue == PLATFORM_COLOR ||
                        blueValue == NINO_TQ     ||
                        blueValue == CHAD_TB     ||
                        blueValue == CHARLZ_TS   ||
                        blueValue == RILEY_TZ    ||
                        blueValue == DENVER_TC   ||
                        blueValue == BOSS_LAYER) {
                    lvlData[j][i] = AIR;
                } else if (blueValue == CRUMBLING_TILE_COLOR) {
                    lvlData[j][i] = INVISIBLE_SOLID;
                } else if (blueValue == LADDER_COLOR) {
                    lvlData[j][i] = LADDER_COLOR; // 132
                } else {
                    if (redValue >= 50) {
                        lvlData[j][i] = AIR;
                    } else {
                        lvlData[j][i] = redValue;
                    }
                }
            }
        }
        return lvlData;
    }

    public static boolean isSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int tileY) {
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile) {
            // Player is to the Left: Check tiles from Player + 1 to Boss - 1
            for (int i = secondXTile + 1; i < firstXTile; i++)
                if (IsSolid(i * Game.TILES_SIZE, tileY * Game.TILES_SIZE, lvlData))
                    return false;
        } else {
            // Player is to the Right: Check tiles from Boss + 1 to Player - 1
            for (int i = firstXTile + 1; i < secondXTile; i++)
                if (IsSolid(i * Game.TILES_SIZE, tileY * Game.TILES_SIZE, lvlData))
                    return false;
        }
        return true;
    }

    public static boolean isBossPixel(int blueValue) {
        return blueValue == BOSS_LAYER;
    }

    public static int getBossType(int greenValue) {
        return switch (greenValue) {
            case 101 -> EMBRYN;
            case 102 -> KAELOR;
            default -> -1;
        };
    }

    public static boolean isEntityOnLadder(Rectangle2D.Float hitbox, int[][] lvlData) {
        float centerX = hitbox.x + hitbox.width / 2;
        float topY = hitbox.y;
        float botY = hitbox.y + hitbox.height;

        // coords
        int xIndex = (int) (centerX / Game.TILES_SIZE);
        int yTopIndex = (int) (topY / Game.TILES_SIZE);
        int yBotIndex = (int) (botY / Game.TILES_SIZE);

        // within bounds
        if (xIndex < 0 || xIndex >= lvlData[0].length) return false;
        if (yTopIndex < 0 || yTopIndex >= lvlData.length) return false;
        if (yBotIndex < 0 || yBotIndex >= lvlData.length) return false;

        return lvlData[yTopIndex][xIndex] == 132 || lvlData[yBotIndex][xIndex] == 132;
    }

    public static boolean isTilePassable(float x, float y, int[][] lvlData) {
        int value = lvlData[(int) (y / Game.TILES_SIZE)][(int) (x / Game.TILES_SIZE)];
        return value == PLATFORM_COLOR;
    }
}