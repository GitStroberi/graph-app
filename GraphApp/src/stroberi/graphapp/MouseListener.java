package stroberi.graphapp;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter{
    private GraphPanel graphPanel;
    private NodeManager nodeManager;
    private EdgeManager edgeManager;
    private Node selectedNode;

    public MouseListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.nodeManager = new NodeManager(graphPanel);
        this.edgeManager = new EdgeManager(graphPanel, nodeManager);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Get the node at the clicked position
        selectedNode = nodeManager.getNodeAt(x, y);

        if (SwingUtilities.isLeftMouseButton(e)) {
            // If there is no node at the clicked position, create a new node
            if(selectedNode == null) {
                nodeManager.createNode(x, y);
            }
            else {
                // If there is a node at the clicked position, toggle its selection
                nodeManager.toggleNodeSelection(selectedNode);

                // If there are two nodes selected, toggle the edge between them
                nodeManager.toggleEdge();
            }

            // Repaint the panel
            graphPanel.repaint();
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            //calculate the offset of the mouse click from the node's position
            selectedNode.calculateOffsets(x, y);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e){
        if(selectedNode != null && SwingUtilities.isRightMouseButton(e)) {
            int x = e.getX();
            int y = e.getY();

            //update the node's position
            selectedNode.updatePosition();

            //repaint the panel
            graphPanel.repaint();
        }
    }
}
