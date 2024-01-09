package stroberi.graphapp;

import stroberi.graphapp.listeners.KeyboardListener;
import stroberi.graphapp.listeners.MouseListener;
import stroberi.graphapp.managers.AlgorithmManager;
import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.managers.NodeManager;
import stroberi.graphapp.models.AdjacencyList;
import stroberi.graphapp.models.AdjacencyMatrix;
import stroberi.graphapp.models.Edge;
import stroberi.graphapp.models.Node;
import stroberi.graphapp.utils.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

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

    private final AlgorithmManager algorithmManager;

    private final Utilities utils;
    private final int nodeSize;
    private final Scanner scanner;

    private boolean mapMode = true;

    private double longitudeMax = 0;
    private double longitudeMin = Integer.MAX_VALUE;

    private double latitudeMax = 0;
    private double latitudeMin = Integer.MAX_VALUE;

    int width, height;
    public GraphPanel(int width, int height) throws IOException {
        this.width = width;
        this.height = height;

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
        adjacencyMatrix = new AdjacencyMatrix(this, prop.getProperty("matrixFilePath"), mapMode);
        adjacencyList = new AdjacencyList(this, mapMode);
        biggestLabel = -1;
        nodeSize = Integer.parseInt(prop.getProperty("nodeSize"));

        this.nodeManager = new NodeManager(this);
        this.edgeManager = new EdgeManager(this);
        this.algorithmManager = new AlgorithmManager(this);
        this.utils = new Utilities(this);

        MouseListener mouseListener = new MouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        KeyboardListener keyboardListener = new KeyboardListener(this);
        addKeyListener(keyboardListener);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();

        loadNodesAndEdgesFromFile(prop.getProperty("xmlFilePath"));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public double getLongitudeMax() {
        return longitudeMax;
    }

    public double getLongitudeMin() {
        return longitudeMin;
    }

    public double getLatitudeMax() {
        return latitudeMax;
    }

    public double getLatitudeMin() {
        return latitudeMin;
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
            if(edge.isSelected()){
                g.setColor(Color.YELLOW);
            }
            else {
                g.setColor(Color.WHITE);
            }
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
            if(!node.getShowLabel())
                continue;
            if(Integer.parseInt(label) < 10) {
                g.drawString(label, node.getX()-4, node.getY()+5);
            } else {
                g.drawString(label, node.getX()-9, node.getY()+5);
            }
        }
    }

    public void loadNodesAndEdgesFromFile(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            // Load nodes
            NodeList nodeList = doc.getElementsByTagName("node");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element nodeElement = (Element) nodeList.item(i);
                int id = Integer.parseInt(nodeElement.getAttribute("id"));
                double longitude = Double.parseDouble(nodeElement.getAttribute("longitude"));
                double latitude = Double.parseDouble(nodeElement.getAttribute("latitude"));

                //find the max and min longitude and latitude
                if(longitude > longitudeMax){
                    longitudeMax = longitude;
                }
                if(longitude < longitudeMin){
                    longitudeMin = longitude;
                }
                if(latitude > latitudeMax){
                    latitudeMax = latitude;
                }
                if(latitude < latitudeMin){
                    latitudeMin = latitude;
                }

                //cast the double to an int
                int x = (int)longitude;
                int y = (int)latitude;

                // Create Node object and add it to the list
                Node node = new Node(x, y, 0, Integer.toString(id));
                node.setShowLabel(false);
                nodes.add(node);
            }

            //Print out the max and min longitude and latitude
            System.out.println("Max longitude: " + longitudeMax);
            System.out.println("Min longitude: " + longitudeMin);

            //Reposition the nodes based on the map
            for(Node n : nodes){
                n.setPosition(utils.mapLongitudeToX(n.getX()), utils.mapLatitudeToY(n.getY()));
            }

            // Load edges (arcs)
            NodeList arcList = doc.getElementsByTagName("arc");
            for (int i = 0; i < arcList.getLength(); i++) {
                Element arcElement = (Element) arcList.item(i);
                int fromNodeId = Integer.parseInt(arcElement.getAttribute("from"));
                int toNodeId = Integer.parseInt(arcElement.getAttribute("to"));

                //cast the int to a string
                String fromNodeIdString = Integer.toString(fromNodeId);
                String toNodeIdString = Integer.toString(toNodeId);

                // Get Node objects based on IDs
                Node startNode = nodeManager.getNodeByLabel(fromNodeIdString);
                Node endNode = nodeManager.getNodeByLabel(toNodeIdString);

                if (startNode != null && endNode != null) {
                    // Create Edge object and add it to the list
                    Edge edge = new Edge(startNode, endNode);
                    edges.add(edge);
                }
            }

            // Repaint the panel after loading nodes and edges
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as needed
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

    public int getNodeSize() {
        return nodeSize;
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
                    return edge;
            }
        }
        return null;
    }

    public void removeEdge(Edge edge) {
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

    public AlgorithmManager getAlgorithmManager() {
        return algorithmManager;
    }
    public Utilities getUtils() {
        return utils;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public ArrayList<Edge> getReverseEdges(){
        ArrayList<Edge> reverseEdges = new ArrayList<>();
        for(Edge e : edges){
            reverseEdges.add(new Edge(e.getEnd(), e.getStart()));
        }
        return reverseEdges;
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
}