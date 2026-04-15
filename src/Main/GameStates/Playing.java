package Main.GameStates;

import Entities.Player;
import Levels.Level;
import Levels.LevelHandler;
import Main.Core.Game;
import Main.GameState;
import Objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing {
    private Player player;
    ObjectManager objectManager;
    private Game game;
    private LevelHandler levelHandler;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;

    public Playing(Game game) {
        this.game = game;
        this.levelHandler = game.getLevelHandler();
        this.player = game.getPlayer();
        this.objectManager = game.getObjectManager();
    }

    public void update() {
        levelHandler.update();
        objectManager.update(game.getPlayer());
        game.getPlayer().update();
        checkCloseToBorder();
        checkLevelCompleted();
    }

    public void draw(Graphics g) {
        g.drawImage(game.getBackgroundImg(), 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        game.drawGameBackground(g);
        game.getObjectManager().draw(g, xLvlOffset);
        game.getUi().draw(g);
        game.drawSaveMessage(g);
    }

    public void updateLevelOffsets() {
        int lvlTilesWide = game.getLevelHandler().getCurrentLevel().getLevelData()[0].length;
        maxLvlOffsetX = (lvlTilesWide - Game.TILES_IN_WIDTH) * Game.TILES_SIZE;
    }

    public void checkCloseToBorder() {
        int playerX = (int) game.getPlayer().getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder) {
            xLvlOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLvlOffset += diff - leftBorder;
        }

        if (xLvlOffset > maxLvlOffsetX){
            xLvlOffset = maxLvlOffsetX;
        } else if (xLvlOffset < 0){
            xLvlOffset = 0;
        }
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
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player.setLeft(true);
            case KeyEvent.VK_D -> player.setRight(true);
            case KeyEvent.VK_SPACE -> player.setJump(true);
            case KeyEvent.VK_ESCAPE -> GameState.state = GameState.PAUSED;
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

    public void setPlayer(Player player) { this.player = player; }
}