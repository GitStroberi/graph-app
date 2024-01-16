package stroberi.graphapp.managers;

import stroberi.graphapp.models.Edge;
import stroberi.graphapp.GraphPanel;
import stroberi.graphapp.models.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class EdgeManager {
    final private GraphPanel graphPanel;
    final private ArrayList<Node> selectedNodes;

    public EdgeManager(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.selectedNodes = graphPanel.getSelectedNodes();
    }

    public void createEdge(){
        //if there is no edge between the two nodes, create it
        //for now, we will assume that the edge is undirected, so we will create two edges
        Edge edge = new Edge(selectedNodes.get(0), selectedNodes.get(1));
        graphPanel.addEdge(edge);

        if(graphPanel.getIsUndirected()){
            // System.out.println("Entered if because graph is undirected (createEdge)");
            Edge edge2 = new Edge(selectedNodes.get(1), selectedNodes.get(0));
            graphPanel.addEdge(edge2);
        }

        //unselect the nodes
        selectedNodes.get(0).unselect();
        selectedNodes.get(1).unselect();
        selectedNodes.clear();
    }

    public Edge getEdge(Node start, Node end, ArrayList<Edge> edges) {
        for (Edge edge : edges) {
            if (edge.getStart() == start && edge.getEnd() == end) {
                return edge;
            }
        }
        return null;
    }

    public Edge getEdge(Node start, Node end, HashMap<String, Edge> edgeMap) {
        String edgeKey = start.getLabel() + "-" + end.getLabel(); // Assuming Node has an 'getId()' method

        // Check if the edge exists in the map
        if (edgeMap.containsKey(edgeKey)) {
            return edgeMap.get(edgeKey);
        }

        // Edge not found
        return null;
    }

    public void removeEdge(Edge edge){
        graphPanel.removeEdge(edge);

        //remove the other edge for now, we will assume that the edge is undirected
        //Edge edge2 = graphPanel.getEdge(edge.getEnd(), edge.getStart());
        //graphPanel.removeEdge(edge2);

        if(graphPanel.getIsUndirected()){
            // System.out.println("Entered if because graph is undirected (removeEdge)");
            Edge edge2 = graphPanel.getEdge(edge.getEnd(), edge.getStart());
            if (edge2 != null){
                // System.out.println("Also removing edge " + edge2.getStart().getLabel() + " " + edge2.getEnd().getLabel());
                graphPanel.removeEdge(edge2);
            }
            /*else{
                // System.out.println("Edge2 is null");
            }*/
        }

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
                // System.out.println("Called removeEdge for " + edge.getStart().getLabel() + " " + edge.getEnd().getLabel());
                removeEdge(edge);
            }
            else {
                createEdge();
            }
        }
    }

    public void toggleRunDjikstra() {
        if(selectedNodes.size() == 2) {
            graphPanel.getAlgorithmManager().runDjikstra(selectedNodes.get(0), selectedNodes.get(1));
        }
    }

    public void createUndirectedEdges() {
        // new edge array
        ArrayList<Edge> newEdges = new ArrayList<>();
        ArrayList<Edge> edges = graphPanel.getEdges();
        for(Edge edge : edges) {
            Edge edge2 = new Edge(edge.getEnd(), edge.getStart());
            //add the edge to the new edge array only if it doesn't already exist
            if(!newEdges.contains(edge2)) {
                newEdges.add(edge2);
            }
        }
        for (Edge edge : newEdges) {
            graphPanel.addEdge(edge);
        }
        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
    }

    public void createCompleteGraph(){
        ArrayList<Node> nodes = graphPanel.getNodes();
        ArrayList<Edge> newEdges = new ArrayList<>();
        for(Node node1 : nodes){
            for(Node node2 : nodes){
                if(node1 != node2){
                    Edge edge = new Edge(node1, node2);
                    if(!newEdges.contains(edge)){
                        newEdges.add(edge);
                    }
                }
            }
        }
        for (Edge edge : newEdges) {
            graphPanel.addEdge(edge);
        }
        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
        graphPanel.repaint();
    }

    public void createPartialGraph(int chanceOfEdge){
        ArrayList<Edge> newEdges = new ArrayList<>();
        ArrayList<Node> nodes = graphPanel.getNodes();
        //in case the graph is undirected, to avoid giving the same edge two chances,
        //after we are finished with a node, we remove it from the list so that it won't be checked again
        ArrayList<Node> tempNodes = new ArrayList<>(nodes);
        if(graphPanel.getIsUndirected()){
            for(Node node1 : nodes){
                for(Node node2 : tempNodes){
                    if(node1 != node2){
                        int random = (int)(Math.random() * 100);
                        if(random < chanceOfEdge){
                            Edge edge = new Edge(node1, node2);
                            if(!newEdges.contains(edge)){
                                newEdges.add(edge);
                            }
                        }
                    }
                }
                tempNodes.remove(node1);
            }
        }
        else{
            for(Node node1 : nodes){
                for(Node node2 : nodes){
                    if(node1 != node2){
                        int random = (int)(Math.random() * 100);
                        if(random < chanceOfEdge){
                            Edge edge = new Edge(node1, node2);
                            if(!newEdges.contains(edge)){
                                newEdges.add(edge);
                            }
                        }
                    }
                }
            }
        }

        for (Edge edge : newEdges) {
            graphPanel.addEdge(edge);
        }

        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
        graphPanel.repaint();

        System.out.println("Edge count: " + graphPanel.getEdges().size());
    }

    public void removeAllEdges() {
        ArrayList<Edge> edgesToRemove = new ArrayList<>(graphPanel.getEdges());
        for(Edge edge : edgesToRemove) {
            graphPanel.removeEdge(edge);
        }
        graphPanel.getAdjacencyMatrix().saveMatrixToFile();
        graphPanel.repaint();
    }

    public void printEdges() {
        for(Edge edge : graphPanel.getEdges()) {
            System.out.println(edge.getStart().getLabel() + " " + edge.getEnd().getLabel());
        }
    }

    public void resetEdges(){
        for(Edge edge : graphPanel.getEdges()){
            edge.unselect();
            edge.setWeight(0);
        }
        graphPanel.repaint();
    }
}
