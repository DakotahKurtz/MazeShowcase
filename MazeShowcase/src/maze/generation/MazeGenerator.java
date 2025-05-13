package maze.generation;

import maze.Updatable;

import java.util.ArrayList;

public abstract class MazeGenerator implements Updatable
{

    private int height;
    private int width;
    private final char[][] maze;
    private int iteration;

    private boolean isComplete;
    private ArrayList<char[][]> mazeIterations;
    public enum DIRECTION {LEFT, RIGHT, UP, DOWN}

    public final static char WALL = '1';
    public final static char PATH = '-';
    public final static char START = 'b';
    public final static char EXIT = 'e';
    public final static char SUSPECT = 'q';
    public final static char SOLUTION = 's';

    /**
     * Constructor to initialize variables and create the perimeter wall around the
     * outside of the maze.
     *
     * @param height of maze to be generated
     * @param width of maze to be generated
     */

    public MazeGenerator(int height, int width) {

        this.height = height + height + 1;
        this.width = width + width + 1;
        iteration = 0;

        isComplete = false;
        maze = new char[this.height][this.width];
        mazeIterations = new ArrayList<>();
    }

    @Override
    public int getIterations() {
        return iteration;
    }

    @Override
    public void setComplete(boolean b) {
        isComplete = b;
    }

    public void initializeEmptyMaze() {
        for (int i = 0; i < getHeight(); i++)
        {
            maze[i][0] = WALL;
            maze[i][getWidth() - 1] = WALL;
        }

        for (int i = 0; i < getWidth(); i++)
        {
            maze[0][i] = WALL;
            maze[getHeight() - 1][i] = WALL;
        }

        for (int i = 1; i < getHeight() - 1; i++)
        {
            for (int j = 1; j < getWidth() - 1; j++)
            {
                if (i % 2 == 1 && j % 2 == 1)
                {
                    maze[i][j] = PATH;
                } else
                {
                    maze[i][j] = WALL;
                }
            }
        }
        updateHistory();
    }

    public boolean inBounds(int y, int x) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }



    @Override
    public void updateHistory() {
        iteration++;
        char[][] current = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                current[i][j] = maze[i][j];
            }
        }
        mazeIterations.add(current);
    }

    @Override
    public char[][] getMazeState(int iteration) {
        return mazeIterations.get(iteration);
    }

    /**
     * Prints the maze to console.
     */
    public void printMaze() {
        System.out.println();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println();
    }

    @Override
    public char[][] getMaze() {
        return maze;
    }

    @Override
    public boolean isComplete()
    {
        return isComplete;
    }

    /**
     * Return the height of the generated maze.
     * @return height
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Return the width of the generated maze.
     * @return width
     */
    @Override
    public int getWidth() {
        return width;
    }

}
