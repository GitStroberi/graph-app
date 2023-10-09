package stroberi.graphapp;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {
    private GraphPanel graphPanel;

    public KeyboardListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE){
            System.out.println("Space pressed");
            graphPanel.toggleGraphMode();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
