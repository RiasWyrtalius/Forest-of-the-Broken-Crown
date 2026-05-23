package Main.GameStates;

import Levels.Level;
import Main.Core.Game;
import Main.GameState;
import Main.UI.UI;
import Utils.LoadSave;
import Entities.PlayerCharacter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.JOptionPane;

import static Utils.Constants.SlotUI.*;

public class SlotScreen {
    private Font customFont;
    private Game game;
    private String mode = "SAVE";
    private Font saveMsgFont;
    private GameState returnState = GameState.PLAYING;

    //animation
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 12;
    private int currentState = OPENING;
    private boolean active = false;
    private int mouseX, mouseY;

    // Frame Dimensions
    private final int FRAME_WIDTH = 240;
    private final int FRAME_HEIGHT = 135;

    private Rectangle slot1 = new Rectangle(424, 150, 400, 70);
    private Rectangle slot2 = new Rectangle(424, 260, 400, 70);
    private Rectangle slot3 = new Rectangle(424, 370, 400, 70);
    private Rectangle btnBack = new Rectangle(424, 480, 400, 50);
    private Rectangle deleteBtn1 = new Rectangle(840, 165, 80, 40);
    private Rectangle deleteBtn2 = new Rectangle(840, 275, 80, 40);
    private Rectangle deleteBtn3 = new Rectangle(840, 385, 80, 40);

