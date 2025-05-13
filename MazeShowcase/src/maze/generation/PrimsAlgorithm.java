package maze.generation;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class PrimsAlgorithm extends MazeGenerator
{
    char[][] maze;
    boolean generated = false;
    boolean begun = false;
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    ArrayList<int[]> choices = new ArrayList<>();
    ArrayList<int[]> visited = new ArrayList<>();


    /**
     * Constructor to initialize variables and create the perimeter wall around the
     * outside of the maze.
     *
     * @param height of maze to be generated
     * @param width  of maze to be generated
     */
    public PrimsAlgorithm(int height, int width)
    {
        super(height, width);
        maze = super.getMaze();
        initializeEmptyMaze();
    }

    public void update() {
        int[] current;

        if (!begun)
        {
            current = new int[]{
                    rand.nextInt(1, (getHeight() - 1) / 2) * 2 + 1,
                    rand.nextInt(1, (getWidth() - 1) / 2) * 2 + 1
            };
            maze[current[0]][current[1]] = PATH;
            visited.add(new int[]{current[0], current[1]});

            updateChoices(choices, current[0], current[1]);

            begun = true;
        } else if (!generated)
        {

            ArrayList<Integer> availableDirections = new ArrayList<>();
            int direction;
            boolean found = false;

            while (!found)
            {

                current = choices.get(rand.nextInt(0, choices.size()));


                choices.remove(current);


                maze[current[0]][current[1]] = PATH;
                visited.add(new int[]{current[0], current[1]});

                if (visited(current[0] + 2, current[1]))
                { // 0
                    availableDirections.add(0);
                    found = true;
                }
                if (visited(current[0] - 2, current[1]))
                { // 1

                    availableDirections.add(1);
                    found = true;
                }
                if (visited(current[0], current[1] + 2))
                { // 2
                    availableDirections.add(2);

                    found = true;
                }
                if (visited(current[0], current[1] - 2))
                { // 3
                    availableDirections.add(3);

                    found = true;
                }

                if (found)
                {
                    direction = availableDirections.get(rand.nextInt(0,
                            availableDirections.size()));

                    switch (direction)
                    {
                        case 0:
                            maze[current[0] + 1][current[1]] = PATH;
                            break;
                        case 1:
                            maze[current[0] - 1][current[1]] = PATH;
                            break;
                        case 2:
                            maze[current[0]][current[1] + 1] = PATH;
                            break;
                        case 3:
                            maze[current[0]][current[1] - 1] = PATH;
                            break;
                    }
                }


                availableDirections.clear();


                updateChoices(choices, current[0], current[1]);

                if (choices.isEmpty()) {
                    generated = true;
                    return;
                }
            }
        } else if (!isComplete())
        {

            maze[1][1] = START;
            maze[getHeight() - 2][getWidth() - 2] = EXIT;
            setComplete(true);

        }

        updateHistory();
    }



    private boolean validChoice(int y, int x)
    {
        return inBounds(y, x) && maze[y][x] != SUSPECT && !visited(y, x);
    }

    private void updateChoices(ArrayList<int[]> choices, int currY, int currX)
    {
        if (validChoice(currY, currX - 2)) {
            choices.add(new int[]{currY, currX - 2});
            maze[currY][currX - 2] = SUSPECT;
        }
        if (validChoice(currY, currX + 2)) {
            choices.add(new int[]{currY, currX + 2});
            maze[currY][currX + 2] = SUSPECT;

        }
        if (validChoice(currY - 2, currX)) {
            choices.add(new int[]{currY - 2, currX});
            maze[currY - 2][currX] = SUSPECT;

        }
        if (validChoice(currY + 2, currX)) {
            choices.add(new int[]{currY + 2, currX});
            maze[currY + 2][currX] = SUSPECT;

        }
    }

    private boolean visited(int y, int x)
    {
        for (int[] arr : visited) {
            if (arr[0] == y && arr[1] == x) {
                return true;
            }
        }
        return false;
    }


}
