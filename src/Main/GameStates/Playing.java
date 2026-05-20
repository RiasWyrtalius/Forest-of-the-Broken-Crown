package Main.GameStates;

import Entities.Boss.EnemyManager;
import Entities.Boss.Sylthra;
import Entities.NPC;
import Entities.Player;
import Levels.Level;
import Levels.LevelHandler;
import Main.Core.Game;
import Main.GameState;
import Main.UI.DialogueManager;
import Objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;

import static Utils.Constants.EnemyConstants.*;

public class Playing {
    private boolean victoryTriggered = false;
    private Player player;
    private ObjectManager objectManager;
    private Game game;
    private LevelHandler levelHandler;
    private DialogueManager dialogueManager;
    private EnemyManager enemyManager;

    private int xLvlOffset;
    private int yLvlOffset;
    private float actualXOffset, actualYOffset;
    private float smoothing = 0.015f;
    private int deadZone = (int) (Game.TILES_SIZE * 1.5);

    private int maxLvlOffsetX;
    private int maxLvlOffsetY;

    public Playing(Game game) {
        this.game = game;
        this.levelHandler = game.getLevelHandler();
        this.player = game.getPlayer();
        this.objectManager = game.getObjectManager();
        this.dialogueManager = new DialogueManager();
        this.enemyManager = new EnemyManager(this);
        loadEnemiesForLevel(levelHandler.getCurrentLevelNum());
    }

    public void update() {
        levelHandler.update();
        objectManager.update(player);
        game.getPlayer().update();
        objectManager.checkSpikesTouched(player);
        enemyManager.update(levelHandler.getCurrentLevel().getLevelData(), player);
        checkCloseToBorder();
        checkLevelCompleted();
        checkVictory();

        if (dialogueManager.isActive()) {
            dialogueManager.update();
        }
    }

    public void draw(Graphics g) {
        g.drawImage(game.getBackgroundImg(), 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        levelHandler.draw(g, xLvlOffset, yLvlOffset);
        objectManager.draw(g, xLvlOffset, yLvlOffset);
        enemyManager.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);
        game.getUi().draw(g);
        game.getUi().drawSaveMessage(g);
        dialogueManager.draw(g);
        game.getUi().drawBossDefeated(g);
        game.getUi().drawWorldName(g);

        if (enemyManager.getBoss() instanceof Sylthra sylthra) {
            game.getUi().drawStarCounter(g, sylthra.getStarsCollected());
        }
    }

    public void updateLevelOffsets() {
        int lvlTilesWide = game.getLevelHandler().getCurrentLevel().getLevelData()[0].length;
        maxLvlOffsetX = (lvlTilesWide - Game.TILES_IN_WIDTH) * Game.TILES_SIZE;
    }

    private void checkCloseToBorder() {
        // current center position
        float playerCenterX = player.getHitbox().x + (player.getHitbox().width / 2);
        float playerCenterY = player.getHitbox().y + (player.getHitbox().height / 2);

        // centering on player screen
        float targetX = playerCenterX - ((float) Game.GAME_WIDTH / 2);
        float targetY = playerCenterY - ((float) Game.GAME_HEIGHT / 2);

        // deadzone
        float currentCamCenterX = actualXOffset + ((float) Game.GAME_WIDTH / 2);
        float currentCamCenterY = actualYOffset + ((float) Game.GAME_HEIGHT / 2);

        if (Math.abs(playerCenterX - currentCamCenterX) < deadZone) {
            targetX = actualXOffset;
        }
        if (Math.abs(playerCenterY - currentCamCenterY) < deadZone) {
            targetY = actualYOffset;
        }

        // camera smoothing
        actualXOffset += (targetX - actualXOffset) * smoothing;
        actualYOffset += (targetY - actualYOffset) * smoothing;

        // constrain to Level Bounds
        // calculate max vertical scroll dynamically based on level height
        int lvlHeightPixels = levelHandler.getCurrentLevel().getLevelData().length * Game.TILES_SIZE;
        maxLvlOffsetY = lvlHeightPixels - Game.GAME_HEIGHT;

        if (actualXOffset < 0) actualXOffset = 0;
        else if (actualXOffset > maxLvlOffsetX) actualXOffset = maxLvlOffsetX;

        if (actualYOffset < 0) actualYOffset = 0;
        else if (actualYOffset > maxLvlOffsetY) actualYOffset = maxLvlOffsetY;

        // final offsets to render
        xLvlOffset = (int) actualXOffset;
        yLvlOffset = (int) actualYOffset;
    }

