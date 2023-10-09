package stroberi.graphapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrix {
    private List<List<Integer>> matrix = new ArrayList<>();

    private GraphPanel graphPanel;
    private String filePath;

    public AdjacencyMatrix(GraphPanel graphPanel, String filePath) {
        this.graphPanel = graphPanel;
        this.filePath = filePath;
    }

    public void resizeMatrix(int newSize) {
        // Resize the matrix based on the new size
        int currentSize = matrix.size();
        if (newSize < currentSize) {
            // Remove rows and columns to reduce the size
            // System.out.println("newSize: " + newSize + " currentSize: " + currentSize);
            for (int i = currentSize - 1; i >= newSize; i--) {
                matrix.remove(i);
                for (List<Integer> row : matrix) {
                    row.remove(i);
                }
            }
        } else if (newSize > currentSize) {
            // Add rows and columns to increase the size
            // System.out.println("newSize: " + newSize + " currentSize: " + currentSize);
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

    public void removeEdgeFromMatrix(Edge edge) {
        //int biggestLabel = graphPanel.getBiggestLabel();
        //resizeMatrix(biggestLabel + 1);

        int start = Integer.parseInt(edge.getStart().getLabel());
        int end = Integer.parseInt(edge.getEnd().getLabel());

        //set both the start and end to 0 because the graph is undirected
        matrix.get(start).set(end, 0);

        if (!graphPanel.getIsDirected()) {
            matrix.get(end).set(start, 0);
        }
        //printMatrix();
        saveMatrixToFile();
    }

    public void addEdgeToMatrix(Edge edge) {
        //int biggestLabel = graphPanel.getBiggestLabel();
        //resizeMatrix(biggestLabel + 1);
        int start = Integer.parseInt(edge.getStart().getLabel());
        int end = Integer.parseInt(edge.getEnd().getLabel());

        //set both the start and end to 1 because the graph is undirected
        matrix.get(start).set(end, 1);
        if (!graphPanel.getIsDirected()) {
            matrix.get(end).set(start, 1);
        }
        //printMatrix();
        saveMatrixToFile();
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

    public void saveMatrixToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (List<Integer> row : matrix) {
                for (Integer element : row) {
                    writer.write(element + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
