package Main.GameStates;

import Entities.PlayerCharacter;
import Main.Core.Game;
import Utils.LoadSave;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static Utils.LoadSave.CSelection_Atlas;

public class CharacterSelect {

    private Game game;
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

    // UI Elements
    private Rectangle leftArrow = new Rectangle(150, 300, 60, 60);
    private Rectangle rightArrow = new Rectangle(Game.GAME_WIDTH - 210, 300, 60, 60);
    private Rectangle selectBtn = new Rectangle((Game.GAME_WIDTH / 2) - 100, 550, 200, 55);

    public CharacterSelect(Game game) {
        this.game = game;
        loadBackground();
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
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g.drawImage(bgSprites[bgAniIndex], 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        if (bgAniIndex >= bgSprites.length - 1) {
            drawCharacter(g);
        }

        drawUI(g);
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

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String name = selected.name();
        int nameWidth = g.getFontMetrics().stringWidth(name);
        g.drawString(name, (Game.GAME_WIDTH / 2) - (nameWidth / 2), y - 20);
    }

    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        g.drawString("<", leftArrow.x + 15, leftArrow.y + 45);
        g.drawString(">", rightArrow.x + 15, rightArrow.y + 45);

        g.drawRect(selectBtn.x, selectBtn.y, selectBtn.width, selectBtn.height);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("SELECT", selectBtn.x + 45, selectBtn.y + 38);
    }

    public void mouseClicked(MouseEvent e) {
        if (transitioning) return;

        if (leftArrow.contains(e.getPoint())) {
            startTransition(-1);
        } else if (rightArrow.contains(e.getPoint())) {
            startTransition(1);
        } else if (selectBtn.contains(e.getPoint())) {
            game.initPlayerCharacter(characters[currentIndex]);
        }
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
}