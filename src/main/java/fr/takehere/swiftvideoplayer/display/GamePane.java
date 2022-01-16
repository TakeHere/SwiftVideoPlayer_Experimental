package fr.takehere.swiftvideoplayer.display;

import fr.takehere.swiftvideoplayer.SwiftVideoPlayer;

import javax.swing.*;
import java.awt.*;

public class GamePane extends JPanel{
    SwiftVideoPlayer mainInstance = SwiftVideoPlayer.getInstance();
    private static GamePane instance;

    private GamePane() {
        this.setFocusable(true);
        this.setBackground(Color.RED);
    }

    public static GamePane get(){
        if (instance == null){
            instance = new GamePane();
        }
        return instance;
    }

    int index = 0;

    @Override
    public void paint(Graphics g){
        super.paint(g);

        g.clearRect(0,0,500,500);

        Graphics2D g2d = (Graphics2D) g;

        if (index == mainInstance.bufferedImages.size()) System.exit(0);
        g2d.drawImage(mainInstance.bufferedImages.get(index), 0,0,GameFrame.get().getWidth(), GameFrame.get().getHeight(), null);

        index++;

        super.paintComponents(g);
        mainInstance.gameLoop();
    }
}