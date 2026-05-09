package Main.UI;

import Entities.PlayerCharacter;
import Main.Core.Game;
import Main.GameStates.SlotScreen;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Main.Core.Game.GAME_WIDTH;
import static java.awt.Font.PLAIN;

public class UI {
    private Game game;
    private BufferedImage[] heartSprite = new BufferedImage[2]; // 0 = full, 1 = empty
    private BufferedImage[] manaSprite = new BufferedImage[2];
    private int slotX = (GAME_WIDTH / 2) - 300;
    private int slotY = 200;
    private Font customFont;
    private String tooltipText = null;

    private String saveMessage   = "";
    private String bossDefeatMsg = "";
    private long msgTimer = 0;
    private final long MESSAGE_DURATION = 2000; // this shows the message for 2 seconds

    public UI(Game game) {
        this.game = game;
        loadHeartImages();
        loadManaImages();
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(PLAIN, 40);
    }

    private void loadHeartImages() {
        BufferedImage spriteSheet = LoadSave.getSpriteAtlas(LoadSave.Hearts_Atlas);

        int width = spriteSheet.getWidth() / 2;
        int height = spriteSheet.getHeight();

        heartSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
        heartSprite[1] = spriteSheet.getSubimage(width, 0, width, height);
    }

    private void loadManaImages() {
        BufferedImage spriteSheet = LoadSave.getSpriteAtlas(LoadSave.Mana_Atlas);

        int width = spriteSheet.getWidth() / 2;
        int height = spriteSheet.getHeight();

        manaSprite[0] = spriteSheet.getSubimage(0, 0, width, height);
        manaSprite[1] = spriteSheet.getSubimage(width, 0, width, height);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int maxLife = game.getPlayer().getMaxLife();
        int currentLife = game.getPlayer().getLife();

        int maxMana = game.getPlayer().getMaxManaBottles();
        int currentMana = game.getPlayer().getManaBottles();

        int startX = 20;
        int heartY = 20;
        int manaY = 65;

        int heartSize = Game.TILES_SIZE;
        int manaSize = (int) (Game.TILES_SIZE * 0.7);
        int spacing = 8;

        //Hearts
        for (int i = 0; i < maxLife; i++) {
            int x = startX + (i * (heartSize - spacing));
            int imgIndex = (i < currentLife) ? 0 : 1;
            g.drawImage(heartSprite[imgIndex], x, heartY, heartSize, heartSize, null);
        }

        //Mana Bottles
        Composite originalComposite = g2d.getComposite();

        for (int i = 0; i < maxMana; i++) {
            int x = (startX + 7) + (i * (manaSize - spacing + 2));

            if (i < currentMana) {
                g2d.drawImage(manaSprite[0], x, manaY, manaSize, manaSize, null);
            } else if (i == currentMana) {
                float pulse = (float) (Math.sin(System.currentTimeMillis() / 200) * 0.35 + 0.65);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
                g2d.drawImage(manaSprite[0], x, manaY, manaSize, manaSize, null);

                //reset here so it doesn't apply to the other parts of the UI
                g2d.setComposite(originalComposite);
            } else {
                g2d.drawImage(manaSprite[1], x, manaY, manaSize, manaSize, null);
            }
        }

        g2d.setComposite(originalComposite);
    }

    public void drawCharacterStats(Graphics g, PlayerCharacter selectedHero, int mouseX, int mouseY) {
        tooltipText = null;

        drawHeroLore(g, selectedHero);
        drawVitalStats(g, selectedHero);
        drawPassiveSection(g, selectedHero, mouseX, mouseY);
        drawAbilitySection(g, selectedHero, mouseX, mouseY);

        if (tooltipText != null) {
            drawTooltip(g, mouseX, mouseY, tooltipText);
        }
    }

    private void drawHeroLore(Graphics g, PlayerCharacter hero) {
        int descWidth = 650;
        int descX = (GAME_WIDTH / 2) - (descWidth / 2);

        g.setFont(customFont.deriveFont(Font.PLAIN, 20));
        g.setColor(new Color(142, 142, 142));
        drawWrappedString(g, hero.getDescription(), descX + 20, slotY + 300, descWidth - 40);
    }

    private void drawVitalStats(Graphics g, PlayerCharacter hero) {
        g.setFont(customFont);

        // HP
        g.drawImage(heartSprite[0], slotX, slotY + 80, 64, 64, null);
        g.setColor(Color.RED);
        g.drawString(String.valueOf(hero.getLives()), slotX + 70, slotY + 120);

        // MANA
        g.drawImage(manaSprite[0], slotX, slotY + 150, 64, 64, null);
        g.setColor(new Color(0, 150, 255));
        g.drawString(String.valueOf(hero.getMana()), slotX + 70, slotY + 190);
    }

