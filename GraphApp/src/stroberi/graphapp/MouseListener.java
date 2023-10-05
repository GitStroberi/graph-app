package stroberi.graphapp;

import javax.swing.*;
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
        int x = e.getX();
        int y = e.getY();

        boolean isOnNode = false;
        Node clickedNode = null;

        //check if the click is on a node
        for(Node node : graphPanel.getNodes()) {
            if(x >= node.getX()-25 && x <= node.getX()+25 && y >= node.getY()-25 && y <= node.getY()+25) {
                // click is on a node
                isOnNode = true;
                clickedNode = node;
            }
        }

        //if click is not on a node, create a new node
        if(!isOnNode) {
            Node newNode = new Node(x, y, graphPanel.nodeSize(), String.valueOf(graphPanel.nodeCount()));
            graphPanel.addNode(newNode);
        } else {
            //if click is on a node, select or unselect the node
            if(clickedNode.isSelected()) {
                clickedNode.unselect();
            } else {
                clickedNode.select();
            }
        }

        // Repaint the panel
        graphPanel.repaint();
    }
}
