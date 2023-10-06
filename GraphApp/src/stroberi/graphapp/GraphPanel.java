package stroberi.graphapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class GraphPanel extends JPanel{
    Properties prop = new Properties();
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    private ArrayList<Integer> availableLabels;
    private ArrayList<Node> selectedNodes;

    public GraphPanel() throws IOException {

        String projectPath = System.getProperty("user.dir");
        try {
            prop.loadFromXML(new FileInputStream(projectPath + "/src/stroberi/graphapp/config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
        availableLabels = new ArrayList<Integer>();
        selectedNodes = new ArrayList<Node>();

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
}