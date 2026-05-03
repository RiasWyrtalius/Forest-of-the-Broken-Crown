package Main.Core;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;
import Main.GameStates.PauseScreen;

import static Main.Core.Game.GAME_HEIGHT;
import static Main.Core.Game.GAME_WIDTH;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private PauseScreen pauseScreen;
    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game) {
        mouseInputs = new MouseInputs(this);
        this.game = game;

        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        setFocusable(true);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        //System.out.println("Size: " + GAME_WIDTH + "x" + GAME_HEIGHT);
    }

    public void updateGame() {}

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        double scaleX = (double) getWidth() / GAME_WIDTH;
        double scaleY = (double) getHeight() / GAME_HEIGHT;

        g2d.scale(scaleX, scaleY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        game.render(g2d);
    }

    public Game getGame() {
        return game;
    }
}
