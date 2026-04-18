package Utils;

import Main.Core.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static Utils.Constants.NPCConstants.NINO_TQ;
import static Utils.Constants.ObjectConstants.SPIKE_COLOR;
import static Utils.Constants.ObjectConstants.VASE_COLOR;
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

    private static boolean IsSolid(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        if (x < 0 || x >= maxWidth) return true;
        if (y < 0 || y >= Game.GAME_HEIGHT) return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];
        return value != 18; // 18 is air
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
                    lvlData[j][i] = 18;
                } else if (blueValue == VASE_COLOR || blueValue == SPIKE_COLOR || blueValue == NINO_TQ) {
                    lvlData[j][i] = 18;
                } else {
                    if (redValue >= 48) redValue = 0;
                    lvlData[j][i] = redValue;
                }
            }
        }
        return lvlData;
    }

    //UI Helper Method
    public static void DrawHoverableButton(Graphics g, Rectangle btn, String label, Rectangle hoveredBtn, Font font) {
        boolean isHovered = (hoveredBtn != null && hoveredBtn.equals(btn));

        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        int textX = btn.x + (btn.width / 2) - (fm.stringWidth(label) / 2);
        int textY = btn.y + (btn.height / 2) + (fm.getAscent() / 2) - 2;

        if (isHovered) {
            g.setColor(new Color(255, 255, 255, 50)); // faint white shadow
            g.drawString(label, textX + 2, textY + 2);
            g.setColor(Color.YELLOW); // hover
        } else {
            g.setColor(Color.WHITE);
        }

        g.drawString(label, textX, textY);
    }
}