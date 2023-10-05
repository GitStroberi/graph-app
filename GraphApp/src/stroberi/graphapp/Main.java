package stroberi.graphapp;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("My Graph App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);

            GraphPanel graphPanel = null;
            try {
                graphPanel = new GraphPanel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            frame.add(graphPanel);
        });
    }
}