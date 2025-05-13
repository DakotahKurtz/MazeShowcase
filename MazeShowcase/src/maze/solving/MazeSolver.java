package maze.solving;

import maze.Updatable;

import java.util.ArrayList;

public abstract class MazeSolver implements Updatable
{
    private int height;
    private int width;
    private final char[][] maze;
    private int iteration;

    private boolean isComplete;
    private ArrayList<char[][]> mazeIterations;

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

     */

    public MazeSolver(char[][] maze) {

        this.maze = maze;
        this.height = maze.length;
        this.width = maze[0].length;
        iteration = 0;

        isComplete = false;
        mazeIterations = new ArrayList<>();
        updateHistory();

    }

    @Override
    public int getIterations() {
        return iteration;
    }

    @Override
    public void setComplete(boolean b) {
        isComplete = b;
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

    protected boolean inBounds(int y, int x)
    {
        return y >= 0 && y < getHeight() - 1 && x >= 0 && x < getWidth() - 1 && maze[y][x] == MazeSolver.PATH;
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

    public int[] findStart()
    {
        int[] fake = new int[]{-1, -1};
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                if (maze[i][j] == MazeSolver.START) {
                    return new int[]{i, j};
                }
            }
        }
        return fake;
    }

    public boolean foundExit(int y, int x)
    {
        if (y - 1 >= 0 && maze[y - 1][x] == MazeSolver.EXIT) {
            return true;
        }
        if (y + 1 < getHeight() && maze[y + 1][x] == MazeSolver.EXIT) {
            return true;
        }
        if (x - 1 >= 0 && maze[y][x - 1] == MazeSolver.EXIT) {
            return true;
        }
        return x + 1 < getWidth() && maze[y][x + 1] == MazeSolver.EXIT;
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
