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
        if(graphPanel.getIsUndirected()){
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
        if(key == KeyEvent.VK_A){
            graphPanel.findConnectedComponents();
        }

        if(key == KeyEvent.VK_S){
            graphPanel.redrawAsSCCs();
        }

        if(key == KeyEvent.VK_T){
            graphPanel.topologicalSort();
        }

        if(key == KeyEvent.VK_K){
            graphPanel.findRoot();
        }

        if(key == KeyEvent.VK_W){
            graphPanel.setEdgeWeights();
            System.out.println("Prim's algorithm");
            graphPanel.prim();
            System.out.println("Kruskal's algorithm");
            graphPanel.kruskal();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
