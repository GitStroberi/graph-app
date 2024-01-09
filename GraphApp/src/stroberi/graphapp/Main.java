package stroberi.graphapp;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int width = 800;
            int height = 600;
            JFrame frame = new JFrame("My Graph App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);

            GraphPanel graphPanel;
            try {
                graphPanel = new GraphPanel(width, height);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            frame.add(graphPanel);
        });
    }
}