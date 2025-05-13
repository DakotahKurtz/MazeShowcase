package maze.generation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class KruskalsAlgorithm extends MazeGenerator
{
    /**
     * Constructor to initialize variables and create the perimeter wall around the
     * outside of the maze.
     *
     * @param height of maze to be generated
     * @param width  of maze to be generated
     */

    ArrayList<int[]> walls;
    DisjointSet disjointSet;
    boolean generated = false;

    char[][] maze;
    ThreadLocalRandom rand;
    int i = 0;

    public KruskalsAlgorithm(int height, int width)
    {
        super(height, width);
        walls = new ArrayList<>((height * width) / 3);
        disjointSet = new DisjointSet();
        maze = super.getMaze();
        rand = ThreadLocalRandom.current();
        initializeEmptyMaze();
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
                    disjointSet.addSubset(new int[]{i, j});
                    maze[i][j] = PATH;
                } else {
                    maze[i][j] = WALL;
                    if (i % 2 == 0 ^ j % 2 == 0)
                    {
                        walls.add(new int[]{i, j});
                    }
                }
            }
        }
        updateHistory();
        randomizeWalls();
    }

    public void update() {

        if (!generated)
        {
            boolean match = false;

            while (!match)
            {

                if (walls.isEmpty())
                {
                    generated = true;
                    return;
                }

                int[] wallLoc = walls.get(0);


                walls.remove(0);

                if (wallLoc[1] % 2 == 0)
                {
                    if (disjointSet.union(new int[]{wallLoc[0], wallLoc[1] - 1},
                            new int[]{wallLoc[0], wallLoc[1] + 1}))
                    {
                        match = true;
                    }
                } else
                {
                    if (disjointSet.union(new int[]{wallLoc[0] - 1, wallLoc[1]},
                            new int[]{wallLoc[0] + 1, wallLoc[1]}))
                    {
                        match = true;
                    }
                }

                if (match)
                {
                    maze[wallLoc[0]][wallLoc[1]] = PATH;
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

    private void randomizeWalls()
    {
        for (int i = 0; i < walls.size(); i++) {
            int rand1 = rand.nextInt(0, walls.size());
            int rand2 = rand.nextInt(0, walls.size());
            int[] temp = Arrays.copyOf(walls.get(rand1), 2);
            walls.get(rand1)[0] = walls.get(rand2)[0];
            walls.get(rand1)[1] = walls.get(rand2)[1];
            walls.get(rand2)[0] = temp[0];
            walls.get(rand2)[1] = temp[1];
        }
    }



    class DisjointSet {

        HashMap<Node, LinkedList<Node>> sets;

        public DisjointSet() {
            sets = new HashMap<>();

        }

        public boolean addSubset(int[] arr) {
            Node n = new Node(arr[0], arr[1]);
            if (sets.containsKey(n)) {
                return false;
            }
            LinkedList<Node> list = new LinkedList<>();
            list.add(n);
            sets.put(n, list);
            return true;
        }

        public boolean union(int[] arr1, int[] arr2) {
            Node n1 = sets.get(new Node(arr1[0], arr1[1])).get(0).getParent();
            Node n2 = sets.get(new Node(arr2[0], arr2[1])).get(0).getParent();


            if (!sets.containsKey(n1) || !sets.containsKey(n2)) {
                return false;
            }

            if (n1.getParent().equals(n2.getParent())) {
                return false;
            }

            LinkedList<Node> l1 = sets.get(n1.getParent());
            LinkedList<Node> l2 = sets.get(n2.getParent());

            if (l1.size() >= l2.size()) { // merge n2 into n1
                // get n2 parent, and it's linked list. set all nodes in list to share
                // parent with n1.parent. Then add n2.list into n1

                for (Node n : l2) {
                    n.setParent(n1.getParent());
                }
                l1.addAll(l2);
            } else {
                for (Node n : l1) {
                    n.setParent(n2.getParent());
                }
                l2.addAll(l1);
            }
            return true;
        }

        @Override
        public String toString()
        {
            StringBuilder out = new StringBuilder();

            for (Map.Entry<Node, LinkedList<Node>> entry : sets.entrySet())
            {
                out.append(entry.getKey()).append(" -> ");
                for (Node n : entry.getValue()) {
                    out.append(n).append("  |  ");
                }
                out.append("\n");
            }

            return out.toString();
        }

    }

    class Node {
        int[] data;
        Node parent;

        public Node(int y, int x) {
            this.data = new int[]{y, x};
            parent = this;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent)
        {
            this.parent = parent;
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(data);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Node)) {
                return false;
            }
            int[] o = ((Node) obj).data;
//            System.out.println("("+o[1]+", "+o[0]+") =?= ("+data[1]+", " + data[0] +").");
            return Arrays.equals(data, ((Node) obj).data);
        }

        @Override
        public String toString()
        {
            String out = "";
            out += "Data: (" + data[1] + ", " + data[0] + "). Parent at: ( " + parent.data[1] + ", " + parent.data[0] + ").";

            return out;
        }
    }

}
