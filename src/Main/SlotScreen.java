package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

import javax.swing.JOptionPane;   // ← Added for confirmation dialog

public class SlotScreen {

    private Game game;

    private String mode = "SAVE";


    private Rectangle slot1 = new Rectangle(424, 150, 400, 70);
    private Rectangle slot2 = new Rectangle(424, 260, 400, 70);
    private Rectangle slot3 = new Rectangle(424, 370, 400, 70);
    private Rectangle btnBack = new Rectangle(424, 480, 400, 50);


    private Rectangle deleteBtn1 = new Rectangle(840, 165, 80, 40);
    private Rectangle deleteBtn2 = new Rectangle(840, 275, 80, 40);
    private Rectangle deleteBtn3 = new Rectangle(840, 385, 80, 40);

    public SlotScreen(Game game) {
        this.game = game;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void draw(Graphics g) {
        // Dim background
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
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
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Back", btnBack.x + (btnBack.width / 2) - 20, btnBack.y + 30);
    }

    private void drawSlot(Graphics g, Rectangle slot, int slotNum) {
        File file = new File("save_slot" + slotNum + ".txt");

        g.setColor(Color.DARK_GRAY);
        g.fillRect(slot.x, slot.y, slot.width, slot.height);
        g.setColor(Color.WHITE);
        g.drawRect(slot.x, slot.y, slot.width, slot.height);

        g.setFont(new Font("Arial", Font.PLAIN, 18));

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                float x = Float.parseFloat(br.readLine());
                float y = Float.parseFloat(br.readLine());
                int levelNum = Integer.parseInt(br.readLine());
                br.close();

                g.setColor(Color.WHITE);
                g.drawString("Slot " + slotNum + "  —  Level: " + levelNum
                                + "  |  x: " + (int)x + "  y: " + (int)y,
                        slot.x + 20, slot.y + 40);

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
        g.setColor(new Color(180, 0, 0));           // Reddish color
        g.fillRect(btn.x, btn.y, btn.width, btn.height);
        g.setColor(Color.WHITE);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Delete", btn.x + 12, btn.y + 26);
    }

    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        // Check Delete buttons first (only in LOAD mode)
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

        // Check slot clicks
        if (slot1.contains(e.getPoint())) {
            handleSlot(1);
        }
        else if (slot2.contains(e.getPoint())) {
            handleSlot(2);
        }
        else if (slot3.contains(e.getPoint())) {
            handleSlot(3);
        }
        // Back button - now properly checked
        else if (btnBack.contains(e.getPoint())) {
            if (mode.equals("SAVE")) {
                GameState.state = GameState.PLAYING;
            } else {
                GameState.state = GameState.MENU;   // Go back to Main Menu
            }
        }
    }

    private void handleSlot(int slotNum) {
        if (mode.equals("SAVE")) {
            saveToSlot(slotNum);
        } else {
            loadFromSlot(slotNum);
        }
    }

    private void saveToSlot(int slotNum) {
        try {
            FileWriter fw = new FileWriter("save_slot" + slotNum + ".txt");
            fw.write(game.getPlayer().getHitbox().x + "\n");
            fw.write(game.getPlayer().getHitbox().y + "\n");
            fw.write(game.getLevelHandler().getCurrentLevelNum() + "\n");
            fw.close();

            game.setSaveMessage("Saved Game!");
            GameState.state = GameState.PLAYING;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromSlot(int slotNum) {
        File file = new File("save_slot" + slotNum + ".txt");
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            float x = Float.parseFloat(br.readLine());
            float y = Float.parseFloat(br.readLine());
            int levelNum = Integer.parseInt(br.readLine());
            br.close();

            game.getPlayer().getHitbox().x = x;
            game.getPlayer().getHitbox().y = y;
            game.getLevelHandler().loadLevel(levelNum);

            GameState.state = GameState.PLAYING;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // NEW: Delete slot with confirmation
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
}