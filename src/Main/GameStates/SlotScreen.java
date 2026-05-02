package Main.GameStates;

import Levels.Level;
import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.JOptionPane;   // Added for confirmation dialog

public class SlotScreen {
    private Font customFont;
    private Game game;
    private String mode = "SAVE";
    private Font saveMsgFont;

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
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(customFont);
        String title = mode.equals("SAVE") ? "Select a Save Slot" : "Select a Load Slot";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (Game.GAME_WIDTH / 2) - (fm.stringWidth(title) / 2), 100);
        drawSlot(g, slot1, 1);
        drawSlot(g, slot2, 2);
        drawSlot(g, slot3, 3);

        if (mode.equals("LOAD")) {
            drawDeleteButton(g, deleteBtn1, 1);
            drawDeleteButton(g, deleteBtn2, 2);
            drawDeleteButton(g, deleteBtn3, 3);
        }

        g.setColor(Color.DARK_GRAY);
        g.fillRect(btnBack.x, btnBack.y, btnBack.width, btnBack.height);
        g.setColor(Color.WHITE);
        g.drawRect(btnBack.x, btnBack.y, btnBack.width, btnBack.height);
        g.setFont(customFont);
        g.drawString("Back", btnBack.x + (btnBack.width / 2) - 40, btnBack.y + 35);
    }

    private void drawSlot(Graphics g, Rectangle slot, int slotNum) {
        File file = new File("save_slot" + slotNum + ".txt");

        g.setColor(Color.DARK_GRAY);
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


    private void drawDeleteButton(Graphics g, Rectangle btn, int slotNum) {
        g.setColor(new Color(180, 0, 0));
        g.fillRect(btn.x, btn.y, btn.width, btn.height);
        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);

        Font sFont = customFont.deriveFont(20f);
        g.setFont(sFont);
        g.drawString("Delete", btn.x + 12, btn.y + 26);
    }

    public void mouseClicked(MouseEvent e) {
        if (mode.equals("LOAD")) {
            if (deleteBtn1.contains(e.getPoint())) {
                deleteSlot(1);
                return;
            }
            if (deleteBtn2.contains(e.getPoint())) {
                deleteSlot(2);
                return;
            }
            if (deleteBtn3.contains(e.getPoint())) {
                deleteSlot(3);
                return;
            }
        }


        if (slot1.contains(e.getPoint()))       handleSlot(1);
        else if (slot2.contains(e.getPoint()))  handleSlot(2);
        else if (slot3.contains(e.getPoint()))  handleSlot(3);

        else if (btnBack.contains(e.getPoint())) {
            GameState.state = GameState.PLAYING;
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
            fw.close();

            game.setSaveMessage("Game Progress Saved!");
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

            // load world assets
            game.getLevelHandler().loadLevel(levelNum);
            Level currentLevel = game.getLevelHandler().getCurrentLevel();

            if (game.getPlaying() != null) {
                game.getPlaying().getEnemyManager().loadEnemies(currentLevel);
                game.getPlaying().updateLevelOffsets();
            }

            // place player to saved location.
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

    public Font getSaveMsgFont() { return saveMsgFont; }
}