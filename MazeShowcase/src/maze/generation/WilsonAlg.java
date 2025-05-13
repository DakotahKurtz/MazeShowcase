package maze.generation;


import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class WilsonAlg extends MazeGenerator
{
    char[][] maze;
    ThreadLocalRandom rand;
    ArrayList<int[]> cells;

    private enum DIRECTION
    {UP, DOWN, LEFT, RIGHT}

    int[] end;
    int[] deleteTo;
    int iteration = 0;
    boolean foundit;
    ArrayList<int[]> toDraw;
    boolean started = false;
    boolean firstPath = false;
    boolean drawFirstPath = false;
    boolean generated = false;
    boolean looking = true;

    boolean deleting = false;
    Stack<Object[]> stack = new Stack<>();
    ArrayList<Cell> queue = new ArrayList<>();


    /**
     * Constructor to initialize variables and create the perimeter wall around the
     * outside of the maze.
     *
     * @param height of maze to be generated
     * @param width  of maze to be generated
     */
    public WilsonAlg(int height, int width)
    {
        super(height, width);
        maze = super.getMaze();

        rand = ThreadLocalRandom.current();

        deleteTo = new int[2];
        cells = new ArrayList<>();
        foundit = false;
        toDraw = new ArrayList<>();

        initializeEmptyMaze();
    }

    @Override
    public void initializeEmptyMaze()
    {
        for (int i = 0; i < getHeight(); i++)
        {
            for (int j = 0; j < getWidth(); j++)
            {
                maze[i][j] = WALL;
                if (i % 2 == 1 && j % 2 == 1)
                {
                    cells.add(new int[]{i, j});

                }
            }
        }
        updateHistory();
    }

    public void update()
    {
        if (!started)
        {
            DIRECTION[] directions = new DIRECTION[]{
                    DIRECTION.UP, DIRECTION.DOWN,
                    DIRECTION.LEFT, DIRECTION.RIGHT
            };
            DIRECTION startingDirection = directions[rand.nextInt(0, 4)];

//            int t = rand.nextInt(0, cells.size());
            int t = rand.nextInt(0, cells.size() - 1);
            int[] begin = cells.get(t);
            maze[begin[0]][begin[1]] = PATH;
            cells.remove(t);

//            t = rand.nextInt(0, cells.size());
            t = rand.nextInt(0, cells.size() - 1);
            end = cells.get(t);
            maze[end[0]][end[1]] = PATH;
            cells.remove(t);
            updateHistory();
            stack.add(load(begin, startingDirection));
            queue.add(new Cell(begin[0], begin[1]));
            started = true;

        } else if (deleting)
        {
            stepBack();

        } else if (drawFirstPath)
        {
            if (queue.size() == 1)
            {
                drawFirstPath = false;
                firstPath = true;
            }
            int[] change = queue.get(0).loc;
            queue.remove(0);
            maze[change[0]][change[1]] = PATH;
            updateHistory();
        } else if (!firstPath)
        {
            int[] next = stepForward();
            int y = next[0];
            int x = next[1];

            if (end[0] == y && end[1] == x)
            {
                drawFirstPath = true;
                queue.add(new Cell(y, x));

                stack.clear();
                return;
            }

            if (maze[y][x] != WALL)
            {
                deleting = true;
                deleteTo = new int[]{y, x};

//                    stack.pop();
                queue.remove(queue.size() - 1);

                updateHistory();
                return;
            }


            maze[y][x] = SUSPECT;


            updateHistory();


        } else if (!generated)
        {
            if (looking)
            {
                stack.clear();
                queue.clear();
                if (!foundEmptyCell()) {
                    return;
                }
                updateHistory();
            }

            int[] next = stepForward();

            if (maze[next[0]][next[1]] == SUSPECT)
            {

                deleteTo[0] = next[0];
                deleteTo[1] = next[1];
                deleting = true;
                queue.remove(queue.size() - 1);
                updateHistory();
                return;
            }

            if (maze[next[0]][next[1]] == PATH)
            {
                looking = true;
                for (Cell cell : queue)
                {
                    maze[cell.loc[0]][cell.loc[1]] = PATH;
                    updateHistory();
                }
                return;
            }
            maze[next[0]][next[1]] = SUSPECT;
            updateHistory();


        } else if (!isComplete())
        {
            maze[1][1] = START;
            maze[getHeight() - 2][getWidth() - 2] = EXIT;
            updateHistory();
            setComplete(true);
        }
    }

    private boolean foundEmptyCell()
    {
        int[] c;
        do
        {
            int t = rand.nextInt(0, cells.size());
            c = cells.get(t);
            cells.remove(t);


            if (cells.isEmpty())
            {
                generated = true;
                if (maze[c[0]][c[1]] != WALL) {
                    return false;
                }
            }

        } while (maze[c[0]][c[1]] != WALL);


        looking = false;
        DIRECTION[] directions = new DIRECTION[]{
                DIRECTION.UP, DIRECTION.DOWN,
                DIRECTION.LEFT, DIRECTION.RIGHT
        };

        DIRECTION startingDirection = directions[rand.nextInt(0, 4)];
        stack.add(load(c, startingDirection));
        queue.add(new Cell(c[0], c[1]));

        maze[c[0]][c[1]] = SUSPECT;
        return true;
    }

    private int[] stepForward()
    {
        Object[] arr = stack.peek();
        int y = (int) arr[0];
        int x = (int) arr[1];


        DIRECTION d = (DIRECTION) arr[2];

        switch (d)
        {
            case LEFT:
                queue.add(new Cell(y, x - 1));
                maze[y][x - 1] = SUSPECT;
                x -= 2;
                break;
            case RIGHT:
                queue.add(new Cell(y, x + 1));
                maze[y][x + 1] = SUSPECT;
                x += 2;
                break;
            case UP:
                queue.add(new Cell(y - 1, x));
                maze[y - 1][x] = SUSPECT;
                y -= 2;
                break;
            case DOWN:
                queue.add(new Cell(y + 1, x));
                maze[y + 1][x] = SUSPECT;
                y += 2;
                break;
        }

        stack.add(load(new int[]{y, x}, d));
        queue.add(new Cell(y, x));

        return new int[]{y, x};
    }

    private void stepBack()
    {

        int[] current = queue.get(queue.size() - 1).loc;
        if (current[0] == deleteTo[0] && current[1] == deleteTo[1])
        {
            deleting = false;
            return;
        }
        queue.remove(queue.size() - 1);

        maze[current[0]][current[1]] = WALL;
        updateHistory();

        if ((queue.size() % 2) == 1)
        {
            Object[] arr = stack.pop();

        }

    }

    private Object[] load(int[] begin, DIRECTION direction)
    {
        return new Object[]{
                begin[0], begin[1], getRandomDirection(begin[0], begin[1],
                direction)
        };
    }

    private DIRECTION getRandomDirection(int y, int x, DIRECTION direction)
    {
        ArrayList<DIRECTION> directions = new ArrayList<>();

        if (y - 2 >= 0 && direction != DIRECTION.DOWN)
        {
            directions.add(DIRECTION.UP);
        }
        if (y + 2 < getHeight() && direction != DIRECTION.UP)
        {
            directions.add(DIRECTION.DOWN);
        }
        if (x - 2 >= 0 && direction != DIRECTION.RIGHT)
        {
            directions.add(DIRECTION.LEFT);
        }
        if (x + 2 < getWidth() && direction != DIRECTION.LEFT)
        {
            directions.add(DIRECTION.RIGHT);
        }


        DIRECTION d = directions.get(rand.nextInt(0, directions.size()));
        return d;
    }

    class Cell
    {

        int[] loc;

        public Cell(int y, int x)
        {
            loc = new int[]{y, x};
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (!(obj instanceof Cell))
            {
                return false;
            }
            int[] arr = ((Cell) obj).loc;
            return arr[0] == loc[0] && arr[1] == loc[1];
        }
    }

}

