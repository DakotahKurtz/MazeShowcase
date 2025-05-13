package maze.generation;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class DepthFirstBacktracking extends MazeGenerator
{

    char[][] maze;
    ThreadLocalRandom rand;
    ArrayList<int[]> visited;
    int count = 0;
    Stack<Object[]> stack = new Stack<>();
    boolean init = false;
    boolean generated = false;
    int[] previous = new int[2];


    /**
     * Constructor to create initial grid of "cells" fully separated by walls
     *
     * @param height of maze to be generated
     * @param width  of maze to be generated
     */

    public DepthFirstBacktracking(int height, int width)
    {
        super(height, width);
        maze = getMaze();
        rand = ThreadLocalRandom.current();
        visited = new ArrayList<>();
        initializeEmptyMaze();
    }

    public void update()
    {
        if (isComplete())
        {
            return;
        }
        if (!init)
        {
            init = true;
            int[] start = new int[]{1, 1};
            addToStack(start);
            maze[1][1] = SUSPECT;
            previous = new int[]{1, 1};
        } else if (!generated)
        {
            if (stack.isEmpty())
            {
                generated = true;
            } else
            {
                nextIterative();
            }
        } else if (!isComplete())
        {
            maze[1][1] = START;
            maze[getHeight() - 2][getWidth() - 2] = EXIT;
            setComplete(true);
        }


        updateHistory();
    }




    private void addToStack(int[] next)
    {
        stack.push(new Object[]{next});
    }

    private ArrayList<DIRECTION> getValidDirections(int[] next)
    {
        ArrayList<DIRECTION> directions = new ArrayList<>();
        int y = next[0];
        int x = next[1];

        if (inBounds(y - 2, x) && !contains(new int[]{y - 2, x}))
        {
            directions.add(DIRECTION.UP);
        }
        if (inBounds(y + 2, x) && !contains(new int[]{y + 2, x}))
        {
            directions.add(DIRECTION.DOWN);
        }
        if (inBounds(y, x + 2) && !contains(new int[]{y, x + 2}))
        {
            directions.add(DIRECTION.RIGHT);
        }
        if (inBounds(y, x - 2) && !contains(new int[]{y, x - 2}))
        {
            directions.add(DIRECTION.LEFT);
        }

        return directions;
    }

    public void nextIterative()
    {

        Object[] objects = stack.peek();
        int[] now = (int[]) objects[0];
        int y = now[0];
        int x = now[1];
        ArrayList<DIRECTION> directions = getValidDirections(now);

        visited.add(now);

        if (directions.isEmpty())
        {
            maze[y][x] = PATH;
            stack.remove(objects);
            if (stack.size() > 1)
            {
                now = (int[]) stack.peek()[0];

                maze[now[0]][now[1]] = SUSPECT;
                previous = new int[]{now[0], now[1]};
            }
            return;
        }

        maze[previous[0]][previous[1]] = PATH;

        int r = rand.nextInt(0, directions.size());
        DIRECTION direction = directions.get(r);

        switch (direction)
        {
            case UP: // try to go up

                maze[--y][x] = PATH;
                y--;

                break;
            case DOWN: // try to go down

                maze[++y][x] = PATH;
                y++;

                break;
            case LEFT: // try to go left

                maze[y][--x] = PATH;
                x--;

                break;
            case RIGHT: // try to go right

                maze[y][++x] = PATH;
                x++;

                break;
        }


        addToStack(new int[]{y, x});

        maze[y][x] = SUSPECT;
        previous = new int[]{y, x};
    }


    private boolean contains(int[] arr)
    {
        for (int[] ints : visited)
        {
            if (ints[0] == arr[0] && ints[1] == arr[1])
            {
                return true;
            }
        }
        return false;
    }
}
