package stroberi.graphapp.listeners;

import stroberi.graphapp.managers.EdgeManager;
import stroberi.graphapp.GraphPanel;
import stroberi.graphapp.models.Node;
import stroberi.graphapp.managers.NodeManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter{
    final private GraphPanel graphPanel;
    private final NodeManager nodeManager;
    private final EdgeManager edgeManager;
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

        if (!graphPanel.getMapMode()){
            selectedNode = nodeManager.getNodeAt(x, y);
            if (SwingUtilities.isLeftMouseButton(e)) {
                // If there is no node at the clicked position, create a new node
                if(selectedNode == null) {
                    //check if the click would create a node overlapping another node
                    if(nodeManager.isNodeNearby(x, y)) {
                        // System.out.println("Node would overlap another node");
                        return;
                    }
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
        else {
            selectedNode = nodeManager.getClosestNode(x, y);
            if (SwingUtilities.isLeftMouseButton(e)) {
                // If there is no node at the clicked position, create a new node
                if(selectedNode == null) {
                    //check if the click would create a node overlapping another node
                    if(nodeManager.isNodeNearby(x, y)) {
                        // System.out.println("Node would overlap another node");
                        return;
                    }
                }
                else {
                    // If there is a node at the clicked position, toggle its selection
                    nodeManager.toggleNodeSelection(selectedNode);

                    // If there are two nodes selected, toggle the edge between them
                    edgeManager.toggleRunDjikstra();
                }

                // Repaint the panel
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

            //check if the mouse position (with the offsets of the selected node) is inside another node
            double minDistance = getMinDistance(x, y);

            if(minDistance > selectedNode.getRadius()) {
                selectedNode.updatePosition(x, y);
            }
            else {
                // System.out.println("Node " + selectedNode.getLabel() + " is overlapping");
            }

            //repaint the panel
            graphPanel.repaint();
        }
    }

    public int getMinDistance (int x, int y) {
        int minDistance = Integer.MAX_VALUE;
        for(Node node : graphPanel.getNodes()) {
            if(node != selectedNode) {
                double distance = Math.sqrt(Math.pow(node.getX() - (x - selectedNode.getxOffset()), 2) + Math.pow(node.getY() - (y - selectedNode.getyOffset()), 2));
                if(distance < minDistance) {
                    minDistance = (int)distance;
                }
            }
        }
        return minDistance;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isRightMouseDragging) {
            isRightMouseDragging = false;
        }
    }
}
