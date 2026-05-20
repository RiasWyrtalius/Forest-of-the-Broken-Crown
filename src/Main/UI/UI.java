package Main.UI;

import Entities.PlayerCharacter;
import Main.Core.Game;
import Main.GameStates.SlotScreen;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Main.Core.Game.GAME_WIDTH;
import static java.awt.Font.PLAIN;
import static java.awt.Font.getFont;

public class UI {
    private Game game;
    private BufferedImage[] heartSprite = new BufferedImage[2]; // 0 = full, 1 = empty
    private BufferedImage[] manaSprite = new BufferedImage[2];
    private int slotX = (GAME_WIDTH / 2) - 300;
    private int slotY = 200;
    private Font customFont;
    private Font vcrFont;
    private String tooltipText = null;

    private String saveMessage   = "";
    private String bossDefeatMsg = "";
    private long msgTimer = 0;
    private final long MESSAGE_DURATION = 2000; // this shows the message for 2 seconds

    private String worldNameMsg = "";
    private long worldMsgTimer = 0;
    private final long WORLD_MSG_DURATION = 3500;

    private Rectangle takeCrownBtn, throwCrownBtn;
    private boolean decisionActive = false;

    public UI(Game game) {
        this.game = game;
        loadHeartImages();
        loadManaImages();
        customFont  = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(PLAIN, 40);
        vcrFont     = LoadSave.getFont("Font/VCR.ttf").deriveFont(Font.PLAIN, 30f);

        takeCrownBtn = new Rectangle(Game.GAME_WIDTH / 2 - 250, 400, 200, 50);
        throwCrownBtn = new Rectangle(Game.GAME_WIDTH / 2 + 50, 400, 200, 50);
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
        drawSpeedrunTimer(g);
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

        //border
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(x + 15, y + 15, boxW, boxH);

        //desc
        g.setColor(Color.WHITE);
        drawWrappedString(g, description, x + 15 + padding, y + 15 + padding + fm.getAscent(), boxW - padding);
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

    public void drawCenteredText(Graphics g, String text, int y, float size) {
        g.setFont(vcrFont.deriveFont(size));
        FontMetrics fm = g.getFontMetrics();
        int x = (Game.GAME_WIDTH / 2) - (fm.stringWidth(text) / 2);

        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(text, x + 2, y + 2);

        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
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

    public void drawSpeedrunTimer(Graphics g) {
        if (!Main.GameStates.OptionsScreen.speedrunTimer) return;

        String timeStr = formatTime(game.getSpeedrunTicks());
        g.setFont(vcrFont);

        //center
        FontMetrics fm = g.getFontMetrics();
        int x = (Game.GAME_WIDTH / 2) - (fm.stringWidth(timeStr) / 2);
        int y = 40; // Top of the screen

        //shadow
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(timeStr, x + 2, y + 2);

        //text
        g.setColor(Color.WHITE);
        g.drawString(timeStr, x, y);
    }

    private String formatTime(long ticks) {
        long totalSeconds = ticks / 200;      // 200 UPS = 1 second
        long centiseconds = (ticks % 200) / 2; // Convert remaining ticks to 0-99
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds);
        } else {
            return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds);
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void setSaveMessage(String msg) {
        this.saveMessage = msg;
        this.msgTimer = System.currentTimeMillis();
    }

    public void setBossMsg(String msg) {
        this.bossDefeatMsg = msg;
        this.msgTimer = System.currentTimeMillis();
    }

    public String getFormattedTime() {
        return formatTime(game.getSpeedrunTicks());
    }

    public void drawBossDecision(Graphics g) {
        // Semi-transparent background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setFont(vcrFont.deriveFont(Font.BOLD, 42f));
        g.setColor(Color.WHITE);

        String title = "THE CROWN LIES BEFORE YOU...";
        int titleX = getXPosForCenteredText(title, g);
        g.drawString(title, titleX, 300);

        drawDecisionButton(g, takeCrownBtn, "CLAIM IT", new Color(150, 0, 0));
        drawDecisionButton(g, throwCrownBtn, "DESTROY IT", new Color(0, 150, 100));
    }

    private void drawDecisionButton(Graphics g, Rectangle r, String text, Color theme) {
        Graphics2D g2 = (Graphics2D) g;

        // background btn
        g2.setColor(new Color(46, 34, 46));
        g2.fillRect(r.x, r.y, r.width, r.height);

        // thick border
        g2.setColor(theme);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(r.x, r.y, r.width, r.height);

        // reset stroke
        g2.setStroke(new BasicStroke(1));

        // text
        g2.setFont(vcrFont.deriveFont(22f));
        g2.setColor(Color.WHITE);

        // center text
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int textX = r.x + (r.width / 2) - (textWidth / 2);
        int textY = r.y + (r.height / 2) + 10; // Simple vertical nudge

        g2.drawString(text, textX, textY);
    }

    public void drawWorldName(Graphics g) {
        if (!worldNameMsg.isEmpty()) {
            long elapsed = System.currentTimeMillis() - worldMsgTimer;

            if (elapsed < WORLD_MSG_DURATION) {
                int alpha = getAlpha(elapsed);

                g.setFont(vcrFont.deriveFont(Font.BOLD, 50f));
                FontMetrics fm = g.getFontMetrics();

                int msgX = (Game.GAME_WIDTH / 2) - (fm.stringWidth(worldNameMsg) / 2);
                int msgY = 120;
                int shadowAlpha = (int) (180 * (alpha / 255.0f));

                // text shadow
                g.setColor(new Color(0, 0, 0, shadowAlpha));
                g.drawString(worldNameMsg, msgX + 3, msgY + 3);

                // main text
                g.setColor(new Color(255, 255, 255, alpha));
                g.drawString(worldNameMsg, msgX, msgY);

            } else {
                worldNameMsg = ""; // reset timer
            }
        }
    }

    private int getAlpha(long elapsed) {
        int alpha = 255;
        int fadeStartDelay = 2000; // 2 sec

        // if past 2 sec, fade
        if (elapsed > fadeStartDelay) {
            float fadeProgress = (float) (elapsed - fadeStartDelay) / (WORLD_MSG_DURATION - fadeStartDelay);
            alpha = 255 - (int) (255 * fadeProgress);
            //safety check
            if (alpha < 0) alpha = 0;
        }
        return alpha;
    }

    public void setWorldNameMsg(String msg) {
        this.worldNameMsg = msg;
        this.worldMsgTimer = System.currentTimeMillis();
    }

    public int getXPosForCenteredText(String text, Graphics g) {
        int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        return Game.GAME_WIDTH / 2 - length / 2;
    }

    public Rectangle getTakeCrownBtn() { return takeCrownBtn; }
    public Rectangle getThrowCrownBtn() { return throwCrownBtn; }
}
