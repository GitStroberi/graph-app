package stroberi.graphapp;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrix {
    private List<List<Integer>> matrix = new ArrayList<List<Integer>>();

    private GraphPanel graphPanel;
    private ArrayList<Edge> edges;

    public AdjacencyMatrix(int size, GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        for(int i = 0; i < size; i++) {
            List<Integer> row = new ArrayList<Integer>();
            for(int j = 0; j < size; j++) {
                row.add(0);
            }
            matrix.add(row);
        }
    }

    public void populateMatrix() {
        edges = graphPanel.getEdges();
        for(Edge edge : edges) {
            int start = Integer.parseInt(edge.getStart().getLabel());
            int end = Integer.parseInt(edge.getEnd().getLabel());
            matrix.get(start).set(end, 1);
            matrix.get(end).set(start, 1);
        }
    }

    public void printMatrix() {
        for(List<Integer> row : matrix) {
            for (Integer element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
        System.out.printf("\n");
    }
}
