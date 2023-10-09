package stroberi.graphapp;

import java.util.ArrayList;

public class NodeManager {
    private GraphPanel graphPanel;
    private ArrayList<Node> selectedNodes;
    public NodeManager(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.selectedNodes = graphPanel.getSelectedNodes();
    }

    public Node getNodeAt(int x, int y) {
        for(Node node : graphPanel.getNodes()) {
            if(isClickOnNode(x, y, node)) {
                return node;
            }
        }
        return null;
    }

    public void createNode(int x, int y) {
        int label;
        if(graphPanel.getAvailableLabels().isEmpty()){
            label = graphPanel.nodeCount();
        }
        else {
            label = graphPanel.getAvailableLabels().get(0);
            graphPanel.removeAvailableLabel(label);
        }
        Node newNode = new Node(x, y, graphPanel.nodeSize(), Integer.toString(label));
        graphPanel.addNode(newNode);

        //check if the added label is the biggest label
        if(label > graphPanel.getBiggestLabel()){
            graphPanel.setBiggestLabel(label);
        }

        //resize the adjacency matrix
        graphPanel.getAdjacencyMatrix().resizeMatrix(graphPanel.getBiggestLabel()+1);
        graphPanel.getAdjacencyMatrix().printMatrix();
        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
    }

    public void removeNode(Node node) {
        graphPanel.removeNode(node);
        graphPanel.getSelectedNodes().remove(node);
        graphPanel.addAvailableLabel(Integer.parseInt(node.getLabel()));

        //check if the removed label is the biggest label
        if(Integer.parseInt(node.getLabel()) == graphPanel.getBiggestLabel()){
            //find the new biggest label
            int biggestLabel = -1;
            for(Node n : graphPanel.getNodes()){
                if(Integer.parseInt(n.getLabel()) > biggestLabel){
                    biggestLabel = Integer.parseInt(n.getLabel());
                }
            }
            graphPanel.setBiggestLabel(biggestLabel);
        }

        //resize the adjacency matrix
        graphPanel.getAdjacencyMatrix().resizeMatrix(graphPanel.getBiggestLabel()+1);
        graphPanel.getAdjacencyMatrix().printMatrix();
        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
    }

    public void toggleNodeSelection(Node node) {
        if(selectedNodes.contains(node)) {
            node.unselect();
            selectedNodes.remove(node);
        }
        else {
            node.select();
            selectedNodes.add(node);
            System.out.println("Selected nodes: ");
            for(Node n : selectedNodes) {
                System.out.println(n.getLabel());
            }
        }

        //ensure that only two nodes are selected at a time
        if(selectedNodes.size() > 2) {
            selectedNodes.get(0).unselect();
            selectedNodes.remove(0);
        }
    }

    private boolean isClickOnNode(int x, int y, Node node) {
        return x >= node.getX()-25 && x <= node.getX()+25 && y >= node.getY()-25 && y <= node.getY()+25;
    }
}
