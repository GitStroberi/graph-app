package stroberi.graphapp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class GraphPanel extends JPanel{
    Properties prop = new Properties();
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;
    private final AdjacencyMatrix adjacencyMatrix;
    private final ArrayList<Integer> availableLabels;
    private final ArrayList<Node> selectedNodes;
    private int biggestLabel;

    public GraphPanel() throws IOException {

        String projectPath = System.getProperty("user.dir");
        try {
            prop.loadFromXML(new FileInputStream(projectPath + "/src/stroberi/graphapp/config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        availableLabels = new ArrayList<>();
        selectedNodes = new ArrayList<>();
        adjacencyMatrix = new AdjacencyMatrix(prop.getProperty("matrixFilePath"));
        biggestLabel = -1;

        MouseListener mouseListener = new MouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
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
            drawArrow(g, start.getX(), start.getY(), arrowX, arrowY, arrowSize, 25);
        }

        // Draw the nodes
        for (Node node : nodes) {
            g.setColor(Color.WHITE);
            g.fillOval(node.getX()-25, node.getY()-25, node.getRadius(), node.getRadius());
            g.setColor(Color.BLACK);

            if(node.isSelected()) {
                g.setColor(Color.RED);
            }
            else {
                g.setColor(Color.BLACK);
            }
            g.drawOval(node.getX()-25, node.getY()-25, node.getRadius(), node.getRadius());

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
        edges.add(edge);
        adjacencyMatrix.addEdgeToMatrix(edge);
    }

    public Edge getEdge(Node start, Node end) {
        for (Edge edge : edges) {
            ///if(edge.getStart() == start && edge.getEnd() == end) {
            if(edge.getStart() == start && edge.getEnd() == end || edge.getStart() == end && edge.getEnd() == start) {
                    return edge;
            }
        }
        return null;
    }

    public void removeEdge(Edge edge) {
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
}