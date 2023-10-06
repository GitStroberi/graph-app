package stroberi.graphapp;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter{
    private GraphPanel graphPanel;
    private NodeManager nodeManager;

    public MouseListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.nodeManager = new NodeManager(graphPanel);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Get the node at the clicked position
        Node node = nodeManager.getNodeAt(x, y);

        // If there is no node at the clicked position, create a new node
        if(node == null) {
            node = nodeManager.createNode(x, y);
        }
        else {
            // If there is a node at the clicked position, toggle its selection
            nodeManager.toggleNodeSelection(node);

            // If there are two nodes selected, toggle the edge between them
            nodeManager.toggleEdge();
        }



        // Repaint the panel
        graphPanel.repaint();
    }
}
