package stroberi.graphapp.managers;

import stroberi.graphapp.models.Edge;
import stroberi.graphapp.models.Node;
import stroberi.graphapp.GraphPanel;
import stroberi.graphapp.utils.DisjointSet;

import java.awt.*;
import java.util.*;

public class AlgorithmManager {

    private final GraphPanel graphPanel;
    private final ArrayList<ArrayList<Node>> connectedComponents;
    public AlgorithmManager(GraphPanel graphPanel){
        this.graphPanel = graphPanel;
        connectedComponents = new ArrayList<>();
    }

    public void findConnectedComponents(){
        //reset the connected components
        connectedComponents.clear();
        //reset the colors of the nodes
        for(Node n : graphPanel.getNodes()){
            n.setColor(Color.WHITE);
        }
        //find the connected components
        for(Node n : graphPanel.getNodes()){
            if(n.getColor() == Color.WHITE){
                ArrayList<Node> connectedComponent = new ArrayList<>();
                dfsUndirected(n, connectedComponent);
                connectedComponents.add(connectedComponent);
            }
        }
        //print the connected components
        System.out.println("The connected components are: ");
        for(ArrayList<Node> connectedComponent : connectedComponents){
            System.out.print("[");
            for(Node n : connectedComponent){
                System.out.print(n.getLabel() + " ");
            }
            System.out.println("]");
        }
        //color the connected components
        for(ArrayList<Node> connectedComponent : connectedComponents){
            Color color = graphPanel.getUtils().getRandomColor();
            for(Node n : connectedComponent){
                n.setColor(color);
            }
        }
        //repaint the graph
        graphPanel.repaint();
    }

    private void dfsUndirected(Node n, ArrayList<Node> connectedComponent){
        Stack<Node> stack = new Stack<>();
        stack.push(n);
        while(!stack.isEmpty()){
            Node current = stack.pop();
            if(current.getColor() == Color.WHITE){
                current.setColor(Color.GRAY);
                connectedComponent.add(current);
                ArrayList<Node> neighbours = graphPanel.getUtils().getNeighbours(current, graphPanel.getEdges());
                for(Node neighbor : neighbours){
                    stack.push(neighbor);
                }
            }
        }
    }

    private void dfsDirected(Node n, ArrayList<Edge> edges, ArrayList<Node> connectedComponent, boolean[] visited){
        visited[Integer.parseInt(n.getLabel())] = true;
        connectedComponent.add(n);
        ArrayList<Node> neighbors = graphPanel.getUtils().getNeighboursDirected(n, edges);
        for(Node neighbor : neighbors) {
            if (!visited[Integer.parseInt(neighbor.getLabel())]) {
                dfsDirected(neighbor, edges, connectedComponent, visited);
            }
        }
    }
    public void topologicalSort(){
        //check if the graph is directed
        if(graphPanel.getIsUndirected()){
            System.out.println("The graph is not directed, so it cannot be topologically sorted.");
            return;
        }
        //if the graph is not acyclic, then it cannot be topologically sorted
        if(!graphPanel.getUtils().isAcyclic()){
            System.out.println("The graph is not acyclic, so it cannot be topologically sorted.");
            return;
        }
        //the topological sort can be done by using the fillOrder function and then reversing the stack
        Stack<Node> stack = new Stack<>();
        boolean[] visited = new boolean[graphPanel.getBiggestLabel() + 1];
        ArrayList<Node> nodes = graphPanel.getNodes();
        for(Node n : nodes){
            if(!visited[Integer.parseInt(n.getLabel())]){
                graphPanel.getUtils().fillOrder(n, visited, stack);
            }
        }
        //print the topological sort
        System.out.println("The topological sort is: ");
        while(!stack.isEmpty()){
            System.out.print(stack.pop().getLabel() + " ");
        }
    }
    public ArrayList<ArrayList<Node>> kosaraju(){
        Stack<Node> stack = new Stack<>();
        boolean[] visited = new boolean[graphPanel.getBiggestLabel() + 1];
        ArrayList<Node> nodes = graphPanel.getNodes();
        for (Node node : nodes) {
            if (!visited[Integer.parseInt(node.getLabel())]) {
                graphPanel.getUtils().fillOrder(node, visited, stack);
            }
        }

        visited = new boolean[graphPanel.getBiggestLabel() + 1];
        ArrayList<ArrayList<Node>> connectedComponents =  new ArrayList<>();

        while (!stack.isEmpty()) {
            Node currentNode = stack.pop();
            if (!visited[Integer.parseInt(currentNode.getLabel())]) {
                ArrayList<Node> connectedComponent = new ArrayList<>();
                ArrayList<Edge> reverseEdges = graphPanel.getReverseEdges();
                dfsDirected(currentNode, reverseEdges, connectedComponent, visited);
                connectedComponents.add(connectedComponent);
            }
        }

        return connectedComponents;
    }

