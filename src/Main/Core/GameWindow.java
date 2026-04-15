package Main.Core;

import Utils.LoadSave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class GameWindow extends JFrame {

    private JFrame jFrame;

    //Debating between game being resizable or stay to 1920x1080

    public GameWindow(GamePanel gamePanel) {
        jFrame = new JFrame();

        try {
            Image icon = LoadSave.getSpriteAtlas(LoadSave.GAME_ICON);
            jFrame.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Could not load game icon!");
        }

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Forest of the Broken Crown");
        jFrame.add(gamePanel);
        jFrame.setResizable(true);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        jFrame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });
    }
}
