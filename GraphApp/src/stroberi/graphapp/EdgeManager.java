package stroberi.graphapp;

public class EdgeManager {
    private GraphPanel graphPanel;
    private NodeManager nodeManager;

    public EdgeManager(GraphPanel graphPanel, NodeManager nodeManager) {
        this.graphPanel = graphPanel;
        this.nodeManager = nodeManager;
    }

    public Edge createEdge(Node node1, Node node2) {
        Edge newEdge = new Edge(node1, node2);
        graphPanel.addEdge(newEdge);
        return newEdge;
    }

    public Edge removeEdge(Node node1, Node node2) {
        Edge edge = graphPanel.getEdge(node1, node2);
        graphPanel.removeEdge(edge);
        return edge;
    }

    public void updateEdgePosition(Node node){
        for(Edge edge : graphPanel.getEdges()){
            //if the node is the start of the edge, update the edge position
            if(edge.getStart() == node){
                edge.setStart(node);
            }
            //if the node is the end of the edge, update the edge position
            else if(edge.getEnd() == node){
                edge.setEnd(node);
            }
        }
    }

}