    private void drawPassiveSection(Graphics g, PlayerCharacter hero, int mouseX, int mouseY) {
        g.setFont(customFont.deriveFont(Font.PLAIN, 25));
        int labelX = slotX + 500;
        int labelY = slotY + 100;

        //label
        g.setColor(new Color(170, 168, 168));
        String label = "Passive: ";
        g.drawString(label, labelX, labelY);

        //name
        int labelWidth = g.getFontMetrics().stringWidth(label);
        g.setColor(Color.YELLOW);
        String passiveName = hero.getPassive().getName();
        g.drawString(passiveName, labelX + labelWidth, labelY);

        //tooltip check
        int height = g.getFontMetrics().getHeight();
        int nameWidth = g.getFontMetrics().stringWidth(passiveName);
        int textTop = labelY - height + 10; // Calculate the top of the letters

        if (isMouseOver(mouseX, mouseY, labelX + labelWidth, textTop, nameWidth, height)) {
            tooltipText = hero.getPassive().getDescription();
        }
    }

    private void drawAbilitySection(Graphics g, PlayerCharacter hero, int mouseX, int mouseY) {
        g.setFont(customFont.deriveFont(Font.PLAIN, 25));
        int labelX = slotX + 500;
        int labelY = slotY + 180;

        //label
        g.setColor(new Color(170, 168, 168));
        String label = "Skill: ";
        g.drawString(label, labelX, labelY);

        //SKILL NAME
        int labelWidth = g.getFontMetrics().stringWidth(label);
        g.setColor(new Color(0, 255, 150));

        String skillName = hero.getSkill(game.getPlayer()).getName();
        g.drawString(skillName, labelX + labelWidth, labelY);

        int height = g.getFontMetrics().getHeight();
        int nameWidth = g.getFontMetrics().stringWidth(skillName);
        int textTop = labelY - height + 10; // Same calculation

        if (isMouseOver(mouseX, mouseY, labelX + labelWidth, textTop, nameWidth, height)) {
            tooltipText = hero.getSkill(game.getPlayer()).getSkillDescription();
        }
    }

    private void drawTooltip(Graphics g, int x, int y, String description) {
        g.setFont(customFont.deriveFont(Font.PLAIN, 18));
        FontMetrics fm = g.getFontMetrics();

        int padding = 10;
        int maxWidth = 300;

        //tooltip dimensions
        int boxW = Math.min(fm.stringWidth(description) + (padding * 2), maxWidth);
        int boxH = (fm.stringWidth(description) / (maxWidth - padding * 2) + 1) * fm.getHeight() + (padding * 2);

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(x + 15, y + 15, boxW, boxH);

        //TODO: replace with graphics
        //border
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(x + 15, y + 15, boxW, boxH);

        //desc
        g.setColor(Color.WHITE);
        drawWrappedString(g, description, x + 15 + padding, y + 15 + padding + fm.getAscent(), boxW - padding);
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void drawWrappedString(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = fm.getHeight();
        int currentY = y;

        for (String word : words) {
            if (fm.stringWidth(line + word) < maxWidth) {
                line.append(word).append(" ");
            } else {
                // Draw current line and start a new one
                g.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word + " ");
                currentY += lineHeight;
            }
        }
        g.drawString(line.toString(), x, currentY);
    }

    public void drawSaveMessage(Graphics g) {
        if (!saveMessage.isEmpty()) {
            long elapsed = System.currentTimeMillis() - msgTimer;
            if (elapsed < MESSAGE_DURATION) {
                g.setFont(customFont.deriveFont(Font.PLAIN, 30));
                g.setColor(new Color(208, 229, 7));
                FontMetrics fm = g.getFontMetrics();
                int msgX = (GAME_WIDTH / 2) - (fm.stringWidth(saveMessage) / 2);
                g.setColor(Color.WHITE);
                g.drawString(saveMessage, msgX, 70);
            } else {
                saveMessage = "";
            }
        }
    }

    public static void drawHoverableButton(Graphics g, int x, int y, String label, boolean isHovered, Font font, Color mainColor) {
        g.setFont(font);

        if (isHovered) {
            g.setColor(new Color(255, 255, 255, 50));
            g.drawString(label, x + 2, y + 2);

            g.setColor(Color.YELLOW);
        } else {
            g.setColor(mainColor);
        }

        g.drawString(label, x, y);
    }

    public void drawBossDefeated(Graphics g) {
        if (!bossDefeatMsg.isEmpty()) {
            long elapsed = System.currentTimeMillis() - msgTimer;
            if (elapsed < MESSAGE_DURATION) {
                g.setFont(customFont.deriveFont(Font.PLAIN, 50));
                FontMetrics fm = g.getFontMetrics();
                int msgX = (GAME_WIDTH / 2) - (fm.stringWidth(bossDefeatMsg) / 2);
                int msgY = 150;

                g.setColor(new Color(0, 0, 0, 150));
                g.drawString(bossDefeatMsg, msgX + 2, msgY + 2);

                g.setColor(Color.RED);
                g.drawString(bossDefeatMsg, msgX, msgY);
            } else {
                bossDefeatMsg = "";
            }
        }
    }

    public void setSaveMessage(String msg) {
        this.saveMessage = msg;
        this.msgTimer = System.currentTimeMillis();
    }

    public void setBossMsg(String msg) {
        this.bossDefeatMsg = msg;
        this.msgTimer = System.currentTimeMillis();
    }
}
