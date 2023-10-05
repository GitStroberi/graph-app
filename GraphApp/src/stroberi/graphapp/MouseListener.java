package stroberi.graphapp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;

public class MouseListener extends MouseAdapter{

    private GraphPanel graphPanel;

    public MouseListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Get the click coordinates
        int x = e.getX();
        int y = e.getY();

        // Create a new Node at the clicked position and add it to the list
        Node newNode = new Node(x, y, graphPanel.nodeSize(), String.valueOf(graphPanel.nodeCount()));
        graphPanel.addNode(newNode);

        // Repaint the panel
        graphPanel.repaint();
    }
}
