package maze;

import maze.generation.MazeGenerator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class DisplayPane extends Pane
{
    private final double width;
    private final double height;
    private ArrayList<Cell> rectangles;
    private char[][] maze;

    private int rows;
    private int cols;
    private Updatable algorithm;
    private int stateCount;
    private final static int MINIMUM_BORDER = 10;
    private boolean solveMode;
    private double cellBorder;
    private static final int STANDARD_STROKE_WIDTH = 6;
    private int strokeWidth;

    public DisplayPane(Updatable algorithm, double width, double height,
                       boolean solveMode)
    {
        this.algorithm = algorithm;
        this.width = width;
        this.height = height;

        this.rows = convertDimensionToCell(algorithm.getHeight());
        this.cols = convertDimensionToCell(algorithm.getWidth());
        this.solveMode = solveMode;
        strokeWidth = STANDARD_STROKE_WIDTH;
        cellBorder = strokeWidth * 1.5;

//        Rectangle borderRectangle
        maze = algorithm.getMaze();
        stateCount = 0;
        rectangles = new ArrayList<>(rows * cols);
        initializeRectangles();
    }

    private int convertDimensionToCell(int dimension)
    {
        return (dimension - 1) / 2;
    }

    public void setAlgorithm(Updatable algorithm)
    {
        this.algorithm = algorithm;
        this.cols = convertDimensionToCell(algorithm.getWidth());
        this.rows = convertDimensionToCell(algorithm.getHeight());


        rectangles = new ArrayList<>(rows * cols);
        initializeRectangles();
        stateCount = 0;
    }

    public void updateDisplay(char[][] maze) {

    }

    private void initializeRectangles()
    {
        getChildren().clear();
        maze = algorithm.getMaze();

        int dimension = (int) Math.min((width - MINIMUM_BORDER) / cols,
                (height - MINIMUM_BORDER) / rows);
        double spacingHorizontal = (width - (cols * dimension)) / 2;
        double spacingVertical = (height - (rows * dimension)) / 2;

        strokeWidth = (int) Math.min(STANDARD_STROKE_WIDTH, dimension * .15);
        cellBorder = strokeWidth * 1.5;

        Rectangle backgroundPath = new Rectangle(spacingHorizontal, spacingVertical,
                width - (spacingHorizontal * 2), height - (spacingVertical * 2));
        backgroundPath.setFill(MazeMain.PATH_COLOR);
        this.getChildren().add(backgroundPath);

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {

                Cell c = new Cell((j * dimension) + spacingHorizontal,
                        (i * dimension) + spacingVertical,
                        dimension, i, j);
//                rectangles[i][j].setFill(MazeMain.PATH_COLOR);
                rectangles.add(c);

                this.getChildren().add(c);
            }
        }

        for (Cell c : rectangles) {
            this.getChildren().addAll(c.getLines());
            if (solveMode) {
                this.getChildren().addAll(c.getSolveLines());
            }
        }

        // place border lines across top and left side
        Line top = new Line(spacingHorizontal, spacingVertical,
                width - spacingHorizontal, spacingVertical);
        Line left = new Line(spacingHorizontal, spacingVertical,
                spacingHorizontal,
                height - spacingVertical);
        top.setStrokeWidth(strokeWidth);
        left.setStrokeWidth(strokeWidth);
        this.getChildren().addAll(top, left);
    }

    public boolean updateAlgorithm()
    {
        if (!algorithm.isComplete())
        {
            algorithm.update();
            return true;
        }

        return false;
    }

    public boolean incrementStateCount()
    {
        if (stateCount + 1 < algorithm.getIterations())
        {
            stateCount++;
            return true;
        }
            return false;
    }

    public boolean decrementStateCount()
    {
        if (stateCount - 1 >= 0) {
            stateCount--;
            return true;
        }
        return false;

    }

    public void updateDisplay()
    {
        maze = algorithm.getMazeState(stateCount);
        for (Cell c : rectangles) {
            c.update();
        }
    }

    public char[][] getMaze()
    {
        return algorithm.getMaze();
    }

    public boolean algorithmComplete()
    {
        return algorithm.isComplete();
    }

    class Cell extends Rectangle {

        Line right;
        Line bottom;
        char type;
        private int xLoc, yLoc;
        Rectangle solveRight;
        Rectangle solveDown;

        public Cell(double x, double y, double dimension, int yLoc, int xLoc) {
            super(x + cellBorder, y + cellBorder, dimension - (cellBorder * 2),
                    dimension - (cellBorder * 2));

            right = new Line(x + dimension, y,
                    x + dimension,
                    y + dimension);
            bottom = new Line(x, y + dimension, x + dimension,
                    y + dimension);

            solveRight = new Rectangle(this.getX() + getWidth(), this.getY(),
                    cellBorder * 2, dimension - (cellBorder * 2));
            solveRight.setFill(Color.TRANSPARENT);
            solveDown = new Rectangle(this.getX(), this.getY() + this.getHeight(),
                    dimension - (cellBorder * 2), cellBorder * 2);
            solveDown.setFill(Color.BLACK);

            this.yLoc = convertDimensionToGrid(yLoc);
            this.xLoc = convertDimensionToGrid(xLoc);

            right.setStrokeWidth(strokeWidth);
            bottom.setStrokeWidth(strokeWidth);

            update();
        }

        public ArrayList<Line> getLines() {
            ArrayList<Line> lines = new ArrayList<>(2);
            lines.add(right);
            lines.add(bottom);
            return lines;
        }

        public void carveRight() {
            right.setStroke(Color.TRANSPARENT);
        }

        public void carveDown() {
            bottom.setStroke(Color.TRANSPARENT);
        }

        public void drawRight() {
            right.setStroke(Color.BLACK);
        }

        public void drawDown() {
            bottom.setStroke(Color.BLACK);
        }

        public void setType()
        {
            Color c;

            switch (maze[yLoc][xLoc])
            {
                case MazeGenerator.PATH:
                    c = MazeMain.PATH_COLOR;
                    break;

                case MazeGenerator.WALL:
                    c = MazeMain.WALL_COLOR;
                    break;

                case MazeGenerator.START:
                    c = MazeMain.START_COLOR;
                    break;

                case MazeGenerator.SUSPECT:
                    c = MazeMain.SUSPECT_COLOR;
                    break;

                case MazeGenerator.EXIT:
                    c = MazeMain.EXIT_COLOR;
                    break;

                case MazeGenerator.SOLUTION:
                    c = MazeMain.SOLUTION_COLOR;
                    break;

                default:
                    throw new IllegalStateException("Unexpected value " + type + " in " +
                            "Cell");
            }

            this.setFill(c);
        }

        public int convertDimensionToGrid(int loc) {
            return loc * 2 + 1;
        }

        public void update()
        {
            setType();
            if (maze[yLoc + 1][xLoc] == MazeGenerator.WALL) {
                drawDown();
            } else {
                carveDown();
            }
            if (maze[yLoc][xLoc + 1] == MazeGenerator.WALL) {
                drawRight();
            } else {
                carveRight();
            }

            if (solveMode)
            {
                if ((yLoc + 1 < algorithm.getHeight()) && maze[yLoc + 1][xLoc] == MazeGenerator.SOLUTION)
                {
                    solveDown.setFill(MazeMain.SOLUTION_COLOR);
                }
                else if ((yLoc + 1 < algorithm.getHeight()) && maze[yLoc + 1][xLoc] == MazeGenerator.SUSPECT)
                {
                    solveDown.setFill(MazeMain.SUSPECT_COLOR);
                }
                else {
                    solveDown.setFill(Color.TRANSPARENT);
                }
                if ((xLoc + 1 < algorithm.getWidth()) && maze[yLoc][xLoc + 1] == MazeGenerator.SOLUTION)
                {
                    solveRight.setFill(MazeMain.SOLUTION_COLOR);
                }
                else if ((xLoc + 1 < algorithm.getWidth()) && maze[yLoc][xLoc + 1] == MazeGenerator.SUSPECT)
                {
                    solveRight.setFill(MazeMain.SUSPECT_COLOR);
                }
                else {
                    solveRight.setFill(Color.TRANSPARENT);
                }
            }
        }

        public ArrayList<Rectangle> getSolveLines()
        {
            ArrayList<Rectangle> r = new ArrayList<>(2);
            r.add(solveDown);
            r.add(solveRight);
            return r;
        }
    }
}
