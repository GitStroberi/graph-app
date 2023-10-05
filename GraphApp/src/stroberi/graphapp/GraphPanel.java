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

    public GraphPanel() throws IOException {

        String projectPath = System.getProperty("user.dir");
        try {
            prop.loadFromXML(new FileInputStream(projectPath + "/src/stroberi/graphapp/config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();

        MouseListener mouseListener = new MouseListener(this);
        addMouseListener(mouseListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the edges first so that the nodes will be on top of the edges
        for (Edge edge : edges) {
            Node start = edge.getStart();
            Node end = edge.getEnd();
            g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        }

        // Draw the nodes
        for (Node node : nodes) {
            g.drawOval(node.getX(), node.getY(), node.getRadius(), node.getRadius());
            g.drawString(node.getLabel(), node.getX(), node.getY());
        }
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public int nodeSize() {
        return Integer.parseInt(prop.getProperty("nodeSize"));
    }

    public int nodeCount() {
        return nodes.size();
    }
}