    public void checkLevelCompleted() {
        int lvlWidth = levelHandler.getCurrentLevel().getLevelData()[0].length * Game.TILES_SIZE;

        if (player.getHitbox().x >= lvlWidth - (Game.TILES_SIZE * 2)) {
            int currentLvl = levelHandler.getCurrentLevelNum();

            boolean canProceed = switch (currentLvl) {
                case 2 -> enemyManager.isBossTypeDefeated(EMBRYN);
                case 4 -> enemyManager.isBossTypeDefeated(KAELOR);
                case 6 -> enemyManager.isBossTypeDefeated(SYLTHRA);
                default -> true;
            };

            if (!canProceed) {
                game.getUi().setBossMsg("YOU CANNOT LEAVE!");
                return;
            }

            if (currentLvl == 2) game.setEmbryDefeated(true);
            if (currentLvl == 4) game.setKaelDefeated(true);
            if (currentLvl == 6) game.setSylthraDefeated(true);

            System.out.println("Level Complete Triggered!");
            int nextLevelNum = currentLvl + 1;

            if (nextLevelNum <= levelHandler.getAmountOfLevels()) {
                game.setupLevel(nextLevelNum);
                game.startFadeTo(Main.GameState.PLAYING);
                xLvlOffset = 0;
                resetCamera();
            } else {
                game.startFadeTo(Main.GameState.MENU);
                game.resetGame();
            }
        }
    }

    private void checkVictory() {
        if (victoryTriggered) return;
        if (game.getLevelHandler().getCurrentLevelNum() == 6) {
            if (enemyManager.isBossTypeDefeated(SYLTHRA)) {
                victoryTriggered = true;
                boolean goodEnding = player.getLife() > (player.getMaxLife() / 2);
                String endingKey = goodEnding ? "OUTRO_GOOD" : "OUTRO_BAD";
                game.getCutsceneState().startCutscene(endingKey, GameState.NAME_INPUT);
                GameState.state = GameState.CUTSCENE;
            }
        }
    }

    public void resetCamera() {
        xLvlOffset = 0;
        updateLevelOffsets();
    }

    //INPUT METHODS
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_K) enemyManager.killAllBosses();

        if (dialogueManager.isActive() && e.getKeyCode() == KeyEvent.VK_ENTER) {
            dialogueManager.skipOrNext();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> player.setClimbUp(true);
            case KeyEvent.VK_S -> player.setDown(true);
            case KeyEvent.VK_ESCAPE -> GameState.state = GameState.PAUSED;
            case KeyEvent.VK_ENTER -> {
                NPC npc = objectManager.getHoveredNPC();
                if (npc != null) {

                    dialogueManager.startDialogue(npc.getDialogue(), npc);

                    if (npc.isSavePoint()) {
                        game.getSlotScreen().setMode("SAVE");
                        GameState.state = GameState.SLOTS;
                    } else {
                        dialogueManager.startDialogue(npc.getDialogue(), npc);
                    }
                }
            }
            default -> {
                if (e.getKeyCode() == OptionsScreen.keyJump)  player.setJump(true);
                if (e.getKeyCode() == OptionsScreen.keyLeft)  player.setLeft(true);
                if (e.getKeyCode() == OptionsScreen.keyRight) player.setRight(true);
                if (e.getKeyCode() == OptionsScreen.keySkill) player.getActiveSkill().activate();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> player.setClimbUp(false);
            case KeyEvent.VK_S -> player.setDown(false);
            default -> {
                if (e.getKeyCode() == OptionsScreen.keyJump)  player.setJump(false);
                if (e.getKeyCode() == OptionsScreen.keyLeft)  player.setLeft(false);
                if (e.getKeyCode() == OptionsScreen.keyRight) player.setRight(false);
                if (e.getKeyCode() == OptionsScreen.keySkill) player.getActiveSkill().deactivate();
            }
        }
    }

    public void loadEnemiesForLevel(int levelNum) {
        System.out.println("Loading enemies for level: " + levelNum);
        Level level = levelHandler.getCurrentLevel();

        if (level != null) {
            enemyManager.loadEnemies(level);
            System.out.println("Bosses loaded!");
        }
    }

    public Player getPlayer() { return player; }
    public int getxLvlOffset() { return xLvlOffset; }
    public int getyLvlOffset() { return yLvlOffset; }
    public void setPlayer(Player player) { this.player = player; }
    public EnemyManager getEnemyManager() {return enemyManager;}
    public ObjectManager getObjectManager()     { return objectManager; }
}