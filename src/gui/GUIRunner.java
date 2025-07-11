package gui;

import javax.swing.*;

public class GUIRunner {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Disable console output from Firebase
        System.setProperty("java.util.logging.config.file", "logging.properties");
        
        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting Codevia GUI...");
            new CodeviaGUI().setVisible(true);
        });
    }
}
