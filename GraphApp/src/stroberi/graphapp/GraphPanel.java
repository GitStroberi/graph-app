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
                for(Node neighbor : adjacencyMatrix.getNeighborsUndirected(current)){
                    stack.push(neighbor);
                }
            }
        }
    }

    private void fillOrder(AdjacencyMatrix matrix, Node node, boolean[] visited, Stack<Node> stack) {
        visited[Integer.parseInt(node.getLabel())] = true;
        for (Node neighbor : matrix.getNeighborsDirected(node)) {
            if (!visited[Integer.parseInt(neighbor.getLabel())]) {
                fillOrder(matrix, neighbor, visited, stack);
            }
        }
        stack.push(node);
    }

    private void dfs(AdjacencyMatrix matrix, Node node, boolean[] visited, ArrayList<Node> connectedComponent) {
        visited[Integer.parseInt(node.getLabel())] = true;
        connectedComponent.add(node);
        for (Node neighbor : matrix.getNeighborsDirected(node)) {
            if (!visited[Integer.parseInt(neighbor.getLabel())]) {
                dfs(matrix, neighbor, visited, connectedComponent);
            }
        }
    }

    private ArrayList<ArrayList<Node>> kosaraju(AdjacencyMatrix matrix){
        Stack<Node> stack = new Stack<>();
        boolean[] visited = new boolean[matrix.getSize()];

        for (Node node : nodes) {
            if (!visited[Integer.parseInt(node.getLabel())]) {
                fillOrder(matrix, node, visited, stack);
            }
        }

        AdjacencyMatrix transposedMatrix = matrix.transpose();
        ArrayList<ArrayList<Node>> connectedComponents = new ArrayList<>();
        visited = new boolean[matrix.getSize()];

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (!visited[Integer.parseInt(node.getLabel())]) {
                ArrayList<Node> connectedComponent = new ArrayList<>();
                dfs(transposedMatrix, node, visited, connectedComponent);
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

        //adjacency list for the SCCs to remember which SCCs are connected
        ArrayList<ArrayList<Integer>> adjacencyListSCC = new ArrayList<>();

        //if an edge exists between SCC A and SCC B, mark in the adjacency list that A is adjacent to B
        for(ArrayList<Node> CC : connectedComponents){
            ArrayList<Integer> adjacentSCCs = new ArrayList<>();
            for(Node n : CC){
                for(Node neighbor : adjacencyMatrix.getNeighborsDirected(n)){
                    if(!CC.contains(neighbor)){
                        int SCC = -1;
                        //see in which SCC the node
                        for(int i = 0; i < connectedComponents.size(); i++){
                            if(connectedComponents.get(i).contains(neighbor)){
                                SCC = i;
                                break;
                            }
                        }
                        if(!adjacentSCCs.contains(SCC)){
                            adjacentSCCs.add(SCC);
                        }
                    }
                }
            }
            adjacencyListSCC.add(adjacentSCCs);
        }

        nodeManager.removeAllNodes();

        //add the new nodes to the graph
        for(Node n : newNodes){
            nodeManager.createNode(n.getX(), n.getY());
        }

        System.out.println("Created the new nodes:");
        for(Node n : newNodes){
            System.out.println(n.getLabel());
        }

        //add the edges between the new nodes, based on the adjacency list (now each node label is the index in the adjacency list)
        for(int i = 0; i < adjacencyListSCC.size(); i++){
            for(int j = 0; j < adjacencyListSCC.get(i).size(); j++){
                int SCC2 = adjacencyListSCC.get(i).get(j);
                if(i != SCC2){
                    System.out.println("Adding edge between " + i + " and " + SCC2);
                    Edge edge = new Edge(this.nodeManager.getNodeByLabel(Integer.toString(i)), this.nodeManager.getNodeByLabel(Integer.toString(SCC2)));
                    addEdge(edge);
                }
            }
        }

        this.repaint();
        /*
        //convert the adjacency list into the newEdges array
        for(int i = 0; i < adjacencyListSCC.size(); i++){
            for(int j = 0; j < adjacencyListSCC.get(i).size(); j++){
                int SCC1 = i;
                int SCC2 = adjacencyListSCC.get(i).get(j);
                if(SCC1 != SCC2){
                    Edge edge = new Edge(newNodes.get(SCC1), newNodes.get(SCC2));
                    addEdge(edge);
                }
            }
        }*/
    }

    private ArrayList<Node> getSCCNodes(ArrayList<ArrayList<Node>> connectedComponents) {
        ArrayList<Node> newNodes = new ArrayList<>();

        int newLabel = 0;

        //create the new nodes by finding the center of each connected component
        for(ArrayList<Node> connectedComponent : connectedComponents){
            int x = 0;
            int y = 0;
            for(Node n : connectedComponent){
                x += n.getX();
                y += n.getY();
            }
            x /= connectedComponent.size();
            y /= connectedComponent.size();
            Node newNode = new Node(x, y, nodeSize, Integer.toString(newLabel++));
            newNodes.add(newNode);
        }
        return newNodes;
    }
}