    public Node findRoot() {
        ArrayList<Node> nodes = graphPanel.getNodes();
        ArrayList<Edge> edges = graphPanel.getEdges();
        for(Node n : nodes){
            n.setColor(Color.WHITE);
        }
        graphPanel.repaint();
        if (graphPanel.getIsUndirected()) {
            System.out.println("The graph is not directed. Arborescence requires a directed graph.");
            return null;
        }

        if (!graphPanel.getUtils().isAcyclic()) {
            System.out.println("The graph is not acyclic. Arborescence must be acyclic.");
            return null;
        }

        int inDegreeZeroCount = 0;
        Node potentialRoot = null;

        // Count nodes with in-degree 0 and store them in potentialRoot
        for (Node node : nodes) {
            int inDegree = 0;
            for (Edge edge : edges) {
                if (edge.getEnd() == node) {
                    inDegree++;
                }
            }

            if (inDegree == 0) {
                inDegreeZeroCount++;
                potentialRoot = node;
            }
        }

        if (inDegreeZeroCount == 1) {
            System.out.println("The graph is an arborescence, and the root is: " + potentialRoot.getLabel());
            //color the root node green and the rest of the nodes white
            for(Node n : nodes){
                if(n.isEqual(potentialRoot)){
                    n.setColor(Color.GREEN);
                } else {
                    n.setColor(Color.WHITE);
                }
            }
            graphPanel.repaint();
            return potentialRoot;
        } else {
            System.out.println("The graph is not an arborescence.");
        }
        return null;
    }

    public ArrayList<Edge> runPrim() {
        ArrayList<Node> nodes = graphPanel.getNodes();
        ArrayList<Edge> edges = graphPanel.getEdges();
        if (!graphPanel.getIsUndirected()) {
            System.out.println("The graph must be undirected for Prim's algorithm.");
            return null;
        }

        Node root = nodes.get(0);
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getWeight() - edge2.getWeight();
            }
        });
        HashSet<Node> visited = new HashSet<>();
        ArrayList<Edge> minimumSpanningTree = new ArrayList<>();

        visited.add(root);
        for (Edge e : edges) {
            if (e.getStart() == root) {
                priorityQueue.add(e);
            }
        }

        while (!priorityQueue.isEmpty()) {
            Edge currentEdge = priorityQueue.poll();
            Node currentNode = currentEdge.getEnd();
            if (!visited.contains(currentNode)) {
                minimumSpanningTree.add(currentEdge);
                visited.add(currentNode);
                for (Edge e : edges) {
                    if (e.getStart() == currentNode) {
                        priorityQueue.add(e);
                    }
                }
            }
        }

        System.out.println("The minimum spanning tree is: ");
        for (Edge e : minimumSpanningTree) {
            System.out.println(e.getStart().getLabel() + " " + e.getEnd().getLabel() + ":" + e.getWeight());
            e.select();
            Edge reverseEdge = graphPanel.getEdge(e.getEnd(), e.getStart());
            if(reverseEdge != null){
                reverseEdge.select();
            }
        }

        return minimumSpanningTree;
    }

    // Kruskal's algorithm
    public ArrayList<Edge> runKruskal() {
        ArrayList<Edge> edges = graphPanel.getEdges();
        if (!graphPanel.getIsUndirected()) {
            System.out.println("The graph must be undirected for Kruskal's algorithm.");
            return null;
        }

        ArrayList<Edge> minimumSpanningTree = new ArrayList<>();
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                return edge1.getWeight() - edge2.getWeight();
            }
        });

        for (Edge e : edges) {
            priorityQueue.add(e);
        }

        DisjointSet disjointSet = new DisjointSet(graphPanel.getBiggestLabel() + 1);

        while (!priorityQueue.isEmpty()) {
            Edge currentEdge = priorityQueue.poll();
            int start = Integer.parseInt(currentEdge.getStart().getLabel());
            int end = Integer.parseInt(currentEdge.getEnd().getLabel());

            if (disjointSet.find(start) != disjointSet.find(end)) {
                disjointSet.union(start, end);
                minimumSpanningTree.add(currentEdge);
            }
        }

        System.out.println("The minimum spanning tree is: ");
        for (Edge e : minimumSpanningTree) {
            System.out.println(e.getStart().getLabel() + " " + e.getEnd().getLabel() + ":" + e.getWeight());
            e.select();
            Edge reverseEdge = graphPanel.getEdge(e.getEnd(), e.getStart());
            if(reverseEdge != null){
                reverseEdge.select();
            }
        }

        return minimumSpanningTree;
    }
}
