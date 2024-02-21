// Package declaration for organizational purposes.
package Tetris;

// Import statements for utilizing other classes and handling user input and graphical interface.
import Tetris.Shape.Tetrominoe;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Board class definition, extending JPanel for graphical representation.
public class Board extends JPanel {

    // Constants for the board's dimensions and the timer interval.
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 300;

    // Fields for game state management.
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;

    // Constructor that initializes the board.
    public Board(Tetris parent) {
        initBoard(parent);
    }

    // Initializes board settings and key listener.
    private void initBoard(Tetris parent) {
        setFocusable(true);
        setBackground(new Color(0, 0, 0)); // Set background color to black.
        statusbar = parent.getStatusBar(); // Access the Tetris game's status bar for updates.
        addKeyListener(new TAdapter()); // Add a key listener for controlling pieces.
    }

    // Calculates the width of one square based on the board width.
    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    // Calculates the height of one square based on the board height.
    private int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    // Returns the shape at the specified coordinates on the board.
    private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    // Starts the game by clearing the board, creating a new piece, and starting the timer.
    void start() {
        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        newPiece();
        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    // Toggles the pause state and updates the status bar accordingly.
    private void pause() {
        isPaused = !isPaused;
        if (isPaused) {
            statusbar.setText("paused");
        } else {
            statusbar.setText(String.format("Score: %d", numLinesRemoved));
        }
        repaint();
    }

    // Overrides the JPanel's paintComponent method to perform custom drawing.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    // Handles the actual drawing of Tetrominoes on the board.
    private void doDrawing(Graphics g) {
        var size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetrominoe.NoShape) {
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
            }
        }
    }

    // Moves the current piece all the way down until it hits something.
    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            newY--;
        }
        pieceDropped();
    }

    // Moves the current piece one line down if possible.
    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    // Clears the board by setting all squares to NoShape.
    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Tetrominoe.NoShape;
        }
    }

    // Handles actions to take immediately after a piece has been dropped.
    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        removeFullLines();
        if (!isFallingFinished) {
            newPiece();
        }
    }

    // Creates a new piece and places it in the starting position.
    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);
            timer.stop();
            statusbar.setText(String.format("Game over. Score: %d", numLinesRemoved));
        }
    }

    // Attempts to move the current piece to a new location.
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT || shapeAt(x, y) != Tetrominoe.NoShape) {
                return false;
            }
        }
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    // Removes full lines from the board and updates the score.
    private void removeFullLines() {
        int numFullLines = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }
        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.format("Score: %d", numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }

    // Draws a single square of a tetromino with colors depending on its shape.
    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {
        Color colors[] = {
            new Color(0, 0, 0), new Color(255, 255, 255), new Color(255, 0, 255),
            new Color(0, 255, 255), new Color(0, 0, 255), new Color(255, 255, 0),
            new Color(255, 105, 180), new Color(0, 255, 0), new Color(128, 0, 128)
        };
        var color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    // Inner class to handle the game cycle's actions.
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    // Performs a single game cycle step, updating game state and repainting.
    private void doGameCycle() {
        update();
        repaint();
    }

    // Updates the game state, checking for game pause, piece fall completion, and moving the piece down.
    private void update() {
        if (isPaused) {
            return;
        }
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    // KeyAdapter subclass to handle keyboard inputs for controlling the game.
    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (curPiece.getShape() == Tetrominoe.NoShape) {
                return; // No action if there's no current piece.
            }
            int keycode = e.getKeyCode();
            switch (keycode) {
                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }
}
