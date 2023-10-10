package stroberi.graphapp.listeners;

import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.GraphPanel;
import stroberi.graphapp.models.Node;
import stroberi.graphapp.managers.NodeManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter{
    private GraphPanel graphPanel;
    private NodeManager nodeManager;
    private EdgeManager edgeManager;
    private Node selectedNode;

    private boolean isRightMouseDragging = false;

    public MouseListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.nodeManager = graphPanel.getNodeManager();
        this.edgeManager = graphPanel.getEdgeManager();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        //System.out.println(graphPanel.getAvailableLabels());
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
                edgeManager.toggleEdge();
            }

            // Repaint the panel
            graphPanel.repaint();
        }
        else if(SwingUtilities.isRightMouseButton(e)) {
            // If there is a node at the clicked position, remove it
            if (selectedNode != null) {
                edgeManager.removeEdges(selectedNode);
                nodeManager.removeNode(selectedNode);
                graphPanel.repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        // Get the node at the clicked position
        selectedNode = nodeManager.getNodeAt(x, y);
        /*
        if(selectedNode != null)
            System.out.println("Selected node: " + selectedNode.getLabel());
        */

        if (SwingUtilities.isRightMouseButton(e) && selectedNode != null) {
            //calculate the offset of the mouse click from the node's position
            selectedNode.calculateOffsets(x, y);
            isRightMouseDragging = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e){
        if(isRightMouseDragging) {
            int x = e.getX();
            int y = e.getY();

            //System.out.println("Dragging node " + selectedNode.getLabel());
            //update the node's position

            //check if the node is dragged outside the panel
            if(x < 0) {
                x = 0;
            }
            else if(x > graphPanel.getWidth()) {
                x = graphPanel.getWidth();
            }
            if(y < 0) {
                y = 0;
            }
            else if(y > graphPanel.getHeight()) {
                y = graphPanel.getHeight();
            }
            selectedNode.updatePosition(x, y);

            //repaint the panel
            graphPanel.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isRightMouseDragging) {
            isRightMouseDragging = false;
        }
    }
}