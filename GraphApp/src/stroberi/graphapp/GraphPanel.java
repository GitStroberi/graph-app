package stroberi.graphapp;

import stroberi.graphapp.listeners.KeyboardListener;
import stroberi.graphapp.listeners.MouseListener;
import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.managers.NodeManager;
import stroberi.graphapp.models.AdjacencyMatrix;
import stroberi.graphapp.models.Edge;
import stroberi.graphapp.models.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class GraphPanel extends JPanel{
    Properties prop = new Properties();
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;
    private final ArrayList<ArrayList<Node>> connectedComponents = new ArrayList<>();
    private final AdjacencyMatrix adjacencyMatrix;
    private final ArrayList<Integer> availableLabels;
    private final ArrayList<Node> selectedNodes;
    private int biggestLabel;
    private boolean isDirected = false;
    private final NodeManager nodeManager;
    private final EdgeManager edgeManager;
    private final int nodeSize;
    private final Scanner scanner;
    public GraphPanel() throws IOException {

        String projectPath = System.getProperty("user.dir");
        try {
            prop.loadFromXML(new FileInputStream(projectPath + "/src/resources/config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner = new Scanner(System.in);
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        availableLabels = new ArrayList<>();
        selectedNodes = new ArrayList<>();
        adjacencyMatrix = new AdjacencyMatrix(this, prop.getProperty("matrixFilePath"));
        biggestLabel = -1;
        nodeSize = Integer.parseInt(prop.getProperty("nodeSize"));

        this.nodeManager = new NodeManager(this);
        this.edgeManager = new EdgeManager(this);

        MouseListener mouseListener = new MouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        KeyboardListener keyboardListener = new KeyboardListener(this);
        addKeyListener(keyboardListener);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        super.setBackground(Color.DARK_GRAY);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 16));

        // Draw the edges first so that the nodes will be on top of the edges
        for (Edge edge : edges) {
            Node start = edge.getStart();
            Node end = edge.getEnd();
            g.setColor(Color.WHITE);
            g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
            g.setColor(Color.BLACK);

            int arrowSize = 10; // Adjust the size as needed
            int arrowX = end.getX();
            int arrowY = end.getY();

            if(isDirected){
                drawArrow(g, start.getX(), start.getY(), arrowX, arrowY, arrowSize, nodeSize/2);
            }
        }

        // Draw the nodes
        for (Node node : nodes) {
            g.setColor(node.getColor());
            g.fillOval(node.getX()-nodeSize/2, node.getY()-nodeSize/2, node.getRadius(), node.getRadius());
            g.setColor(Color.BLACK);

            if(node.isSelected()) {
                g.setColor(Color.RED);
            }
            else {
                g.setColor(Color.BLACK);
            }
            g.drawOval(node.getX()-nodeSize/2, node.getY()-nodeSize/2, node.getRadius(), node.getRadius());

            String label = node.getLabel();
            if(Integer.parseInt(label) < 10) {
                g.drawString(label, node.getX()-4, node.getY()+5);
            } else {
                g.drawString(label, node.getX()-9, node.getY()+5);
            }
        }
    }

    private void drawArrow(Graphics g, int x1, int y1, int x2, int y2, int size, int distFromNode) {
        Graphics2D g2 = (Graphics2D) g;

        // Save the current transformation state
        AffineTransform savedTransform = g2.getTransform();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g2.transform(at);

        // Draw the arrowhead
        //g2.drawLine(25, 0, len-25, 0);
        g2.setColor(Color.WHITE);
        g2.fillPolygon(
                new int[] { len-distFromNode, len - size - distFromNode, len - size - distFromNode, len - distFromNode },
                new int[] { 0, -size, size, 0 }, 4);
        g2.setColor(Color.BLACK);

        // Restore the previous transformation state
        g2.setTransform(savedTransform);
    }

    public AdjacencyMatrix getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void removeNode(Node node) {
        nodes.remove(node);
    }

    public void addEdge(Edge edge) {
        //if edge already exists, don't add it
        for (Edge e : edges) {
            if(e.getStart() == edge.getStart() && e.getEnd() == edge.getEnd()) {
                return;
            }
        }
        edges.add(edge);
        adjacencyMatrix.addEdgeToMatrix(edge);
    }

    public Edge getEdge(Node start, Node end) {
        for (Edge edge : edges) {
            if(edge.getStart() == start && edge.getEnd() == end) {
            ///if(edge.getStart() == start && edge.getEnd() == end || edge.getStart() == end && edge.getEnd() == start) {
                    return edge;
            }
        }
        return null;
    }

    public void  removeEdge(Edge edge) {
        edges.remove(edge);
        adjacencyMatrix.removeEdgeFromMatrix(edge);
    }

    public int nodeSize() {
        return Integer.parseInt(prop.getProperty("nodeSize"));
    }
    public int nodeCount() {
        return nodes.size();
    }
    public ArrayList<Node> getSelectedNodes() {
        return selectedNodes;
    }
    public ArrayList<Integer> getAvailableLabels() {
        return availableLabels;
    }
    public void addAvailableLabel(int label) {
        //check if the label is already in the available labels
        if(availableLabels.contains(label)){
            return;
        }
        availableLabels.add(label);
    }
    public void removeAvailableLabel(int label) {
        availableLabels.remove((Integer) label);
    }
    public void setBiggestLabel(int label) {
        biggestLabel = label;
    }
    public int getBiggestLabel() {
        return biggestLabel;
    }
    public void toggleGraphMode() {
        isDirected = !isDirected;
        this.repaint();
        // System.out.println("Graph mode: " + (isDirected ? "Directed is true" : "Directed is false"));
    }
    public boolean getIsUndirected() {
        return !isDirected;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public EdgeManager getEdgeManager() {
        return edgeManager;
    }

    public Scanner getScanner() {
        return scanner;
    }

    private Color getRandomColor() {
        // Generate a random color (you can customize this logic)
        return new Color((int) (Math.random() * 0x1000000));
    }

    public ArrayList<Node> getNeighbours(Node node){
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

    //function that finds the connected components of the graph (implement dfs in a later function)
    public void findConnectedComponents(){
        //reset the connected components
        connectedComponents.clear();
        //reset the colors of the nodes
        for(Node n : nodes){
            n.setColor(Color.WHITE);
        }
        //find the connected components
        for(Node n : nodes){
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
            Color color = getRandomColor();
            for(Node n : connectedComponent){
                n.setColor(color);
            }
        }
        //repaint the graph
        this.repaint();
    }
    private void dfsUndirected(Node n, ArrayList<Node> connectedComponent){
        Stack<Node> stack = new Stack<>();
        stack.push(n);
        while(!stack.isEmpty()){
            Node current = stack.pop();
            if(current.getColor() == Color.WHITE){
                current.setColor(Color.GRAY);
                connectedComponent.add(current);
                for(Node neighbor : getNeighbours(current)){
                    stack.push(neighbor);
                }
            }
        }
    }

    private void dfsDirected(Node n, ArrayList<Edge> edges, ArrayList<Node> connectedComponent, boolean[] visited){
        //iterative dfs
        //visit the node
        visited[Integer.parseInt(n.getLabel())] = true;
        //add the node to the connected component
        connectedComponent.add(n);
        //get the neighbors of the node
        ArrayList<Node> neighbors = getNeighboursDirected(n, edges);
        //for each neighbor, if it hasn't been visited, visit it
        for(Node neighbor : neighbors) {
            if (!visited[Integer.parseInt(neighbor.getLabel())]) {
                dfsDirected(neighbor, edges, connectedComponent, visited);
            }
        }
    }

    private void fillOrder(AdjacencyMatrix matrix, Node node, boolean[] visited, Stack<Node> stack) {
        visited[Integer.parseInt(node.getLabel())] = true;
        for (Node neighbor : getNeighboursDirected(node, getEdges())) {
            if (!visited[Integer.parseInt(neighbor.getLabel())]) {
                fillOrder(matrix, neighbor, visited, stack);
            }
        }
        stack.push(node);

        //print the stack
        System.out.println("The recursive stack is: ");
        for(Node n : stack){
            System.out.print(n.getLabel() + " ");
        }
    }

    private void fillOrderIterative(AdjacencyMatrix matrix, Node startNode, boolean[] visited, Stack<Node> stack) {
        Stack<Node> callStack = new Stack<>();
        callStack.push(startNode);

        while(!callStack.isEmpty()) {
            Node node = callStack.pop();
            if(!visited[Integer.parseInt(node.getLabel())]) {
                visited[Integer.parseInt(node.getLabel())] = true;
                callStack.push(node);
                for(Node neighbor : getNeighboursDirected(node, getEdges())) {
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

            for (Node neighbor : getNeighboursDirected(node, getEdges())) {
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
        boolean[] visited = new boolean[nodes.size()];
        boolean[] recursionStack = new boolean[nodes.size()];

        for (Node node : nodes) {
            if (!visited[Integer.parseInt(node.getLabel())]) {
                if (isCyclicUtil(node, visited, recursionStack)) {
                    return false; // If a cycle is detected, the graph is not acyclic
                }
            }
        }

        return true; // If no cycles are detected, the graph is acyclic
    }

    public void topologicalSort(){
        //check if the graph is directed
        if(!isDirected){
            System.out.println("The graph is not directed, so it cannot be topologically sorted.");
            return;
        }
        //if the graph is not acyclic, then it cannot be topologically sorted
        if(!isAcyclic()){
            System.out.println("The graph is not acyclic, so it cannot be topologically sorted.");
            return;
        }
        //the topological sort can be done by using the fillOrder function and then reversing the stack
        Stack<Node> stack = new Stack<>();
        boolean[] visited = new boolean[adjacencyMatrix.getSize()];
        for(Node n : nodes){
            if(!visited[Integer.parseInt(n.getLabel())]){
                fillOrderIterative(adjacencyMatrix, n, visited, stack);
            }
        }
        //print the topological sort
        System.out.println("The topological sort is: ");
        while(!stack.isEmpty()){
            System.out.print(stack.pop().getLabel() + " ");
        }
    }

    public void findAndPrintRoot() {
        if (isAcyclic() && isQuasiStronglyConnected()) {
            Node rootNode = findRootNode();
            if (rootNode != null) {
                System.out.println("The root node of the arborescence is: " + rootNode.getLabel());
            } else {
                System.out.println("The graph is acyclic and quasi-strongly connected, but the root node could not be determined.");
            }
        } else {
            System.out.println("The graph is not an arborescence.");
        }
    }

    public boolean isQuasiStronglyConnected() {
        for (Node startNode : nodes) {
            for (Node endNode : nodes) {
                if (startNode != endNode) {
                    if (!isReachable(startNode, endNode) && !isReachable(endNode, startNode)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isReachable(Node start, Node end) {
        boolean[] visited = new boolean[nodes.size()];
        Stack<Node> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Node currentNode = stack.pop();
            int currentNodeIndex = Integer.parseInt(currentNode.getLabel());

            if (!visited[currentNodeIndex]) {
                visited[currentNodeIndex] = true;

                for (Node neighbor : getNeighboursDirected(currentNode, getEdges())) {
                    stack.push(neighbor);

                    if (neighbor == end) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Node findRootNode() {
        for (Node node : nodes) {
            if (isRoot(node)) {
                return node;
            }
        }
        return null;
    }

    private boolean isRoot(Node node) {
        for (Node otherNode : nodes) {
            if (node != otherNode && isReachable(otherNode, node)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Edge> getReverseEdges(){
        ArrayList<Edge> reverseEdges = new ArrayList<>();
        for(Edge e : edges){
            reverseEdges.add(new Edge(e.getEnd(), e.getStart()));
        }
        return reverseEdges;
    }

    private ArrayList<ArrayList<Node>> kosaraju(AdjacencyMatrix matrix){
        Stack<Node> stack = new Stack<>();
        boolean[] visited = new boolean[matrix.getSize()];

        for (Node node : nodes) {
            if (!visited[Integer.parseInt(node.getLabel())]) {
                fillOrderIterative(matrix, node, visited, stack);
            }
        }

        visited = new boolean[matrix.getSize()];
        ArrayList<ArrayList<Node>> connectedComponents = new ArrayList<>();

        while (!stack.isEmpty()) {
            Node currentNode = stack.pop();
            if (!visited[Integer.parseInt(currentNode.getLabel())]) {
                ArrayList<Node> connectedComponent = new ArrayList<>();
                dfsDirected(currentNode, getReverseEdges(), connectedComponent, visited);
                connectedComponents.add(connectedComponent);
            }
        }

        return connectedComponents;
    }

    public void displaySCCs(){
        ArrayList<ArrayList<Node>> connectedComponents = kosaraju(adjacencyMatrix);
        System.out.println("The strongly connected components are: ");
        for(ArrayList<Node> connectedComponent : connectedComponents){
            System.out.print("[");
            for(Node n : connectedComponent){
                System.out.print(n.getLabel() + " ");
            }
            System.out.println("]");
        }
    }

    public void redrawAsSCCs(){
        ArrayList<ArrayList<Node>> connectedComponents = kosaraju(adjacencyMatrix);

        ArrayList<Node> newNodes = getSCCNodes(connectedComponents);

        //adjacency list of sccs as a map that takes in a node and as value has an array of the adjacent nodes
        //the node is the newly created SCC, and the adjacent nodes are the nodes that represent other SCCs and are adjacent to the current SCC
        HashMap<Node, ArrayList<Node>> adjacencyListSCC = new HashMap<>();

        //we need to iterate

        //create the adjacency list for the SCCs
        for(int i = 0; i < connectedComponents.size(); i++){
            ArrayList<Node> adjacentNodes = new ArrayList<>();
            for(int j = 0; j < connectedComponents.size(); j++){
                if(i != j){
                    //we need to check if there is an edge between the nodes in the two SCCs. stop when we find one
                    for(Node n1 : connectedComponents.get(i)){
                        for(Node n2 : connectedComponents.get(j)){
                            if(adjacencyMatrix.getEdge(n1, n2) != null){
                                adjacentNodes.add(newNodes.get(j));
                                break;
                            }
                        }
                    }
                }
            }
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

        nodeManager.removeAllNodes();

        //add the new nodes to the graph array, and remove those labels from the available labels
        for(Node n : newNodes){
            nodeManager.createNode(n.getX(), n.getY());
        }

        System.out.println("The new nodes are: ");
        for(Node n : nodes){
            System.out.print(n.getLabel() + ", ");
        }

        //add the edges between the new nodes, based on the adjacency list
        for(Node n : adjacencyListSCC.keySet()){
            for(Node neighbor : adjacencyListSCC.get(n)){
                if(n != neighbor){
                    //select nodes placed at the same position as n and neighbor
                    Node n1 = nodeManager.getNodeAt(n.getX(), n.getY());
                    Node n2 = nodeManager.getNodeAt(neighbor.getX(), neighbor.getY());
                    if(n1 != null && n2 != null){
                        nodeManager.toggleNodeSelection(n1);
                        nodeManager.toggleNodeSelection(n2);
                        edgeManager.createEdge();
                    }
                }
            }
        }
        this.repaint();
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
            Node newNode = new Node(x, y, nodeSize, Integer.toString(newLabel));
            newNodes.add(newNode);
        }
        return newNodes;
    }
}