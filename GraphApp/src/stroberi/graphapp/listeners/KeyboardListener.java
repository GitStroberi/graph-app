package stroberi.graphapp.listeners;

import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.GraphPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {
    final private GraphPanel graphPanel;
    final private EdgeManager edgeManager;
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
        if (key == KeyEvent.VK_C){
            graphPanel.getEdgeManager().createCompleteGraph();
        }
        if(key == KeyEvent.VK_R){
            graphPanel.getEdgeManager().removeAllEdges();
        }
        if(key == KeyEvent.VK_G){
            System.out.println("Generating random graph");
            System.out.println("Give the number of nodes: ");
            int numberOfNodes = Integer.parseInt(graphPanel.getScanner().nextLine());
            System.out.println("Give the chance of edges appearing between nodes: ");
            int chanceOfEdges = Integer.parseInt(graphPanel.getScanner().nextLine());

            graphPanel.getNodeManager().generateRandomNodes(numberOfNodes);
            graphPanel.getEdgeManager().createPartialGraph(chanceOfEdges);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