    public SlotScreen(Game game) {
        this.game = game;
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
        saveMsgFont = customFont.deriveFont(32f);
        loadAnimations();
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.SaveBg);
        animations = new BufferedImage[3][9];

        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < GetSpriteAmount(j); i++) {
                animations[j][i] = img.getSubimage(i * FRAME_WIDTH, j * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
            }
        }
    }

    public void update() {
        if (active) {
            updateAnimationTick();
        }
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            if (aniIndex >= GetSpriteAmount(currentState)) {
                if (currentState == OPENING) {
                    currentState = OPENED; // opened state
                    aniIndex = 0;
                } else if (currentState == CLOSING) {
                    active = false; // Disable drawing
                    GameState.state = returnState; // Return to game
                } else {
                    aniIndex = 0; // Stay on the 1 sprite in Row 1
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (!active || animations == null) return;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        int safeIndex = Math.min(aniIndex, GetSpriteAmount(currentState) - 1);
        g.drawImage(animations[currentState][safeIndex], 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        if (currentState == OPENED) {
            drawUIElements(g);
        }
    }

    private void drawUIElements(Graphics g) {
        //TITLE
        g.setColor(Color.WHITE);
        g.setFont(customFont);
        String title = mode.equals("SAVE") ? "Select a Save Slot" : "Select a Load Slot";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2), 80);

        drawSlot(g, slot1, 1);
        drawSlot(g, slot2, 2);
        drawSlot(g, slot3, 3);

        // DELETE
        if (mode.equals("LOAD")) {
            UI.drawHoverableButton(g, deleteBtn1.x, deleteBtn1.y + 25, "Delete", deleteBtn1.contains(mouseX, mouseY), customFont.deriveFont(30f), Color.RED);
            UI.drawHoverableButton(g, deleteBtn2.x, deleteBtn2.y + 25, "Delete", deleteBtn2.contains(mouseX, mouseY), customFont.deriveFont(30f), Color.RED);
            UI.drawHoverableButton(g, deleteBtn3.x, deleteBtn3.y + 25, "Delete", deleteBtn3.contains(mouseX, mouseY), customFont.deriveFont(30f), Color.RED);
        }

        Color customColor = new Color(60, 52, 16);
        UI.drawHoverableButton(g, btnBack.x + (btnBack.width / 2) - 40, btnBack.y + 35,
                "Back", btnBack.contains(mouseX, mouseY), customFont, customColor);
    }

    private void drawSlot(Graphics g, Rectangle slot, int slotNum) {
        File file = new File("save_slot" + slotNum + ".txt");

        Color customColor = new Color(46, 34, 47);
        g.setColor(customColor);
        g.fillRect(slot.x, slot.y, slot.width, slot.height);
        g.setColor(Color.WHITE);
        g.drawRect(slot.x, slot.y, slot.width, slot.height);

        Font sFont = customFont.deriveFont(25f);
        g.setFont(sFont);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                br.readLine();
                br.readLine();
                String lvlLine = br.readLine();
                int levelNum = Integer.parseInt(lvlLine);
                br.close();

                g.setColor(Color.WHITE);
                g.drawString("Slot " + slotNum + "  —  World: " + levelNum, slot.x + 20, slot.y + 40);

            } catch (IOException e) {
                g.setColor(Color.RED);
                g.drawString("Slot " + slotNum + "  —  Error reading save", slot.x + 20, slot.y + 40);
            }
        } else {
            g.setColor(Color.GRAY);
            g.drawString("Slot " + slotNum + "  —  Empty", slot.x + 20, slot.y + 40);
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (currentState != OPENED) return; // ignore clicks while open

        if (mode.equals("LOAD")) {
            if (deleteBtn1.contains(e.getPoint())) { deleteSlot(1); return; }
            if (deleteBtn2.contains(e.getPoint())) { deleteSlot(2); return; }
            if (deleteBtn3.contains(e.getPoint())) { deleteSlot(3); return; }
        }

        if (slot1.contains(e.getPoint()))       handleSlot(1);
        else if (slot2.contains(e.getPoint()))  handleSlot(2);
        else if (slot3.contains(e.getPoint()))  handleSlot(3);
        else if (btnBack.contains(e.getPoint())) {
            closeMenu(); //closing animation
        }
    }

    private void handleSlot(int slotNum) {
        if (mode.equals("SAVE")) {
            saveToSlot(slotNum);
        } else {
            loadFromSlot(slotNum);
        }
    }

    public void saveToSlot(int slotNum) {
        try {
            FileWriter fw = new FileWriter("save_slot" + slotNum + ".txt");
            fw.write(game.getPlayer().getHitbox().x + "\n");
            fw.write(game.getPlayer().getHitbox().y + "\n");
            fw.write(game.getLevelHandler().getCurrentLevelNum() + "\n");
            fw.write(game.getPlayer().getCharacterData().name() + "\n");
            fw.close();

            game.getPlayer().setSpawn(game.getPlayer().getHitbox().x, game.getPlayer().getHitbox().y);
            game.getUi().setSaveMessage("Game Progress Saved!");
            GameState.state = GameState.PLAYING;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromSlot(int slotNum) {
        File file = new File("save_slot" + slotNum + ".txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            float x = Float.parseFloat(br.readLine().trim());
            float y = Float.parseFloat(br.readLine().trim());
            int levelNum = Integer.parseInt(br.readLine().trim());
            String charName = br.readLine().trim();
            PlayerCharacter character = PlayerCharacter.valueOf(charName);

            // load world assets
            game.getLevelHandler().loadLevel(levelNum);
            Level currentLevel = game.getLevelHandler().getCurrentLevel();

            if (game.getPlaying() != null) {
                game.getPlaying().getEnemyManager().loadEnemies(currentLevel);
                game.getPlaying().updateLevelOffsets();
            }

            // place player to saved location.
            game.initPlayerCharacter(character, levelNum);
            game.getPlayer().setSpawn(x, y);
            game.getPlayer().getHitbox().x = x;
            game.getPlayer().getHitbox().y = y;
            game.getPlayer().updateLevelData(game.getLevelHandler().getCurrentLevel().getLevelData());

            GameState.state = GameState.PLAYING;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteSlot(int slotNum) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete Slot " + slotNum + "?\nThis cannot be undone.",
                "Delete Save",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            File file = new File("save_slot" + slotNum + ".txt");
            if (file.delete()) {
                System.out.println("Slot " + slotNum + " deleted successfully.");
            } else {
                System.out.println("Failed to delete Slot " + slotNum);
            }
        }
    }

    //MOUSE
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    //HELPER
    public void setMode(String mode) {
        this.returnState = mode.equals("LOAD") ? GameState.MENU : GameState.PLAYING;
        this.mode = mode;
        this.active = true;
        this.currentState = OPENING;
        this.aniIndex = 0;
        this.aniTick = 0;
    }

    private void closeMenu() {
        this.currentState = CLOSING;
        this.aniIndex = 0;
        this.aniTick = 0;
    }
}