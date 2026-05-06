package Main.GameStates;

import Audio.AudioPlayer;
import Entities.PlayerCharacter;
import Main.Core.Game;
import Main.GameState;
import Main.UI.UI;
import Utils.HelpMethods;
import Utils.LoadSave;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static Main.GameState.CUTSCENE;
import static Main.GameState.PLAYING;
import static Utils.LoadSave.CSelection_Atlas;

public class CharacterSelect {

    private Font customFont;
    private Game game;
    private UI ui;
    private PlayerCharacter[] characters = PlayerCharacter.values();
    private int currentIndex = 0;

    // Character Animation
    private int aniTick, aniIndex, aniSpeed = 25;

    // Background Transition Animation
    private BufferedImage[] bgSprites;
    private int bgAniTick, bgAniIndex;
    private int bgAniSpeed = 10;
    private boolean transitioning = false;
    private int mouthDirection = 1;
    private int targetDir = 0;

    private Rectangle hoveredBtn = null;
    private Rectangle lastHovered = null;

    private int mouseX, mouseY;

    // UI Elements
    private Rectangle leftArrow = new Rectangle(150, 300, 60, 60);
    private Rectangle rightArrow = new Rectangle(Game.GAME_WIDTH - 210, 300, 60, 60);
    private Rectangle selectBtn = new Rectangle((Game.GAME_WIDTH / 2) - 100, 550, 200, 55);

    public CharacterSelect(Game game) {
        this.game = game;
        loadBackground();
        customFont = LoadSave.getFont("Font/GrapeSoda.ttf").deriveFont(48f);
    }

    private void loadBackground() {
        BufferedImage img = LoadSave.getSpriteAtlas(CSelection_Atlas);

        int frameWidth = 240;
        int frameHeight = 135;

        int totalFramesInFile = img.getWidth() / frameWidth;
        int framesToLoad = Math.min(9, totalFramesInFile);
        bgSprites = new BufferedImage[framesToLoad];

        for (int i = 0; i < framesToLoad; i++) {
            bgSprites[i] = img.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }

        bgAniIndex = bgSprites.length - 1;
    }

    public void update() {
        if (!transitioning) {
            updateCharacterAnimation();
        } else {
            updateMouthAnimation();
        }
    }

    private void updateMouthAnimation() {
        bgAniTick++;
        if (bgAniTick >= bgAniSpeed) {
            bgAniTick = 0;
            bgAniIndex += mouthDirection;

            if (bgAniIndex == 0 && mouthDirection == -1) {
                applySelectionChange();
                mouthDirection = 1;
            } else if (bgAniIndex == bgSprites.length - 1 && mouthDirection == 1) {
                transitioning = false;
            }
        }
    }

    private void updateCharacterAnimation() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= characters[currentIndex].getSpriteAmountIDLE()) {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        PlayerCharacter currentHero = characters[currentIndex];
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g.drawImage(bgSprites[bgAniIndex], 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        drawUI(g);

        if (bgAniIndex >= bgSprites.length - 1) {
            drawCharacter(g);
            game.getUi().drawCharacterStats(g, currentHero, mouseX, mouseY);
        }
    }

    private void drawCharacter(Graphics g) {
        PlayerCharacter selected = characters[currentIndex];
        BufferedImage img = game.getCharacterAtlas(selected);

        int spriteSize = Game.SPRITE_DEFAULT_SIZE;
        float charScale = Game.SCALE * 4f;
        int drawSize = (int) (spriteSize * charScale);

        int x = (Game.GAME_WIDTH / 2) - (drawSize / 2);
        int y = (Game.GAME_HEIGHT / 2) - (drawSize / 2);

        int cropX = aniIndex * spriteSize;
        int cropY = selected.getRowIDLE() * spriteSize;

        g.drawImage(
                img.getSubimage(cropX, cropY, spriteSize, spriteSize),
                x, y, drawSize, drawSize, null
        );

        g.setColor(Color.YELLOW);
        g.setFont(customFont);
        String name = selected.name();
        int nameWidth = g.getFontMetrics().stringWidth(name);
        g.drawString(name, (Game.GAME_WIDTH / 2) - (nameWidth / 2), y);
    }

    private void drawUI(Graphics g) {
        boolean isLeftHovered = (hoveredBtn == leftArrow);
        boolean isRightHovered = (hoveredBtn == rightArrow);
        boolean isSelectHovered = (hoveredBtn == selectBtn);

        UI.drawHoverableButton(g, leftArrow.x + 20, leftArrow.y + 45, "<", isLeftHovered, customFont);
        UI.drawHoverableButton(g, rightArrow.x + 20, rightArrow.y + 45, ">", isRightHovered, customFont);
        UI.drawHoverableButton(g, selectBtn.x + 50, selectBtn.y + 40, "SELECT", isSelectHovered, customFont);
    }

    private void startTransition(int direction) {
        transitioning = true;
        mouthDirection = -1;
        targetDir = direction;
    }

    private void applySelectionChange() {
        currentIndex += targetDir;
        if (currentIndex < 0) currentIndex = characters.length - 1;
        else if (currentIndex >= characters.length) currentIndex = 0;
        aniIndex = 0;
    }

    //INPUTS MOUSE & KEYBOARD
    public void mouseClicked(MouseEvent e) {
        if (transitioning) return;

        if (leftArrow.contains(e.getPoint())) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            startTransition(-1);
        } else if (rightArrow.contains(e.getPoint())) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            startTransition(1);
        } else if (selectBtn.contains(e.getPoint())) {
            game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
            game.initPlayerCharacter(characters[currentIndex], 1);
            game.cancelFade();
            String cutsceneKey = "";
            switch (characters[currentIndex]) {
                case EMBJORN -> cutsceneKey = "EMBJORN";
                case KAELTHORN -> cutsceneKey = "KAELTHORN";
                case SYLVARA -> cutsceneKey = "SYLVARA";
            }

            game.getCutsceneState().startCutscene(cutsceneKey, PLAYING);
            GameState.state = CUTSCENE;
        }
    }

    public void mouseMoved(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        Point mousePos = new Point(x, y);

        if (leftArrow.contains(mousePos)) hoveredBtn = leftArrow;
        else if (rightArrow.contains(mousePos)) hoveredBtn = rightArrow;
        else if (selectBtn.contains(mousePos)) hoveredBtn = selectBtn;
        else hoveredBtn = null;

        if (hoveredBtn != null && hoveredBtn != lastHovered) {
            game.getAudioPlayer().playEffect(AudioPlayer.HOVER);
        }
        lastHovered = hoveredBtn;
    }

    public void keyPressed(KeyEvent e) {
        if (transitioning) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
                startTransition(-1);
                break;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
                startTransition(1);
                break;
            case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE:
                game.getAudioPlayer().playEffect(AudioPlayer.CLICK);
                game.initPlayerCharacter(characters[currentIndex], 1);
                game.cancelFade();
                String key = "";
                switch (characters[currentIndex]) {
                    case EMBJORN -> key = "EMBJORN";
                    case KAELTHORN -> key = "KAELTHORN";
                    case SYLVARA -> key = "SYLVARA";
                }
                game.getCutsceneState().startCutscene(key, PLAYING);
                GameState.state = CUTSCENE;
                break;
        }
    }
}