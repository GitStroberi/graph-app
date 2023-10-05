package stroberi.graphapp;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My Graph App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        GraphPanel graphPanel = new GraphPanel();
        frame.add(graphPanel);

        // Add some nodes to your graph panel.
        Node a = new Node(100, 100, 50, "A");
        Node b = new Node(200, 200, 50, "B");
        graphPanel.addNode(a);
        graphPanel.addNode(b);

        frame.setVisible(true);
    }
}