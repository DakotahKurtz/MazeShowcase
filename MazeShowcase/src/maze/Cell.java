package maze;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cell extends Rectangle
{
    Line topLine;
    Line bottomLine;
    Line leftLine;
    Line rightLine;
    private final static int BORDER_WIDTH = 2;
    private ArrayList<Line> border;


    public Cell(double width, double height) {
        super(width, height);
        topLine = new Line(BORDER_WIDTH, BORDER_WIDTH, width - BORDER_WIDTH, BORDER_WIDTH);
        bottomLine = new Line(BORDER_WIDTH, height - BORDER_WIDTH, width - BORDER_WIDTH
                , height - BORDER_WIDTH);
        leftLine = new Line(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, height - BORDER_WIDTH);
        rightLine = new Line(width - BORDER_WIDTH, BORDER_WIDTH, width - BORDER_WIDTH,
                height - BORDER_WIDTH);

        border = new ArrayList<>(4);
        border.add(topLine);
        border.add(bottomLine);
        border.add(leftLine);
        border.add(rightLine);

        for (Line line : border) {
            line.setStrokeWidth(BORDER_WIDTH);
        }
    }

    public static int getDimension(double width, double height, int rows, int cols)
    {
       return  (int) Math.min((width  - 2 * cols * BORDER_WIDTH) / cols,
               (height - 2 * rows * BORDER_WIDTH) / rows);
    }

    public ArrayList<Line> getBorder() {
        return border;
    }

    public void setConstraints(int i, int j)
    {
        GridPane.setConstraints(this, i, j);
        for (Line line : border) {
            GridPane.setConstraints(line, i, j);
        }
    }
}
