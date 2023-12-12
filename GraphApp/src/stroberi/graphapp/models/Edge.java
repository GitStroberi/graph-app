package stroberi.graphapp.models;

public class Edge {
    private final Node start;
    private final Node end;

    private int weight = 0;

    public Edge(Node start, Node end) {
        this.start = start;
        this.end = end;
    }
    
    public Node getStart() {
        return start;
    }
    public Node getEnd() {
        return end;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}