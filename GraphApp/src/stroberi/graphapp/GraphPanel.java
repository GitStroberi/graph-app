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
        g.setFont(new Font("TimesRoman", Font.PLAIN, 16));

        // Draw the edges first so that the nodes will be on top of the edges
        for (Edge edge : edges) {
            Node start = edge.getStart();
            Node end = edge.getEnd();
            g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        }

        // Draw the nodes
        for (Node node : nodes) {
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