package maze.solving;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BreadthFirst extends MazeSolver
{
    char[][] maze;
    private boolean startFound = false;
    private boolean pathFound = false;

    Cell exit;
    private enum Direction {UP, DOWN, LEFT, RIGHT}
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    ArrayList<Object[]> queue = new ArrayList<>();

    public BreadthFirst(char[][] maze)
    {
        super(maze);
        this.maze = maze;
    }

    @Override
    public void update()
    {
        if (!startFound) {
            Cell start = new Cell(findStart(), null);
            queue.add(new Object[]{start, getValidDirections(start)});
            place(start, SUSPECT);
            startFound = true;

        } else if (!pathFound) {
            Object[] currentState = queue.get(0);
            queue.remove(0);
            Cell parent = (Cell) currentState[0];
            int y = parent.loc[0];
            int x = parent.loc[1];
            System.out.println("At y: " + y + ", x: " + x);

            if (foundExit(y, x)) {
                System.out.println("FoundExit");
                pathFound = true;
                exit = parent;
                updateHistory();
                return;
            }
            maze[y][x] = SUSPECT;


            ArrayList<Direction> directions =
                    (ArrayList<Direction>) currentState[1];

            for (Direction direction : directions) {
                Cell step;
                switch (direction) {
                    case UP:
                        step = new Cell(new int[]{y - 1, x}, parent);
                        Cell up = new Cell(new int[]{y - 2, x}, step);
                        queue.add(new Object[]{up, getValidDirections(up)});
                        break;
                    case DOWN:
                        step = new Cell(new int[]{y + 1, x}, parent);
                        Cell down = new Cell(new int[]{y + 2, x}, step);
                        queue.add(new Object[]{down, getValidDirections(down)});
                        break;
                    case LEFT:
                        step = new Cell(new int[]{y, x - 1}, parent);
                        Cell left = new Cell(new int[]{y, x - 2}, step);
                        queue.add(new Object[]{left, getValidDirections(left)});
                        break;
                    case RIGHT:
                        step = new Cell(new int[]{y, x + 1}, parent);
                        Cell right = new Cell(new int[]{y, x + 2}, step);
                        queue.add(new Object[]{right, getValidDirections(right)});
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + direction);
                }
                place(step, SUSPECT);
            }
        } else if (!isComplete()) {
            Cell iterator = exit;
            while (true) {
                place(iterator, SOLUTION);
                iterator = iterator.parent;
                if (iterator.parent == null) {
                    place(iterator, SOLUTION);
                    updateHistory();
                    break;
                }
                updateHistory();
            }

            for (int i = 0; i < getHeight(); i++) {
                for (int j = 0; j < getWidth(); j++) {
                    if (maze[i][j] == SUSPECT) {
                        maze[i][j] = PATH;
                    }
                }
                updateHistory();
            }
            setComplete(true);
            return;
        }
        updateHistory();
    }

    private void place(Cell cell, char toPlace)
    {
        maze[cell.loc[0]][cell.loc[1]] = toPlace;
    }

    private ArrayList<Direction> getValidDirections(Cell c)
    {
        ArrayList<Direction> toLoad = new ArrayList<>();
        int y = c.loc[0];
        int x = c.loc[1];

        if (inBounds(y + 1, x) && inBounds(y + 2, x)) {
            toLoad.add(Direction.DOWN);
        }
        if (inBounds(y - 1, x) && inBounds(y - 2, x)) {
            toLoad.add(Direction.UP);
        }
        if (inBounds(y, x - 1) && inBounds(y, x - 2)) {
            toLoad.add(Direction.LEFT);
        }
        if (inBounds(y, x + 1) && inBounds(y, x + 2)) {
            toLoad.add(Direction.RIGHT);
        }

        return toLoad;
    }

//    private boolean valid(int y1, int x1, int y2, int x2)
//    {
//        return inBounds(y1, x1) && inBounds(y2, x2) && maze[y1][x1] != SUSPECT && maze[y2][x2] != SUSPECT;
//    }

    class Cell {
        int[] loc;
        Cell parent;

        public Cell(int[] location, Cell parent) {
            loc = new int[]{location[0], location[1]};
            this.parent = parent;
        }

        public Cell getParent() {
            return parent;
        }
    }
}
