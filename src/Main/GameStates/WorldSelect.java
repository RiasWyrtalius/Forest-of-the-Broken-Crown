package Main.GameStates;

import Audio.AudioPlayer;
import Main.Core.Game;
import Main.GameState;
import Entities.PlayerCharacter;
import Main.UI.UI;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;

import static java.awt.Color.WHITE;

public class WorldSelect {

    private Game game;
    private Font customFont;
    private String selectedCharKey = "";
    private PlayerCharacter selectedCharacter;
    private long entryTime = 0;

    // 3 world buttons, centered on screen
    private Rectangle world1Btn = new Rectangle(Game.GAME_WIDTH / 2 - 70, 225, 160, 50);
    private Rectangle world2Btn = new Rectangle(Game.GAME_WIDTH / 2 - 70, 305, 160, 50);
    private Rectangle world3Btn = new Rectangle(Game.GAME_WIDTH / 2 - 70, 375, 160, 50);
    private Rectangle backBtn   = new Rectangle(Game.GAME_WIDTH / 2 - 50, 525, 120, 50);

    private Rectangle hoveredBtn = null;
    private Rectangle lastHovered = null;

    public WorldSelect(Game game) {
        this.game = game;
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
    }

    public void onEnter() {
        entryTime = System.currentTimeMillis();
    }

    public void setSelectedCharacter(PlayerCharacter character) {
        this.selectedCharacter = character;
    }

    // Called by CharacterSelect before switching to this screen
    public void setCharacterKey(String key) {
        this.selectedCharKey = key;
    }

    public void update() {}

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setFont(customFont);
        g.setColor(Color.YELLOW);

        // Title
        String title = "Select a World";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.GAME_WIDTH / 2) - (titleW / 2), 150);

        // Buttons
        UI.drawHoverableButton(g, world1Btn.x, world1Btn.y + 35, "World 1", hoveredBtn == world1Btn, customFont, WHITE);
        UI.drawHoverableButton(g, world2Btn.x, world2Btn.y + 35, "World 2", hoveredBtn == world2Btn, customFont, WHITE);
        UI.drawHoverableButton(g, world3Btn.x, world3Btn.y + 35, "World 3", hoveredBtn == world3Btn, customFont, WHITE);
        UI.drawHoverableButton(g, backBtn.x,   backBtn.y + 35,   "Back",    hoveredBtn == backBtn,   customFont, WHITE);
    }

    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        if (System.currentTimeMillis() - entryTime < 300) return;

        if (world1Btn.contains(p)) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            game.initPlayerCharacter(selectedCharacter, 1);
            game.cancelFade();
            game.getCutsceneState().startCutscene(selectedCharKey, GameState.PLAYING);
            GameState.state = GameState.CUTSCENE;

        } else if (world2Btn.contains(p)) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            // World 2 → skip cutscene, jump to level 3 (first level of world 2)
            game.initPlayerCharacter(selectedCharacter, 3);

        } else if (world3Btn.contains(p)) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            // World 3 → skip cutscene, jump to level 5 (first level of world 3)
            game.initPlayerCharacter(selectedCharacter, 5);

        } else if (backBtn.contains(p)) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            GameState.state = GameState.CHARACTER_SELECT;
        }
    }

    public void mouseMoved(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());
        int x = e.getX();
        int y = e.getY();
        hoveredBtn = null;

        if (world1Btn.contains(p))      hoveredBtn = world1Btn;
        else if (world2Btn.contains(p)) hoveredBtn = world2Btn;
        else if (world3Btn.contains(p)) hoveredBtn = world3Btn;
        else if (backBtn.contains(p))   hoveredBtn = backBtn;

        if (hoveredBtn != null && hoveredBtn != lastHovered) {
            game.getAudioPlayer().playEffect(AudioPlayer.HOVER);
        }
        lastHovered = hoveredBtn;
    }
}