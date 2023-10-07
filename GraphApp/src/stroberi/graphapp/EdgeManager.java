package stroberi.graphapp;

import java.util.ArrayList;

public class EdgeManager {
    private GraphPanel graphPanel;
    private ArrayList<Node> selectedNodes;

    public EdgeManager(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.selectedNodes = graphPanel.getSelectedNodes();
    }

    public void createEdge(){
        //if there is no edge between the two nodes, create it
        //for now, we will assume that the edge is undirected so we will create two edges
        Edge edge = new Edge(selectedNodes.get(0), selectedNodes.get(1));
        graphPanel.addEdge(edge);
        Edge edge2 = new Edge(selectedNodes.get(1), selectedNodes.get(0));
        graphPanel.addEdge(edge2);

        //unselect the nodes
        selectedNodes.get(0).unselect();
        selectedNodes.get(1).unselect();
        selectedNodes.clear();
    }

    public void removeEdge(Edge edge){
        graphPanel.removeEdge(edge);

        //remove the other edge for now, we will assume that the edge is undirected
        Edge edge2 = graphPanel.getEdge(edge.getEnd(), edge.getStart());
        graphPanel.removeEdge(edge2);

        //System.out.println("Edge removed");
        //unselect the nodes
        selectedNodes.get(0).unselect();
        selectedNodes.get(1).unselect();
        selectedNodes.clear();
    }

    public void removeEdges(Node node){
        ArrayList<Edge> edges = graphPanel.getEdges();
        ArrayList<Edge> edgesToRemove = new ArrayList<>();
        for(Edge edge : edges){
            if(edge.getStart() == node || edge.getEnd() == node){
                edgesToRemove.add(edge);
            }
        }
        for(Edge edge : edgesToRemove){
            graphPanel.removeEdge(edge);
        }
    }

    public void toggleEdge() {
        if(selectedNodes.size() == 2) {
            //if there is already an edge between the two nodes, remove it
            Edge edge = graphPanel.getEdge(selectedNodes.get(0), selectedNodes.get(1));
            if(edge != null) {
                removeEdge(edge);
            }
            else {
                createEdge();
            }
            graphPanel.getAdjacencyMatrix().populateMatrix();
            graphPanel.getAdjacencyMatrix().printMatrix();
        }
    }
}
