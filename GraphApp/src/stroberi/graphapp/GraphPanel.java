package stroberi.graphapp;

import stroberi.graphapp.listeners.KeyboardListener;
import stroberi.graphapp.listeners.MouseListener;
import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.managers.NodeManager;
import stroberi.graphapp.models.AdjacencyList;
import stroberi.graphapp.models.AdjacencyMatrix;
import stroberi.graphapp.models.Edge;
import stroberi.graphapp.models.Node;
import stroberi.graphapp.utils.DisjointSet;

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

    private final AdjacencyList adjacencyList;
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
        adjacencyList = new AdjacencyList(this);
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
            else {
                drawEdgeWeight(g, edge, start.getX(), start.getY(), end.getX(), end.getY());
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

    public void drawEdgeWeight(Graphics g, Edge edge, int x1, int y1, int x2, int y2) {
        //if edge weight is 0, don't draw it
        if(edge.getWeight() == 0){
            return;
        }
        int weight = edge.getWeight();
        int x = (x1 + x2) / 2;
        int y = (y1 + y2) / 2;
        g.setColor(Color.WHITE);
        g.drawString(Integer.toString(weight), x, y);
        g.setColor(Color.BLACK);
    }

    public AdjacencyMatrix getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public AdjacencyList getAdjacencyList() {
        return adjacencyList;
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
        adjacencyList.addAdjacentNode(edge.getStart(), edge.getEnd());
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
        adjacencyList.removeAdjacentNode(edge.getStart(), edge.getEnd());
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
        visited[Integer.parseInt(n.getLabel())] = true;
        connectedComponent.add(n);
        ArrayList<Node> neighbors = getNeighboursDirected(n, edges);
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

    private void fillOrderIterative(Node startNode, boolean[] visited, Stack<Node> stack) {
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
        boolean[] visited = new boolean[biggestLabel + 1];
        boolean[] recursionStack = new boolean[biggestLabel + 1];

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
                fillOrderIterative(n, visited, stack);
            }
        }
        //print the topological sort
        System.out.println("The topological sort is: ");
        while(!stack.isEmpty()){
            System.out.print(stack.pop().getLabel() + " ");
        }
    }

    public ArrayList<Edge> getReverseEdges(){
        ArrayList<Edge> reverseEdges = new ArrayList<>();
        for(Edge e : edges){
            reverseEdges.add(new Edge(e.getEnd(), e.getStart()));
        }
        return reverseEdges;
    }

    private ArrayList<ArrayList<Node>> kosaraju(){
        Stack<Node> stack = new Stack<>();
        boolean[] visited = new boolean[biggestLabel + 1];

        for (Node node : nodes) {
            if (!visited[Integer.parseInt(node.getLabel())]) {
                fillOrderIterative(node, visited, stack);
            }
        }

        visited = new boolean[biggestLabel + 1];
        ArrayList<ArrayList<Node>> connectedComponents =  new ArrayList<>();

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
        ArrayList<ArrayList<Node>> connectedComponents = kosaraju();
        System.out.println("The strongly connected components are: ");
        for(ArrayList<Node> connectedComponent : connectedComponents){
            System.out.print("[");
            for(Node n : connectedComponent){
                System.out.print(n.getLabel() + " ");
            }
            System.out.println("]");
        }
    }

    // Helper method to find the index of the connected component containing a given node
    private int findConnectedComponentIndex(ArrayList<ArrayList<Node>> connectedComponents, Node node) {
        for (int i = 0; i < connectedComponents.size(); i++) {
            if (connectedComponents.get(i).contains(node)) {
                return i;
            }
        }
        return -1; // Node not found in any connected component
    }

    public void redrawAsSCCs(){
        ArrayList<ArrayList<Node>> connectedComponents = kosaraju();

        ArrayList<Node> newNodes = getSCCNodes(connectedComponents);

        //adjacency list of sccs as a map that takes in a node and as value has an array of the adjacent nodes
        //the node is the newly created SCC, and the adjacent nodes are the nodes that represent other SCCs and are adjacent to the current SCC
        HashMap<Node, ArrayList<Node>> adjacencyListSCC = new HashMap<>();

        // Create the adjacency list for the SCCs
        for (int i = 0; i < connectedComponents.size(); i++) {
            ArrayList<Node> adjacentNodes = new ArrayList<>();

            //iterate through all the edges that start from the nodes in the current SCC
            for (Edge edge : getEdges()) {
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

    public Node findRoot() {
        for(Node n : nodes){
            n.setColor(Color.WHITE);
        }
        this.repaint();
        if (!isDirected) {
            System.out.println("The graph is not directed. Arborescence requires a directed graph.");
            return null;
        }

        if (!isAcyclic()) {
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
            this.repaint();
            return potentialRoot;
        } else {
            System.out.println("The graph is not an arborescence.");
        }
        return null;
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

    public void setEdgeWeights() {
        //the graph must be undirected
        if(isDirected){
            System.out.println("The graph is directed. Edge weights can only be set for undirected graphs.");
            return;
        }
        //for each edge, ask the user to input the weight, and then set it
        ArrayList<Edge> edgesWithoutReverse = new ArrayList<>();
        for(Edge e : edges){
            if(!edgesWithoutReverse.contains(getEdge(e.getEnd(), e.getStart()))){
                edgesWithoutReverse.add(e);
            }
        }
        //for each edge, ask the user to input the weight, and then set it for both the edge and its reverse
        for(Edge e : edgesWithoutReverse){
            System.out.println("Enter the weight for edge " + e.getStart().getLabel() + " " + e.getEnd().getLabel() + ": ");
            int weight = scanner.nextInt();
            e.setWeight(weight);
            getEdge(e.getEnd(), e.getStart()).setWeight(weight);
        }
        this.repaint();
    }

    // Prim's algorithm
    public ArrayList<Edge> prim() {
        if (isDirected) {
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
        }

        return minimumSpanningTree;
    }

    // Kruskal's algorithm
    public ArrayList<Edge> kruskal() {
        if (isDirected) {
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

        DisjointSet disjointSet = new DisjointSet(nodes.size());

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
        }

        return minimumSpanningTree;
    }
}