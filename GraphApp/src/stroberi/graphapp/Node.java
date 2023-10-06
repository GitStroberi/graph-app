package stroberi.graphapp;

public class Node {
    private int x;
    private int y;
    private int xOffset; // offset from mouse click to node center
    private int yOffset; // offset from mouse click to node center
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

    public void calculateOffsets(int x, int y) {
        this.xOffset = x - this.x;
        this.yOffset = y - this.y;
    }

    public void updatePosition() {
        this.x = this.x + this.xOffset;
        this.y = this.y + this.yOffset;
    }

    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public String getLabel() {
        return label;
    }
}