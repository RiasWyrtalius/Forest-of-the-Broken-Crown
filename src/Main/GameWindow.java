package Main;

import javax.swing.*;

public class GameWindow extends JFrame {

    private JFrame jFrame;

    //Debating between game being resizable or stay to 1920x1080

    public GameWindow(GamePanel gamePanel) {
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Forest of the Broken Crown");
        jFrame.add(gamePanel);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
