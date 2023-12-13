package stroberi.graphapp.models;

import stroberi.graphapp.GraphPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdjacencyList {
    HashMap<Node, ArrayList<Node>> adjacencyList = new HashMap<>();
    private final GraphPanel graphPanel;
    public AdjacencyList(GraphPanel graphPanel){
        this.graphPanel = graphPanel;
        createAdjacencyList();
    }

    public void createAdjacencyList(){
        for(Node node : graphPanel.getNodes()){
            ArrayList<Node> neighbours = new ArrayList<>();
            for(Edge edge : graphPanel.getEdges()){
                if(edge.getStart() == node){
                    neighbours.add(edge.getEnd());
                }
            }
            adjacencyList.put(node, neighbours);
        }
    }

    public void addNode(Node node){
        ArrayList<Node> neighbours = new ArrayList<>();
        adjacencyList.put(node, neighbours);
    }

    public void removeNode(Node node){
        adjacencyList.remove(node);
    }

    public void addAdjacentNode(Node node, Node neighbour){
        adjacencyList.get(node).add(neighbour);
    }

    public void removeAdjacentNode(Node node, Node neighbour){
        adjacencyList.get(node).remove(neighbour);
    }

    public ArrayList<Node> getNeighbours(Node node){
        return adjacencyList.get(node);
    }

    public void printAdjacencyList(){
        for(Node node : adjacencyList.keySet()){
            System.out.print(node.getLabel() + " : ");
            for(Node neighbour : adjacencyList.get(node)){
                System.out.print(neighbour.getLabel() + " ");
            }
            System.out.println();
        }
    }
}
