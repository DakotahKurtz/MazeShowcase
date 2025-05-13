package maze.solving;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class DepthFirstSolver extends MazeSolver
{

    private boolean startFound = false;
    private enum Direction {UP, DOWN, LEFT, RIGHT}
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    private final char[][] maze;

    private Stack<Object[]> stack;

    public DepthFirstSolver(char[][] maze) {
        super(maze);
        this.maze = getMaze();

        stack = new Stack<>();
    }

    @Override
    public void update()
    {
        if (!startFound) {
            int[] start = findStart();

            stack.add(new Object[]{start[0], start[1], getValidDirections(start[0],
                    start[1])});
            startFound = true;
            maze[start[0]][start[1]] = SOLUTION;
        } else if (!isComplete()) {
            Object[] arr = stack.peek();
            int y = (int) arr[0];
            int x = (int) arr[1];

            if (foundExit(y, x)) {
                setComplete(true);
                return;
            }

            ArrayList<Direction> directions = (ArrayList<Direction>) arr[2];
            if (directions.isEmpty()) {
                maze[y][x] = PATH;
                stack.pop();
                return;
            }

            int r = rand.nextInt(0, directions.size());
            Direction direction = directions.get(r);
            directions.remove(r);
            stack.pop();
            stack.add(new Object[]{y, x, directions});

            switch (direction) {
                case UP:
                    y -= 1;
                    break;
                case DOWN:
                    y += 1;
                    break;
                case LEFT:
                    x -= 1;
                    break;
                case RIGHT:
                    x += 1;
                    break;
            }
            stack.add(new Object[]{y, x, getValidDirections(y, x)});
            maze[y][x] = SOLUTION;
        }
        updateHistory();
    }



    private ArrayList<Direction> getValidDirections(int y, int x)
    {
        ArrayList<Direction> toLoad = new ArrayList<>();

        if (inBounds(y + 1, x)) {
            toLoad.add(Direction.DOWN);
        }
        if (inBounds(y - 1, x)) {
            toLoad.add(Direction.UP);
        }
        if (inBounds(y, x - 1)) {
            toLoad.add(Direction.LEFT);
        }
        if (inBounds(y, x + 1)) {
            toLoad.add(Direction.RIGHT);
        }

        return toLoad;
    }








}
