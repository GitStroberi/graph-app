package stroberi.graphapp;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {
    private GraphPanel graphPanel;
    private EdgeManager edgeManager;
    public KeyboardListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.edgeManager = graphPanel.getEdgeManager();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE){
            // System.out.println("Space pressed");
            graphPanel.toggleGraphMode();
        }
        if(!graphPanel.getIsDirected()){
            edgeManager.createUndirectedEdges();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
