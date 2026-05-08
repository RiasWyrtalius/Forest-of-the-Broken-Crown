package Main.GameStates;

import Main.Core.Game;
import Main.GameState;
import Main.UI.UI;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;

import static Audio.AudioPlayer.CLICK;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

public class OptionsScreen {
    private Game game;
    private Font customFont;
    // Slider
    private int sliderX = Game.GAME_WIDTH / 2 - 150;
    private int sliderY = 300;
    private int sliderWidth = 300;
    private int sliderHeight = 10;
    private int handleX;
    private int handleWidth = 20;
    private int handleHeight = 30;
    private boolean draggingSlider = false;

    // Toggles
    public static boolean godMode       = false;
    public static boolean showFPS       = false;
    public static boolean speedrunTimer = false;

    // Keybinding
    private boolean listeningForKey = false;
    private String  rebindingAction = "";
    public static int keyJump  = java.awt.event.KeyEvent.VK_SPACE;
    public static int keyLeft  = java.awt.event.KeyEvent.VK_A;
    public static int keyRight = java.awt.event.KeyEvent.VK_D;
    public static int keySkill = java.awt.event.KeyEvent.VK_E;

    // Back button
    private Rectangle backBtn;
    private boolean isBackHovered = false;
    private int mouseX, mouseY;

    // Toggle button rects
    private Rectangle godModeBtn    = new Rectangle(sliderX, 370, 200, 35);
    private Rectangle showFPSBtn    = new Rectangle(sliderX, 415, 200, 35);
    private Rectangle timerBtn      = new Rectangle(sliderX, 460, 200, 35);
    private Rectangle rebindJumpBtn = new Rectangle(sliderX + 230, 370, 160, 35);
    private Rectangle rebindLeftBtn = new Rectangle(sliderX + 230, 415, 160, 35);
    private Rectangle rebindRightBtn= new Rectangle(sliderX + 230, 460, 160, 35);
    private Rectangle rebindSkillBtn= new Rectangle(sliderX + 230, 505, 160, 35);

    public OptionsScreen(Game game) {
        this.game = game;
        this.customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(24f);
        this.backBtn = new Rectangle(Game.GAME_WIDTH / 2 - 50, 560, 100, 40);
        loadSettings();
        // sync slider handle to loaded volume
        float vol = game.getAudioPlayer().getVolume();
        handleX = sliderX + (int)(vol * sliderWidth);
    }

    public void update() {}

    public void draw(Graphics g) {
        g.setColor(new Color(46, 34, 46));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setFont(customFont);
        g.setColor(Color.WHITE);
        drawCenteredText(g);

        // --- AUDIO SECTION ---
        g.setColor(Color.GRAY);
        g.drawString("AUDIO", sliderX, sliderY - 30);
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(sliderX, sliderY, sliderWidth, sliderHeight, 10, 10);
        g.setColor(new Color(255, 215, 0));
        int filledWidth = handleX - sliderX;
        g.fillRoundRect(sliderX, sliderY, filledWidth, sliderHeight, 10, 10);
        g.setColor(draggingSlider ? Color.WHITE : Color.LIGHT_GRAY);
        g.fillRoundRect(handleX - (handleWidth / 2), sliderY - (handleHeight / 2) + (sliderHeight / 2), handleWidth, handleHeight, 5, 5);
        int volumePercent = (int)(((float) filledWidth / sliderWidth) * 100);
        g.setColor(Color.WHITE);
        g.drawString(volumePercent + "%", sliderX + sliderWidth + 20, sliderY + 10);

        // --- GAMEPLAY SECTION ---
        g.setColor(Color.GRAY);
        g.drawString("GAMEPLAY", sliderX, 355);

        drawToggle(g, godModeBtn,    "God Mode",      godMode);
        drawToggle(g, showFPSBtn,    "Show FPS",      showFPS);
        drawToggle(g, timerBtn,      "Speed Timer",   speedrunTimer);

        // --- KEYBINDINGS SECTION ---
        g.setColor(Color.GRAY);
        g.drawString("KEYBINDS", sliderX + 230, 355);

        drawRebindBtn(g, rebindJumpBtn,  "Jump:  "  + java.awt.event.KeyEvent.getKeyText(keyJump),  "JUMP");
        drawRebindBtn(g, rebindLeftBtn,  "Left:  "  + java.awt.event.KeyEvent.getKeyText(keyLeft),  "LEFT");
        drawRebindBtn(g, rebindRightBtn, "Right: "  + java.awt.event.KeyEvent.getKeyText(keyRight), "RIGHT");
        drawRebindBtn(g, rebindSkillBtn, "Skill: "  + java.awt.event.KeyEvent.getKeyText(keySkill), "SKILL");

        if (listeningForKey) {
            g.setColor(new Color(0,0,0,180));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            g.setColor(Color.YELLOW);
            g.setFont(customFont.deriveFont(30f));
            g.drawString("Press any key to bind \"" + rebindingAction + "\"", 200, Game.GAME_HEIGHT / 2);
            g.setFont(customFont);
        }

        UI.drawHoverableButton(g, backBtn.x, backBtn.y + 25, "BACK", isBackHovered, customFont, WHITE);
    }

