// Package declaration for the Tetris game.
package Tetris;

// Import the Random class for generating random shapes.
import java.util.Random;

// Definition of the Shape class.
public class Shape {

    // Enumeration of the different Tetromino shapes including a no shape state.
    protected enum Tetrominoe { NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape }

    // Array to hold the coordinates for the current shape.
    private Tetrominoe pieceShape;
    private int coords[][];
    
    // Table containing coordinate arrays for all Tetromino shapes.
    private int[][][] coordsTable;

    // Constructor for the Shape class.
    public Shape() {
        initShape();
    }

    // Initializes the shape.
    private void initShape() {
        // Initializes the coordinates array for holding the current shape's coordinates.
        coords = new int[4][2];

        // Initializes the coordsTable with coordinates for all possible Tetromino shapes.
        coordsTable = new int[][][] {
            { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
            { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
            { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
            { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
            { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
            { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };

        // Sets the shape to NoShape initially.
        setShape(Tetrominoe.NoShape);
    }

    // Sets the current shape and updates the coordinates array.
    protected void setShape(Tetrominoe shape) {
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }

    // Setter for the x coordinate of a point in the current shape.
    private void setX(int index, int x) { coords[index][0] = x; }
    // Setter for the y coordinate of a point in the current shape.
    private void setY(int index, int y) { coords[index][1] = y; }
    // Getter for the x coordinate of a point in the current shape.
    public int x(int index) { return coords[index][0]; }
    // Getter for the y coordinate of a point in the current shape.
    public int y(int index) { return coords[index][1]; }
    // Getter for the current shape type.
    public Tetrominoe getShape()  { return pieceShape; }

    // Sets the shape to a random Tetromino shape other than NoShape.
    public void setRandomShape() {
        var r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1; // Generates a random number between 1 and 7.
        Tetrominoe[] values = Tetrominoe.values();
        setShape(values[x]); // Sets the shape using the randomly generated index.
    }

    // Calculates the minimum x coordinate across all points of the current shape.
    public int minX() {
        int m = coords[0][0];
        for (int i=0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }

    // Calculates the minimum y coordinate across all points of the current shape.
    public int minY() {
        int m = coords[0][1];
        for (int i=0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    // Rotates the current shape to the left (counter-clockwise) unless it's a square shape.
    public Shape rotateLeft() {
        if (pieceShape == Tetrominoe.SquareShape) {
            return this; // Square shape does not need rotation.
        }
        var result = new Shape();
        result.pieceShape = pieceShape;
        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    // Rotates the current shape to the right (clockwise) unless it's a square shape.
    public Shape rotateRight() {
        if (pieceShape == Tetrominoe.SquareShape) {
            return this; // Square shape does not need rotation.
        }
        var result = new Shape();
        result.pieceShape = pieceShape;
        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}
