package maze.generation;


import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class to generate a character based maze using recursive division.
 *
 * @author Dakotah Kurtz
 */

public class RecursiveDivision extends MazeGenerator
{

    private enum ORIENTATION
    {HORIZONTAL, VERTICAL} // to determine direction of next wall

    private final ThreadLocalRandom rand;
    char[][] maze;
    private final Stack<int[]> stack;
    private boolean perimeter = false;
    private boolean generated = false;

    /**
     * Constructor to initialize variables and create the perimeter wall around the
     * outside of the maze.
     *
     * @param height of maze to be generated
     * @param width  of maze to be generated
     */

    public RecursiveDivision(int height, int width)
    {
        super(height, width);


        stack = new Stack<>();
        maze = getMaze();
        rand = ThreadLocalRandom.current();
        initializeEmptyMaze();
        updateHistory();
    }

    public void initializeEmptyMaze()
    {
        for (int i = 1; i < getHeight() - 1; i++)
        {
            for (int j = 1; j < getWidth() - 1; j++)
            {
                maze[i][j] = PATH;
            }
        }
        makePerimeter();
        stack.add(new int[]{1, getHeight() - 2, 1, getWidth() - 2});
    }


    public void update()
    {
        if (!generated)
        {
            if (!stack.isEmpty())
            {
                int[] subsection = stack.pop();
                make(subsection[0], subsection[1], subsection[2], subsection[3]);
            } else
            {
                generated = true;
            }
        } else if (!isComplete())
        {
            maze[1][1] = START;
            maze[getHeight() - 2][getWidth() - 2] = EXIT;

            updateHistory();
            setComplete(true);
        }
    }

    /**
     * Updates "orientation" of maze (the walls are always added across the narrowest
     * dimension).
     *
     * @param height of section being worked on.
     * @param width  of section being worked on.
     * @return orientation of next wall (VERTICAL or HORIZONTAL)
     */
    private ORIENTATION updateOrientation(int height, int width)
    {
        ORIENTATION orientation;
        if (width > height)
        {
            orientation = ORIENTATION.VERTICAL;
        } else if (width < height)
        {
            orientation = ORIENTATION.HORIZONTAL;
        } else
        { // if even dimensions, choose randomly
            orientation = rand.nextBoolean() ? ORIENTATION.HORIZONTAL :
                    ORIENTATION.VERTICAL;
        }
        return orientation;
    }


    /**
     * Recursive method that creates maze. Calculates new width and height, and sends
     * information to appropriate helper methods.
     *
     * @param minY - Of maze partition, row value closest to 0.
     * @param maxY - Of maze partition, row value closest to maze height
     * @param minX - Of maze partition, column value closest to 0.
     * @param maxX - Of maze partition, column value closest to width.
     */
    private void make(int minY, int maxY, int minX, int maxX)
    {
        printMaze();
        int width;
        int height;
        ORIENTATION orientation;


        width = maxX - minX;
        height = maxY - minY;

        System.out.println("Making section with y from " + minY + " to " + maxY + ", " +
                "and " +
                "x from " + minX + " to " + maxX);
        if ((width < 2 && height < 2) || height < 1 || width < 1)
        {
            return;
        }

        orientation = updateOrientation(height, width);

        if (orientation == ORIENTATION.HORIZONTAL)
        {
            // Maintains at least one segment of PATH between walls.
            int wallY = getValidWall(minY, maxY);

            // Add the wall to the maze.
            for (int i = minX; i <= maxX; i++)
            {
                maze[wallY][i] = WALL;
            }

            updateHistory();

            int doorX = getValidDoor(minX, maxX);
            maze[wallY][doorX] = PATH;
            updateHistory();

            // Recursively call for the

            //bottom partition
            stack.add(new int[]{minY, wallY - 1, minX, maxX});

            // top partition
            stack.add(new int[]{wallY + 1, maxY, minX, maxX});
        } else
        {

            // Maintains at least one segment of PATH between walls.
            int wallX = getValidWall(minX, maxX);

            for (int i = minY; i <= maxY; i++)
            {
                maze[i][wallX] = WALL;
            }

            updateHistory();

            int doorY = getValidDoor(minY, maxY);
            maze[doorY][wallX] = PATH;

            updateHistory();

            // left partition
            stack.add(new int[]{minY, maxY, minX, wallX - 1});

            // right partition
            stack.add(new int[]{minY, maxY, wallX + 1, maxX});
        }


    }

    private int getValidDoor(int min, int max)
    {
        System.out.println("Getting valid door from min: " + min + ", max: " + max);

        int r = min + (rand.nextInt(0, (max - min) / 2) * 2);
        System.out.println("Chose index " + r + " for door");

        return r;
    }

    private int getValidWall(int min, int max)
    {
        System.out.println("Getting valid wall from min: " + min + ", max: " + max);
        int upper = (max - min) / 2;
        int r = min + 1 + (rand.nextInt(0, upper) * 2);
        System.out.println("Chose index " + r + " for wall");
        return r;
    }


    /**
     * Creates a wall around the perimeter of the maze (along the 0 indices and
     * height/width indices).
     */
    private void makePerimeter()
    {
//        for (int i = 0; i < height - 1; i++) {
//
//            for (int j = 0; j < width - 1; j++) {
//                maze[i][j] = PATH;
//            }
//        }

        for (int i = 0; i < getWidth(); i++)
        {
            maze[0][i] = WALL;
            maze[getHeight() - 1][i] = WALL;
        }

        for (int i = 0; i < getHeight(); i++)
        {
            maze[i][0] = WALL;
            maze[i][getWidth() - 1] = WALL;
        }
    }


}
