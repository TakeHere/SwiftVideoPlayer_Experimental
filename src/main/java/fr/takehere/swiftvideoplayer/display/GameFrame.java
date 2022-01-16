package fr.takehere.swiftvideoplayer.display;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private static GameFrame instance;
    public static String title = "Swift video player";

    private GameFrame() throws HeadlessException {
        super(title);
        this.setSize(500,500);
        this.setLayout(new GridBagLayout());
        this.setContentPane(GamePane.get());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
    }

    public static GameFrame get(){
        if (instance == null){
            instance = new GameFrame();
        }
        return instance;
    }
}
