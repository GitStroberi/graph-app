package stroberi.graphapp;

import java.awt.event.MouseAdapter;
public class MouseListener extends MouseAdapter{
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        System.out.println("Mouse clicked at " + e.getX() + ", " + e.getY());
    }
}
