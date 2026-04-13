package Main.GameStates;

import Entities.PlayerCharacter;
import Main.Core.Game;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CharacterSelect {

    private Game game;
    private PlayerCharacter[] characters = PlayerCharacter.values();
    private int currentIndex = 0;

    // Animation variables
    private int aniTick, aniIndex, aniSpeed = 25;

    // Interactive UI Elements
    private Rectangle leftArrow = new Rectangle(150, 300, 60, 60);
    private Rectangle rightArrow = new Rectangle(Game.GAME_WIDTH - 210, 300, 60, 60);
    private Rectangle selectBtn = new Rectangle((Game.GAME_WIDTH / 2) - 100, 550, 200, 55);

    private boolean mouseOverSelect = false;

    public CharacterSelect(Game game) {
        this.game = game;
    }

    public void update() {
        updateAnimationTick();
    }

    private void updateAnimationTick() {
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
        g.setColor(new Color(20, 20, 25));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        PlayerCharacter selected = characters[currentIndex];

        g.setFont(new Font("Arial", Font.BOLD, 42));
        g.setColor(Color.WHITE);
        String name = selected.name();
        int nameWidth = g.getFontMetrics().stringWidth(name);
        g.drawString(name, (Game.GAME_WIDTH / 2) - (nameWidth / 2), 120);

        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        g.drawString("<", leftArrow.x + 15, leftArrow.y + 45);
        g.drawString(">", rightArrow.x + 15, rightArrow.y + 45);

        BufferedImage img = game.getCharacterAtlas(selected);
        int spriteSize = Game.SPRITE_DEFAULT_SIZE;
        int drawScale = (int) (Game.SCALE * 3);

        g.drawImage(img.getSubimage(aniIndex * spriteSize, selected.getRowIDLE() * spriteSize, spriteSize, spriteSize),
                (Game.GAME_WIDTH / 2) - (spriteSize * drawScale / 2),
                220,
                spriteSize * drawScale,
                spriteSize * drawScale,
                null);

        g.drawRect(selectBtn.x, selectBtn.y, selectBtn.width, selectBtn.height);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        String btnText = "SELECT";
        int txtWidth = g.getFontMetrics().stringWidth(btnText);
        g.drawString(btnText, selectBtn.x + (selectBtn.width / 2) - (txtWidth / 2), selectBtn.y + 38);
    }

    public void mouseClicked(MouseEvent e) {
        if (leftArrow.contains(e.getPoint())) {
            changeSelection(-1);
        } else if (rightArrow.contains(e.getPoint())) {
            changeSelection(1);
        } else if (selectBtn.contains(e.getPoint())) {
            game.initPlayerCharacter(characters[currentIndex]);
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseOverSelect = selectBtn.contains(e.getPoint());
    }

    private void changeSelection(int amount) {
        currentIndex += amount;
        if (currentIndex < 0) currentIndex = characters.length - 1;
        else if (currentIndex >= characters.length) currentIndex = 0;

        // Reset animation frames when switching characters
        aniIndex = 0;
        aniTick = 0;
    }
}