package stroberi.graphapp.listeners;

import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.GraphPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {
    final private GraphPanel graphPanel;
    final private EdgeManager edgeManager;

    private boolean mapMode = false;
    public KeyboardListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.edgeManager = graphPanel.getEdgeManager();
        this.mapMode = graphPanel.getMapMode();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE){
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
            graphPanel.getAlgorithmManager().findConnectedComponents();
        }

        if(key == KeyEvent.VK_S){
            graphPanel.getUtils().redrawAsSCCs();
        }

        if(key == KeyEvent.VK_T){
            graphPanel.getAlgorithmManager().topologicalSort();
        }

        if(key == KeyEvent.VK_K){
            graphPanel.getAlgorithmManager().findRoot();
        }

        if(key == KeyEvent.VK_W){
            graphPanel.setEdgeWeights();
            System.out.println("Prim's algorithm");
            graphPanel.getAlgorithmManager().runPrim();
            System.out.println("Kruskal's algorithm");
            graphPanel.getAlgorithmManager().runKruskal();
        }

        if(key == KeyEvent.VK_F){
            graphPanel.getEdgeManager().resetEdges();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
