package stroberi.graphapp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
public class GraphPanel extends JPanel {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public GraphPanel() {
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();

        Node a = new Node(100, 100, 50, "A");
        Node b = new Node(200, 200, 50, "B");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Edge edge : edges) {
            Node start = edge.getStart();
            Node end = edge.getEnd();
            g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        }
        for (Node node : nodes) {
            g.drawOval(node.getX(), node.getY(), node.getRadius(), node.getRadius());
            g.drawString(node.getLabel(), node.getX(), node.getY());

            System.out.println(node.getLabel());
        }
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}