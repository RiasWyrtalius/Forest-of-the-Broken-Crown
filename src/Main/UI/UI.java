package Main.UI;

import Entities.PlayerCharacter;
import Main.Core.Game;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Font.PLAIN;

public class UI {
    private Game game;
    private BufferedImage[] heartSprite = new BufferedImage[2]; // 0 = full, 1 = empty
    private BufferedImage[] manaSprite = new BufferedImage[2];
    private int slotX = (Game.GAME_WIDTH / 2) - 300;
    private int slotY = 200;
    private Font customFont;

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
        for (int i = 0; i < maxMana; i++) {
            int x = (startX + 7) + (i * (manaSize - spacing + 2));

            if (i < currentMana) {
                g2d.drawImage(manaSprite[0], x, manaY, manaSize, manaSize, null);
            } else if (i == currentMana) {
                float pulse = (float) (Math.sin(System.currentTimeMillis() / 200) * 0.35 + 0.65);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
                g2d.drawImage(manaSprite[0], x, manaY, manaSize, manaSize, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
            } else {
                g2d.drawImage(manaSprite[1], x, manaY, manaSize, manaSize, null);
            }
        }
    }

    public void drawCharacterStats(Graphics g, PlayerCharacter selectedHero, int mouseX, int mouseY) {
        drawHeroLore(g, selectedHero);
        drawVitalStats(g, selectedHero);
        drawPassiveSection(g, selectedHero, mouseX, mouseY);
        //TODO: drawAbilitySection(g, selectedHero, mouseX, mouseY);
    }

    private void drawHeroLore(Graphics g, PlayerCharacter hero) {
        int descWidth = 650;
        int descX = (Game.GAME_WIDTH / 2) - (descWidth / 2);

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
        int totalWidth = labelWidth + g.getFontMetrics().stringWidth(passiveName);
        if (isMouseOver(mouseX, mouseY, labelX, labelY, totalWidth, g.getFontMetrics().getHeight())) {
            drawPassiveTooltip(g, mouseX, mouseY, hero.getPassive().getDescription());
        }
    }

    private void drawPassiveTooltip(Graphics g, int x, int y, String description) {
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
        return mouseX >= x && mouseX <= x + width && mouseY >= y - height && mouseY <= y;
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
}
