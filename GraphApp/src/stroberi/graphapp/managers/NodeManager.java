package stroberi.graphapp.managers;

import stroberi.graphapp.GraphPanel;
import stroberi.graphapp.models.Node;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NodeManager {
    final private GraphPanel graphPanel;
    final private ArrayList<Node> selectedNodes;
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
        // graphPanel.getAdjacencyMatrix().printMatrix();
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
        // graphPanel.getAdjacencyMatrix().printMatrix();
        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
    }

    public void removeAllNodes() {
        ArrayList<Node> nodesToRemove = new ArrayList<>();
        for(Node node : graphPanel.getNodes()){
            nodesToRemove.add(node);
        }

        for(Node node : nodesToRemove){
            graphPanel.getEdgeManager().removeEdges(node);
            removeNode(node);
        }
    }

    public void toggleNodeSelection(Node node) {
        if(selectedNodes.contains(node)) {
            node.unselect();
            selectedNodes.remove(node);
        }
        else {
            node.select();
            selectedNodes.add(node);
            // System.out.println("Selected nodes: ");
            /*for(Node n : selectedNodes) {
                System.out.println(n.getLabel());
            }*/
        }

        //ensure that only two nodes are selected at a time
        if(selectedNodes.size() > 2) {
            selectedNodes.get(0).unselect();
            selectedNodes.remove(0);
        }
    }

    private boolean isClickOnNode(int x, int y, Node node) {
        //check if the click is within the bounds of the node, using the radius
        int radius = node.getRadius();
        int centerX = node.getX();
        int centerY = node.getY();

        //check if the click is within the bounds of the node, using the distance formula
        double distance = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2));
        return distance <= radius/2;
    }

    public boolean isNodeNearby(int x, int y) {
        int radius;
        int centerX;
        int centerY;
        for(Node node : graphPanel.getNodes()) {
            radius = node.getRadius();
            centerX = node.getX();
            centerY = node.getY();

            double distance = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2));
            if(distance <= radius) {
                return true;
            }
        }
        return false;
    }

    public Node getNodeByLabel(String label){
        for(Node node : graphPanel.getNodes()){
            if(node.getLabel().equals(label)){
                return node;
            }
        }
        return null;
    }
    public void generateRandomNodes(int count){
        //clear the current nodes
        ArrayList<Node> nodesToRemove = new ArrayList<>();
        for(Node node : graphPanel.getNodes()){
            nodesToRemove.add(node);
        }
        for(Node node : nodesToRemove){
            graphPanel.getEdgeManager().removeEdges(node);
            removeNode(node);
        }

        //generate new nodes at random positions. if the node is overlapping, generate a new position
        for(int i = 0; i < count; i++){
            int x = (int)(Math.random() * (graphPanel.getWidth() - graphPanel.nodeSize())) + graphPanel.nodeSize()/2;
            int y = (int)(Math.random() * (graphPanel.getHeight() - graphPanel.nodeSize())) + graphPanel.nodeSize()/2;
            while(isNodeNearby(x, y)){
                x = (int)(Math.random() * (graphPanel.getWidth() - graphPanel.nodeSize())) + graphPanel.nodeSize()/2;
                y = (int)(Math.random() * (graphPanel.getHeight() - graphPanel.nodeSize())) + graphPanel.nodeSize()/2;
            }
            createNode(x, y);
        }
    }
}
