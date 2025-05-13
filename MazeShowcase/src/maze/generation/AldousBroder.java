package maze.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AldousBroder extends MazeGenerator
{
    char[][] maze;
    ThreadLocalRandom rand;
    HashMap<Cell, Boolean> visited;
    int[] current;
    enum Direction {UP, DOWN, LEFT, RIGHT}
    boolean isBuilt = false;

    public AldousBroder(int height, int width)
    {
        super(height, width);
        maze = super.getMaze();
        rand = ThreadLocalRandom.current();
        visited = new HashMap<>();

        current = new int[]{rand.nextInt(0, getHeight() / 2) * 2 + 1, rand.nextInt(0,
                getWidth() / 2) * 2 + 1};

        initializeEmptyMaze();
    }

    @Override
    public void update()
    {
        if (!isBuilt)
        {
            maze[current[0]][current[1]] = PATH;
            ArrayList<Direction> directions = getDirections(current[0], current[1]);
            Direction direction = directions.get(rand.nextInt(0, directions.size()));
            int x, y;
            Cell c;
            switch (direction)
            {
                case LEFT:
                    y = current[0];
                    x = current[1] - 2;
                    current = new int[]{y, x};
                    maze[y][x] = SUSPECT;
                    updateHistory();
                    c = new Cell(y, x);
                    if (!visited.get(c))
                    {
                        maze[y][x] = PATH;
                        maze[y][x + 1] = PATH;
                        visited.replace(c, true);
                        updateHistory();
                    }
                    break;
                case RIGHT:
                    y = current[0];
                    x = current[1] + 2;
                    current = new int[]{y, x};
                    maze[y][x] = SUSPECT;
                    updateHistory();
                    c = new Cell(y, x);
                    if (!visited.get(c))
                    {
                        maze[y][x] = PATH;
                        maze[y][x - 1] = PATH;
                        visited.replace(c, true);
                        updateHistory();
                    }
                    break;
                case DOWN:
                    y = current[0] + 2;
                    x = current[1];
                    current = new int[]{y, x};
                    maze[y][x] = SUSPECT;
                    updateHistory();
                    c = new Cell(y, x);
                    if (!visited.get(c))
                    {
                        maze[y][x] = PATH;
                        maze[y - 1][x] = PATH;
                        visited.replace(c, true);
                        updateHistory();
                    }
                    break;
                case UP:
                    y = current[0] - 2;
                    x = current[1];
                    current = new int[]{y, x};
                    maze[y][x] = SUSPECT;
                    updateHistory();
                    c = new Cell(y, x);
                    if (!visited.get(c))
                    {
                        maze[y][x] = PATH;
                        maze[y + 1][x] = PATH;
                        visited.replace(c, true);
                        updateHistory();
                    }
                    break;
            }

            if (!visited.containsValue(false)) {
                isBuilt = true;
            }

        } else if (!isComplete()) {
            maze[1][1] = START;
            maze[getHeight() - 2][getWidth() - 2] = EXIT;

            setComplete(true);
            updateHistory();
        }

    }

    private ArrayList<Direction> getDirections(int y, int x)
    {
        ArrayList<Direction> d = new ArrayList<>();

        if (x - 2 >= 0) {
            d.add(Direction.LEFT);
        }
        if (x + 2 < getWidth()) {
            d.add(Direction.RIGHT);
        }
        if (y - 2 >= 0) {
            d.add(Direction.UP);
        }
        if (y + 2 < getHeight()) {
            d.add(Direction.DOWN);
        }

        return d;
    }

    @Override
    public void initializeEmptyMaze()
    {
        for (int i = 0; i < getHeight(); i++) {
            maze[i][0] = WALL;
            maze[i][getWidth() - 1] = WALL;
        }

        for (int i = 0; i < getWidth(); i++) {
            maze[0][i] = WALL;
            maze[getHeight() - 1][i] = WALL;
        }

        for (int i = 1; i < getHeight() - 1; i++) {
            for (int j = 1; j < getWidth() - 1; j++) {
                if (i % 2 == 1 && j % 2 == 1) {
                    maze[i][j] = PATH;
                    visited.put(new Cell(i, j), false);
                } else {
                    maze[i][j] = WALL;
                }
            }
        }
        updateHistory();
    }

    private class Cell {
        int[] location;

        public Cell(int y, int x) {
            this.location = new int[]{y, x};
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(location);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Cell)) {
                return false;
            }
            int[] o = ((Cell) obj).location;
//            System.out.println("("+o[1]+", "+o[0]+") =?= ("+data[1]+", " + data[0] +").");
            return Arrays.equals(location, ((Cell) obj).location);
        }
    }
}
