package maze;

public interface Updatable
{

    public void update();
    boolean isComplete();
    void setComplete(boolean b);
    char[][] getMazeState(int iteration);
    char[][] getMaze();
    int getIterations();
    void updateHistory();
    int getWidth();
    int getHeight();

    public static void print() {
        System.out.println("test");
    }

}
