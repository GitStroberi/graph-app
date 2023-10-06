package stroberi.graphapp;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NodeManager {
    private GraphPanel graphPanel;
    private ArrayList<Node> selectedNodes;
    public NodeManager(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.selectedNodes = new ArrayList<>();
    }

    public Node getNodeAt(int x, int y) {
        for(Node node : graphPanel.getNodes()) {
            if(isClickOnNode(x, y, node)) {
                return node;
            }
        }
        return null;
    }

    public Node createNode(int x, int y) {
        Node newNode = new Node(x, y, graphPanel.nodeSize(), String.valueOf(graphPanel.nodeCount()));
        graphPanel.addNode(newNode);
        return newNode;
    }

    public void toggleEdge(Node start, Node end) {
        //if there is already an edge between the two nodes, remove it

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
    
    public void createEdge() {
        if(selectedNodes.size() == 2) {
            //if there is already an edge between the two nodes, remove it
            Edge edge = graphPanel.getEdge(selectedNodes.get(0), selectedNodes.get(1));
            if(edge != null) {
                graphPanel.removeEdge(edge);

                //unselect the nodes
                selectedNodes.get(0).unselect();
                selectedNodes.get(1).unselect();
                selectedNodes.clear();
            }
            else {
                //if there is no edge between the two nodes, create it
                edge = new Edge(selectedNodes.get(0), selectedNodes.get(1));
                graphPanel.addEdge(edge);

                //unselect the nodes
                selectedNodes.get(0).unselect();
                selectedNodes.get(1).unselect();
                selectedNodes.clear();
            }
        }
    }

    private boolean isClickOnNode(int x, int y, Node node) {
        return x >= node.getX()-25 && x <= node.getX()+25 && y >= node.getY()-25 && y <= node.getY()+25;
    }
}
