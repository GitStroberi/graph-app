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
            System.out.println("Biggest label: " + graphPanel.getBiggestLabel());
        }
    }

    public void removeNode(Node node) {
        graphPanel.removeNode(node);
        graphPanel.getSelectedNodes().remove(node);
        graphPanel.addAvailableLabel(Integer.parseInt(node.getLabel()));

        //check if the removed label is the biggest label
        if(Integer.parseInt(node.getLabel()) == graphPanel.getBiggestLabel()){
            graphPanel.setBiggestLabel(graphPanel.getBiggestLabel()-1);
            System.out.println("Biggest label: " + graphPanel.getBiggestLabel());
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
