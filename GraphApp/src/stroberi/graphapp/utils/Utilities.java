package stroberi.graphapp.utils;

import stroberi.graphapp.GraphPanel;
import stroberi.graphapp.models.AdjacencyMatrix;
import stroberi.graphapp.models.Edge;
import stroberi.graphapp.models.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Utilities {
    private final GraphPanel graphPanel;

    int width, height;
    public Utilities(GraphPanel graphPanel){
        this.graphPanel = graphPanel;
        this.width = graphPanel.getWidth();
        this.height = graphPanel.getHeight();
    }

    public Color getRandomColor() {
        // Generate a random color (you can customize this logic)
        return new Color((int) (Math.random() * 0x1000000));
    }

    public ArrayList<Node> getNeighbours(Node node, ArrayList<Edge> edges){
        ArrayList<Node> neighbours = new ArrayList<>();
        for(Edge e : edges){
            if(e.getStart() == node){
                neighbours.add(e.getEnd());
            }
            else if(e.getEnd() == node){
                neighbours.add(e.getStart());
            }
        }
        return neighbours;
    }

    public ArrayList<Node> getNeighboursDirected(Node node, ArrayList<Edge> edges){
        ArrayList<Node> neighbours = new ArrayList<>();
        for(Edge e : edges){
            if(e.getStart() == node){
                neighbours.add(e.getEnd());
            }
        }
        return neighbours;
    }

    private void fillOrderRecursive(AdjacencyMatrix matrix, Node node, boolean[] visited, Stack<Node> stack) {
        visited[Integer.parseInt(node.getLabel())] = true;
        for (Node neighbor : getNeighboursDirected(node, graphPanel.getEdges())) {
            if (!visited[Integer.parseInt(neighbor.getLabel())]) {
                fillOrderRecursive(matrix, neighbor, visited, stack);
            }
        }
        stack.push(node);

        //print the stack
        System.out.println("The recursive stack is: ");
        for(Node n : stack){
            System.out.print(n.getLabel() + " ");
        }
    }

    public void fillOrder(Node startNode, boolean[] visited, Stack<Node> stack) {
        Stack<Node> callStack = new Stack<>();
        callStack.push(startNode);

        while(!callStack.isEmpty()) {
            Node node = callStack.pop();
            if(!visited[Integer.parseInt(node.getLabel())]) {
                visited[Integer.parseInt(node.getLabel())] = true;
                callStack.push(node);
                for(Node neighbor : getNeighboursDirected(node, graphPanel.getEdges())) {
                    if(!visited[Integer.parseInt(neighbor.getLabel())]) {
                        callStack.push(neighbor);
                    }
                }
            } else {
                stack.push(node);
            }
        }
    }

    private boolean isCyclicUtil(Node node, boolean[] visited, boolean[] recursionStack) {
        int nodeIndex = Integer.parseInt(node.getLabel());

        if (!visited[nodeIndex]) {
            visited[nodeIndex] = true;
            recursionStack[nodeIndex] = true;

            for (Node neighbor : getNeighboursDirected(node, graphPanel.getEdges())) {
                int neighborIndex = Integer.parseInt(neighbor.getLabel());

                if (!visited[neighborIndex] && isCyclicUtil(neighbor, visited, recursionStack)) {
                    return true;
                } else if (recursionStack[neighborIndex]) {
                    return true; // Cycle detected
                }
            }
        }

        recursionStack[nodeIndex] = false; // Remove the node from the recursion stack
        return false;
    }

    public boolean isAcyclic() {
        boolean[] visited = new boolean[graphPanel.getBiggestLabel() + 1];
        boolean[] recursionStack = new boolean[graphPanel.getBiggestLabel() + 1];

        for (Node node : graphPanel.getNodes()) {
            if (!visited[Integer.parseInt(node.getLabel())]) {
                if (isCyclicUtil(node, visited, recursionStack)) {
                    return false; // If a cycle is detected, the graph is not acyclic
                }
            }
        }

        return true; // If no cycles are detected, the graph is acyclic
    }

    private ArrayList<Node> getSCCNodes(ArrayList<ArrayList<Node>> connectedComponents) {
        ArrayList<Node> newNodes = new ArrayList<>();

        //create the new nodes by finding the center of each connected component
        for(ArrayList<Node> connectedComponent : connectedComponents){
            int x = 0;
            int y = 0;
            for(Node n : connectedComponent){
                x += n.getX();
                y += n.getY();
            }

            int newLabel = Integer.parseInt(connectedComponent.get(0).getLabel());

            x /= connectedComponent.size();
            y /= connectedComponent.size();
            Node newNode = new Node(x, y, graphPanel.getNodeSize(), Integer.toString(newLabel));
            newNodes.add(newNode);
        }
        return newNodes;
    }

    private int findConnectedComponentIndex(ArrayList<ArrayList<Node>> connectedComponents, Node node) {
        for (int i = 0; i < connectedComponents.size(); i++) {
            if (connectedComponents.get(i).contains(node)) {
                return i;
            }
        }
        return -1; // Node not found in any connected component
    }
    public void displaySCCs() {
        ArrayList<ArrayList<Node>> connectedComponents = graphPanel.getAlgorithmManager().kosaraju();
        System.out.println("The strongly connected components are: ");
        for (ArrayList<Node> connectedComponent : connectedComponents) {
            System.out.print("[");
            for (Node n : connectedComponent) {
                System.out.print(n.getLabel() + " ");
            }
            System.out.println("]");
        }
    }

    public void redrawAsSCCs(){
        ArrayList<ArrayList<Node>> connectedComponents = graphPanel.getAlgorithmManager().kosaraju();

        ArrayList<Node> newNodes = getSCCNodes(connectedComponents);

        //adjacency list of sccs as a map that takes in a node and as value has an array of the adjacent nodes
        //the node is the newly created SCC, and the adjacent nodes are the nodes that represent other SCCs and are adjacent to the current SCC
        HashMap<Node, ArrayList<Node>> adjacencyListSCC = new HashMap<>();

        // Create the adjacency list for the SCCs
        for (int i = 0; i < connectedComponents.size(); i++) {
            ArrayList<Node> adjacentNodes = new ArrayList<>();

            //iterate through all the edges that start from the nodes in the current SCC
            for (Edge edge : graphPanel.getEdges()) {
                if (connectedComponents.get(i).contains(edge.getStart())) {
                    //if the end node of the edge is not in the current SCC, then it is adjacent to the current SCC
                    if (!connectedComponents.get(i).contains(edge.getEnd())) {
                        //if the adjacent node is not already in the list of adjacent nodes, then add it
                        if (!adjacentNodes.contains(newNodes.get(findConnectedComponentIndex(connectedComponents, edge.getEnd())))) {
                            adjacentNodes.add(newNodes.get(findConnectedComponentIndex(connectedComponents, edge.getEnd())));
                        }
                    }
                }
            }

            // Ensure that the map is initialized before putting a new entry
            adjacencyListSCC.put(newNodes.get(i), adjacentNodes);
        }

        System.out.println("The adjacency list for the SCCs is: ");
        for(Node n : adjacencyListSCC.keySet()){
            System.out.print(n.getLabel() + ": ");
            for(Node neighbor : adjacencyListSCC.get(n)){
                System.out.print(neighbor.getLabel() + " ");
            }
            System.out.println();
        }

        graphPanel.getNodeManager().removeAllNodes();

        //add the new nodes to the graph array, and remove those labels from the available labels
        for(Node n : newNodes){
            graphPanel.getNodeManager().createNode(n.getX(), n.getY());
        }

        System.out.println("The new nodes are: ");
        for(Node n : graphPanel.getNodes()){
            System.out.print(n.getLabel() + ", ");
        }

        //add the edges between the new nodes, based on the adjacency list
        for(Node n : adjacencyListSCC.keySet()){
            for(Node neighbor : adjacencyListSCC.get(n)){
                if(n != neighbor){
                    //select nodes placed at the same position as n and neighbor
                    Node n1 = graphPanel.getNodeManager().getNodeAt(n.getX(), n.getY());
                    Node n2 = graphPanel.getNodeManager().getNodeAt(neighbor.getX(), neighbor.getY());
                    if(n1 != null && n2 != null){
                        graphPanel.getNodeManager().toggleNodeSelection(n1);
                        graphPanel.getNodeManager().toggleNodeSelection(n2);
                        graphPanel.getEdgeManager().createEdge();
                    }
                }
            }
        }
        graphPanel.repaint();
    }


    public double lerp(double a, double b, double f) {
        // Check if the interpolation factor is within the valid range
        if (f < 0 || f > 1) {
            throw new IllegalArgumentException("Interpolation factor (f) should be between 0 and 1.");
        }
        // Calculate the interpolated value
        double interpolatedValue = (1.0 - f) * a + f * b;
        System.out.println("Interpolated value: " + interpolatedValue + " for f = " + f + " and a = " + a + " and b = " + b + ".");
        return interpolatedValue;
    }

    public int mapLongitudeToX(double longitude){
        //the result is a value between 0 and width
        double longitudeMin = graphPanel.getLongitudeMin();
        double longitudeMax = graphPanel.getLongitudeMax();
        return (int)lerp(0, width, (longitude - longitudeMin) / (longitudeMax - longitudeMin));
    }

    public int mapLatitudeToY(double latitude){
        //the result is a value between 0 and height
        double latitudeMin = graphPanel.getLatitudeMin();
        double latitudeMax = graphPanel.getLatitudeMax();
        return (int)lerp(0, height, (latitude - latitudeMin) / (latitudeMax - latitudeMin));
    }

}
