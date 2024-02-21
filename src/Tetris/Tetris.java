// Define a package named Tetris.
package Tetris;

// Import necessary Java Swing and AWT classes for the GUI.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

// Define the Tetris class, which extends JFrame to create a window.
public class Tetris extends JFrame {

    // Declare a JLabel to display the game's status, such as the score.
    private JLabel statusbar;

    // Constructor for the Tetris class.
    public Tetris() {
        // Initialize the user interface.
        initUI();
    }

    // Method to initialize the user interface.
    private void initUI() {
        // Initialize the status bar with text "Score: 0".
        statusbar = new JLabel("Score: 0");
        // Add the status bar to the bottom (SOUTH) of the window.
        add(statusbar, BorderLayout.SOUTH);

        // Create a new Board instance, which is the game board.
        var board = new Board(this);
        // Add the game board to the window.
        add(board);
        // Start the game.
        board.start();

        // Set the window title to "TETRIS".
        setTitle("TETRIS");
        // Set the window size to 200 pixels wide and 400 pixels tall.
        setSize(200, 400);
        // Ensure the application exits when the window is closed.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Center the window relative to the screen.
        setLocationRelativeTo(null);
    }

    // Getter method for the status bar.
    JLabel getStatusBar() {
        return statusbar;
    }

    // Main method to run the application.
    public static void main(String[] args) {
        // Use the EventQueue to ensure that the GUI is constructed in the Event Dispatch Thread.
        EventQueue.invokeLater(() -> {
            // Create an instance of the Tetris game.
            var game = new Tetris();
            // Make the game window visible.
            game.setVisible(true);
        });
    }
}