    private void drawToggle(Graphics g, Rectangle btn, String label, boolean on) {
        g.setColor(on ? new Color(80, 200, 80) : Color.DARK_GRAY);
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setColor(Color.WHITE);
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.drawString(label + ": " + (on ? "ON" : "OFF"), btn.x + 8, btn.y + 24);
    }

    private void drawRebindBtn(Graphics g, Rectangle btn, String label, String action) {
        boolean listening = listeningForKey && rebindingAction.equals(action);
        g.setColor(listening ? new Color(200, 180, 0) : Color.DARK_GRAY);
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setColor(Color.WHITE);
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.drawString(label, btn.x + 6, btn.y + 24);
    }

    public void saveSettings() {
        try (java.io.FileWriter fw = new java.io.FileWriter("settings.txt")) {
            fw.write(game.getAudioPlayer().getVolume() + "\n");
            fw.write(godMode + "\n");
            fw.write(showFPS + "\n");
            fw.write(speedrunTimer + "\n");
            fw.write(keyJump + "\n");
            fw.write(keyLeft + "\n");
            fw.write(keyRight + "\n");
            fw.write(keySkill + "\n");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadSettings() {
        java.io.File file = new java.io.File("settings.txt");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            float vol = Float.parseFloat(br.readLine().trim());
            game.getAudioPlayer().setVolume(vol);
            godMode       = Boolean.parseBoolean(br.readLine().trim());
            showFPS       = Boolean.parseBoolean(br.readLine().trim());
            speedrunTimer = Boolean.parseBoolean(br.readLine().trim());
            keyJump       = Integer.parseInt(br.readLine().trim());
            keyLeft       = Integer.parseInt(br.readLine().trim());
            keyRight      = Integer.parseInt(br.readLine().trim());
            keySkill      = Integer.parseInt(br.readLine().trim());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void drawCenteredText(Graphics g) {
        Font original = g.getFont();
        g.setFont(original.deriveFont((float) 40.0));
        FontMetrics metrics = g.getFontMetrics();
        int textX = 624 - (metrics.stringWidth("OPTIONS") / 2);
        g.drawString("OPTIONS", textX, 100);
        g.setFont(original);
    }

    // Mouse Stuff
    public void mousePressed(MouseEvent e) {
        int mx = e.getX(), my = e.getY();

        Rectangle handleHitbox = new Rectangle(handleX - (handleWidth/2), sliderY - (handleHeight/2) + (sliderHeight/2), handleWidth, handleHeight);
        Rectangle trackHitbox  = new Rectangle(sliderX, sliderY - 10, sliderWidth, sliderHeight + 20);

        if (handleHitbox.contains(mx, my) || trackHitbox.contains(mx, my)) {
            draggingSlider = true;
            handleX = Math.max(sliderX, Math.min(mx, sliderX + sliderWidth));
            game.getAudioPlayer().setVolume((float)(handleX - sliderX) / sliderWidth);
        } else if (godModeBtn.contains(mx, my))    { godMode       = !godMode; }
        else if (showFPSBtn.contains(mx, my))      { showFPS       = !showFPS; }
        else if (timerBtn.contains(mx, my))        { speedrunTimer = !speedrunTimer; }
        else if (rebindJumpBtn.contains(mx, my))   { listeningForKey = true; rebindingAction = "JUMP"; }
        else if (rebindLeftBtn.contains(mx, my))   { listeningForKey = true; rebindingAction = "LEFT"; }
        else if (rebindRightBtn.contains(mx, my))  { listeningForKey = true; rebindingAction = "RIGHT"; }
        else if (rebindSkillBtn.contains(mx, my))  { listeningForKey = true; rebindingAction = "SKILL"; }
    }

    public void mouseReleased(MouseEvent e) {
        draggingSlider = false;
        if (backBtn.contains(e.getX(), e.getY())) {
            game.getAudioPlayer().playEffect(CLICK);
            saveSettings();
            GameState.state = GameState.MENU;
        }
    }

    public void keyPressed(java.awt.event.KeyEvent e) {
        if (!listeningForKey) return;
        int code = e.getKeyCode();
        switch (rebindingAction) {
            case "JUMP"  -> keyJump  = code;
            case "LEFT"  -> keyLeft  = code;
            case "RIGHT" -> keyRight = code;
            case "SKILL" -> keySkill = code;
        }
        listeningForKey = false;
        rebindingAction = "";
    }

    public void mouseDragged(MouseEvent e) {
        if (draggingSlider) {
            handleX = e.getX();

            if (handleX < sliderX) handleX = sliderX;
            if (handleX > sliderX + sliderWidth) handleX = sliderX + sliderWidth;

            float volume = (float) (handleX - sliderX) / sliderWidth;
            game.getAudioPlayer().setVolume(volume);
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        isBackHovered = backBtn.contains(mouseX, mouseY);
    }
}