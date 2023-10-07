package stroberi.graphapp;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrix {
    private List<List<Integer>> matrix = new ArrayList<List<Integer>>();

    private GraphPanel graphPanel;
    private ArrayList<Edge> edges;

    public AdjacencyMatrix(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    public void resizeMatrix(int newSize) {
        // Resize the matrix based on the new size
        int currentSize = matrix.size();
        if (newSize < currentSize) {
            // Remove rows and columns to reduce the size
            for (int i = currentSize - 1; i >= newSize; i--) {
                matrix.remove(i);
                for (List<Integer> row : matrix) {
                    row.remove(i);
                }
            }
        } else if (newSize > currentSize) {
            // Add rows and columns to increase the size
            for (int i = currentSize; i < newSize; i++) {
                List<Integer> newRow = new ArrayList<>();
                for (int j = 0; j < currentSize; j++) {
                    newRow.add(0); // You can initialize with any default value
                }
                matrix.add(newRow);
            }
            for (List<Integer> row : matrix) {
                for (int i = currentSize; i < newSize; i++) {
                    row.add(0); // You can initialize with any default value
                }
            }
        }
    }

    public void populateMatrix() {
        // Resize the matrix based on the current biggest label
        int biggestLabel = graphPanel.getBiggestLabel();
        resizeMatrix(biggestLabel + 1);

        // Populate the matrix with edges
        List<Edge> edges = graphPanel.getEdges();
        for (Edge edge : edges) {
            int start = Integer.parseInt(edge.getStart().getLabel());
            int end = Integer.parseInt(edge.getEnd().getLabel());
            printMatrix();
            matrix.get(start).set(end, 1);
            matrix.get(end).set(start, 1);
        }
    }

    public void printMatrix() {
        for (List<Integer> row : matrix) {
            for (Integer element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
        System.out.printf("\n");
    }
}
