package Main.GameStates;

import Entities.NPC;
import Entities.Player;
import Levels.LevelHandler;
import Main.Core.Game;
import Main.GameState;
import Main.UI.DialogueManager;
import Objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing {
    private Player player;
    private ObjectManager objectManager;
    private Game game;
    private LevelHandler levelHandler;
    private DialogueManager dialogueManager;

    private int xLvlOffset;
    private int yLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int topBorder = (int) (0.4 * Game.GAME_HEIGHT);
    private int bottomBorder = (int) (0.6 * Game.GAME_HEIGHT);
    private int maxLvlOffsetX;
    private int maxLvlOffsetY;

    public Playing(Game game) {
        this.game = game;
        this.levelHandler = game.getLevelHandler();
        this.player = game.getPlayer();
        this.objectManager = game.getObjectManager();
        this.dialogueManager = new DialogueManager();
    }

    public void update() {
        levelHandler.update();
        objectManager.update(game.getPlayer());
        game.getPlayer().update();
        objectManager.checkSpikesTouched(player);
        checkCloseToBorder();
        checkLevelCompleted();

        if (dialogueManager.isActive()) {
            dialogueManager.update();
        }
    }

    public void draw(Graphics g) {
        g.drawImage(game.getBackgroundImg(), 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        levelHandler.draw(g, xLvlOffset, yLvlOffset);
        objectManager.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);
        game.getUi().draw(g);
        game.drawSaveMessage(g);
        dialogueManager.draw(g);
    }

    public void updateLevelOffsets() {
        int lvlTilesWide = game.getLevelHandler().getCurrentLevel().getLevelData()[0].length;
        maxLvlOffsetX = (lvlTilesWide - Game.TILES_IN_WIDTH) * Game.TILES_SIZE;
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diffX = playerX - xLvlOffset;

        // Horizontal Scroll
        if (diffX > rightBorder)
            xLvlOffset += diffX - rightBorder;
        else if (diffX < leftBorder)
            xLvlOffset += diffX - leftBorder;

        if (xLvlOffset < 0) xLvlOffset = 0;
        else if (xLvlOffset > maxLvlOffsetX) xLvlOffset = maxLvlOffsetX;

        // Vertical Scroll
        int playerY = (int) player.getHitbox().y;
        int diffY = playerY - yLvlOffset;

        if (diffY > bottomBorder)
            yLvlOffset += diffY - bottomBorder;
        else if (diffY < topBorder)
            yLvlOffset += diffY - topBorder;

        // this calculates the max vertical scroll based on whatever the map height is.
        int lvlHeightPixels = levelHandler.getCurrentLevel().getLevelData().length * Game.TILES_SIZE;
        maxLvlOffsetY = lvlHeightPixels - Game.GAME_HEIGHT;

        if (yLvlOffset < 0) yLvlOffset = 0;
        else if (yLvlOffset > maxLvlOffsetY) yLvlOffset = maxLvlOffsetY;
    }

    //TEMPORARY
    public void checkLevelCompleted() {
        int lvlWidth = levelHandler.getCurrentLevel().getLevelData()[0].length * Game.TILES_SIZE;

        if (player.getHitbox().x >= lvlWidth - (Game.TILES_SIZE * 2)) {
            System.out.println("Level Complete Triggered!");
            int nextLevelNum = levelHandler.getCurrentLevelNum() + 1;

            if (nextLevelNum <= levelHandler.getAmountOfLevels()) {
                game.setupLevel(nextLevelNum);
                game.startFadeTo(GameState.PLAYING);
                xLvlOffset = 0;
                resetCamera();
            } else {
                game.startFadeTo(GameState.MENU);
                game.resetGame();
            }
        }
    }

    public void resetCamera() {
        xLvlOffset = 0;
        updateLevelOffsets();
    }

    //INPUT METHODS
    public void keyPressed(KeyEvent e) {
        if (dialogueManager.isActive() && e.getKeyCode() == KeyEvent.VK_ENTER) {
            dialogueManager.skipOrNext();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player.setLeft(true);
            case KeyEvent.VK_D -> player.setRight(true);
            case KeyEvent.VK_SPACE -> player.setJump(true);
            case KeyEvent.VK_ESCAPE -> GameState.state = GameState.PAUSED;
            case KeyEvent.VK_ENTER -> {
                NPC npc = objectManager.getHoveredNPC();
                if (npc != null) {
                    dialogueManager.startDialogue(npc.getDialogue());
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player.setLeft(false);
            case KeyEvent.VK_D -> player.setRight(false);
            case KeyEvent.VK_SPACE -> player.setJump(false);
        }
    }

    public void mouseClicked(MouseEvent e) {
        // Placeholder for future combat/interaction
    }

    public int getxLvlOffset() { return xLvlOffset; }
    public int getyLvlOffset() { return yLvlOffset; }
    public void setPlayer(Player player) { this.player = player; }
}