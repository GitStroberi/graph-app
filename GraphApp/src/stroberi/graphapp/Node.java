package stroberi.graphapp;

public class Node {
    private int x;
    private int y;
    private int radius;
    private String label;

    private boolean selected = false;

    public Node(int x, int y, int radius, String label) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.label = label;
    }

    public void select() {
        System.out.println("Node " + this.getLabel() + " selected");
        selected = true;
    }

    public void unselect() {
        System.out.println("Node " + this.getLabel() + " unselected");
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public String getLabel() {
        return label;
    }
}