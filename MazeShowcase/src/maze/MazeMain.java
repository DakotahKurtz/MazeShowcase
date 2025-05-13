package maze;

import maze.generation.*;
import maze.solving.BreadthFirst;
import maze.solving.DepthFirstSolver;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MazeMain extends Application
{
    public final static Color PATH_COLOR = Color.ANTIQUEWHITE;
    public final static Color WALL_COLOR = Color.RED;
    public final static Color START_COLOR = Color.GREEN;
    public final static Color EXIT_COLOR = Color.CORAL;
    public final static Color SUSPECT_COLOR = Color.BLUEVIOLET;
    public final static Color SOLUTION_COLOR = Color.GOLD;

    private int displayHeight;
    private int displayWidth;
    private int applicationHeight;
    private int applicationWidth;

    private DisplayPane generationPane;
    Scene solutionScene, generationScene;

    private final String defaultAlgorithmString = "Depth-First";

    @Override
    public void start(Stage stage)
    {

        displayHeight = 700;
        displayWidth = 1200;
        int generationMenuHeight = 75;
        applicationHeight = displayHeight + generationMenuHeight;
        applicationWidth = displayWidth;
        DisplayPane solveDisplay;

        // Start with GenerationMenu over empty maze grid (Scene1)

        // menu
        VBox menuVBox = new VBox();

        Pane menuBg = new Pane();
        double menuWidth = displayWidth * .8;
        double menuHeight = displayHeight * .8;

        // buttons to select generation algorithm
        HBox generationChoicesHBox = new HBox();
        ToggleGroup generationGroup = new ToggleGroup();

        String[] generationText = new String[]{"Depth-First", "Kruskal's", "Prim's",
                "Recursive Division", "Wilson's", "Aldous-Broder"};
        for (String s : generationText)
        {
            RadioButton b = new RadioButton(s);
            b.setToggleGroup(generationGroup);
            generationChoicesHBox.getChildren().add(b);
            if (s.equals(defaultAlgorithmString)) {
                b.setSelected(true);
            }
        }

        // text fields to select size
        HBox sizeSelection = new HBox();
        Text widthTextPrompt = new Text("Width");
        Text heightTextPrompt = new Text("Height");
        TextField widthField = new TextField("11");
        TextField heightField = new TextField("11");
        sizeSelection.getChildren().addAll(widthTextPrompt, widthField, heightTextPrompt,
                heightField);

        Button generateButton = new Button("Start Generating!");

        menuVBox.getChildren().addAll(generationChoicesHBox, sizeSelection,
                generateButton);
        menuVBox.setAlignment(Pos.CENTER);

        menuBg.getChildren().addAll(new Rectangle(menuWidth, menuHeight, Color.RED), menuVBox);
        BorderPane mainMenu = new BorderPane();
        BorderPane.setAlignment(menuBg, Pos.CENTER);
        mainMenu.setCenter(menuBg);
        mainMenu.setPadding(new Insets((applicationHeight - menuHeight) / 2,
                (applicationWidth - menuWidth) / 2,
                (applicationHeight - menuHeight) / 2, (applicationWidth - menuWidth) / 2));



        // have main menu appear overtop an empty maze display
        HBox controls = new HBox();
        controls.setPrefHeight(generationMenuHeight);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("-fx-background-color: #336699;");
        controls.setSpacing(10);

        Region spacingA = new Region();
        Button mainMenuButton = new Button("Menu");
        Region spacingB = new Region();
        Button solutionMenu = new Button("Solve");
        HBox.setHgrow(spacingA, Priority.ALWAYS);
        HBox.setHgrow(spacingB, Priority.ALWAYS);

        StackPane rootGeneration = new StackPane();
        rootGeneration.setAlignment(Pos.CENTER);

        generationPane =
                new DisplayPane(getGenerationAlgorithm(heightField.getText(),
                        widthField.getText(), defaultAlgorithmString),
                        displayWidth,
                        displayHeight, false);
        MazeAnimation generationTimer = new MazeAnimation(generationPane, 1);

        mainMenuButton.setOnMouseClicked(event -> {
            if (generationTimer.isRunning) {
                generationTimer.stop();
            }
            rootGeneration.getChildren().add(mainMenu);
        });

        StackPane.setAlignment(generationPane, Pos.TOP_RIGHT);
        StackPane.setAlignment(mainMenu, Pos.CENTER);





        BorderPane generationHolder = new BorderPane();

        HBox hBox = loadDisplayOptions(generationTimer, generationPane, generationMenuHeight);
        hBox.getChildren().addAll(spacingA, mainMenuButton, spacingB,
                solutionMenu);


        generationHolder.setCenter(generationPane);
        generationHolder.setBottom(hBox);



        rootGeneration.getChildren().addAll(generationHolder, mainMenu);
        generationScene = new Scene(rootGeneration, applicationWidth, applicationHeight);




        mainMenuButton.setOnMouseClicked(event -> {
            rootGeneration.getChildren().add(mainMenu);
        });


        generateButton.setOnMouseClicked(event -> {
            RadioButton b = (RadioButton) generationGroup.getSelectedToggle();

            generationPane.setAlgorithm(getGenerationAlgorithm(heightField.getText(),
                    widthField.getText(), b.getText()));
            generationPane.updateDisplay();
            rootGeneration.getChildren().remove(mainMenu);

        });

        /**
         * ***********************************************
         */

        HBox solverChoices = new HBox();
        String defaultSolvingAlgorithm = "Depth-First";
        String[] solvingAlgorithms = new String[]{"Depth-First", "Breadth-First"};

        ToggleGroup solvingGroup = new ToggleGroup();

        for (int i = 0; i < solvingAlgorithms.length; i++) {
            RadioButton b = new RadioButton(solvingAlgorithms[i]);
            if (solvingAlgorithms[i].equals(defaultSolvingAlgorithm)) {
                b.setSelected(true);
            }
            b.setToggleGroup(solvingGroup);
            solverChoices.getChildren().add(b);
        }

        Button startSolvingButton = new Button("Solve");
        VBox solvingVbox = new VBox();
        solvingVbox.getChildren().addAll(solverChoices, startSolvingButton);

        Pane solutionMenuHolder = new Pane();
        solutionMenuHolder.getChildren().addAll(new Rectangle(400, 400, Color.RED),
                solvingVbox);


        RadioButton temp = (RadioButton) solvingGroup.getSelectedToggle();
        solveDisplay = new DisplayPane(getSolutionAlgorithm(temp.getText(),
                generationPane.getMaze()), displayWidth, displayHeight, true);

        BorderPane solutionDisplayHolder = new BorderPane();
        solutionDisplayHolder.setCenter(solveDisplay);

        MazeAnimation solutionTimer = new MazeAnimation(solveDisplay, 1);

        HBox solutionOptionsHbox = loadDisplayOptions(solutionTimer, solveDisplay,
                generationMenuHeight);

        Button solutionMenuButton = new Button("Menu");
        Region spacingC = new Region();
        Button generationModeButton = new Button("Generate");
        Region spacingD = new Region();

        HBox.setHgrow(spacingC, Priority.ALWAYS);
        HBox.setHgrow(spacingD, Priority.ALWAYS);


        solutionOptionsHbox.getChildren().addAll(spacingC, solutionMenuButton, spacingD
                , generationModeButton);

        solutionDisplayHolder.setBottom(solutionOptionsHbox);

        StackPane solvingRoot = new StackPane();


        solvingRoot.getChildren().addAll(solutionDisplayHolder, solutionMenuHolder);

        startSolvingButton.setOnMouseClicked(event -> {
            solvingRoot.getChildren().remove(solutionMenuHolder);
            RadioButton b = (RadioButton) solvingGroup.getSelectedToggle();
            char[][] maze = copy2DArray(generationPane.getMaze());
            solveDisplay.setAlgorithm(getSolutionAlgorithm(b.getText(),
                    maze));
            solveDisplay.updateDisplay();
        });

        solutionMenuButton.setOnMouseClicked(event -> {
            if (solutionTimer.isRunning) {
                solutionTimer.stop();
            }
            solvingRoot.getChildren().add(solutionMenuHolder);
        });

        generationModeButton.setOnMouseClicked(event -> {
            if (solutionTimer.isRunning) {
                solutionTimer.stop();
            }

            if (!rootGeneration.getChildren().contains(mainMenu)) {
                rootGeneration.getChildren().add(mainMenu);
            }
            stage.setScene(generationScene);
        });



        solutionScene = new Scene(solvingRoot, applicationWidth, applicationHeight);


        solutionMenu.setOnMouseClicked(event -> {
            if (generationPane.algorithmComplete()) {
                if (generationTimer.isRunning) {
                    generationTimer.stop();
                }
                stage.setScene(solutionScene);
                if (!solvingRoot.getChildren().contains(solutionMenuHolder)) {
                    solvingRoot.getChildren().add(solutionMenuHolder);
                }
                char[][] maze = copy2DArray(generationPane.getMaze());
                solveDisplay.setAlgorithm(getSolutionAlgorithm(defaultSolvingAlgorithm,
                        maze));
                solveDisplay.updateDisplay();
            }
        });


        stage.setTitle("MAZES");
        stage.setScene(generationScene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    private char[][] copy2DArray(char[][] maze)
    {
        char[][] t = new char[maze.length][maze[0].length];

        for (int i = 0; i < maze.length; i++) {
            System.arraycopy(maze[i], 0, t[i], 0, maze[0].length);
        }
        return t;
    }

    private HBox loadDisplayOptions(MazeAnimation timer, DisplayPane pane, int height)
    {
        HBox controls = new HBox();
        controls.setPrefHeight(height);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("-fx-background-color: #336699;");
        controls.setSpacing(10);

        Button incrementState = new Button("Forward");
        Button decrementState = new Button("Back");
        Button playAnimation = new Button("Play");
        Button stopAnimation = new Button("Stop");


        controls.getChildren().addAll(incrementState, decrementState, playAnimation,
                stopAnimation);
        playAnimation.setOnMouseClicked(event -> {
            if (!timer.isRunning)
            {
                timer.start();
            }

        });

        stopAnimation.setOnMouseClicked(event -> {
            if (timer.isRunning) {
                timer.stop();
            }
        });

        incrementState.setOnMouseClicked(event -> {
            if (timer.isRunning) {
                return;
            }
            if (pane.updateAlgorithm() | pane.incrementStateCount()) {
                pane.updateDisplay();
            }
        });

        decrementState.setOnMouseClicked(event -> {
            if (timer.isRunning) {

                return;
            }
            if (pane.decrementStateCount()) {
                pane.updateDisplay();
            }
        });

        return controls;
    }

    private Updatable getSolutionAlgorithm(String text, char[][] maze)
    {
        switch (text) {
            case "Depth-First":
                return new DepthFirstSolver(maze);
            case "Breadth-First":
                return new BreadthFirst(maze);
            default:
                throw new IllegalStateException("Unexpected value: " + text);

        }
    }

    private Updatable getGenerationAlgorithm(String heightText, String widthText, String type)
    {
        int h = Integer.parseInt(heightText);
        int w = Integer.parseInt(widthText);

        switch (type) {
            case "Depth-First":
                return new DepthFirstBacktracking(h, w);
            case "Kruskal's":
                return new KruskalsAlgorithm(h, w);
            case "Prim's":
                return new PrimsAlgorithm(h, w);
            case "Recursive Division":
                return new RecursiveDivision(h, w);
            case "Wilson's":
                return new WilsonAlg(h, w);
            case "Aldous-Broder":
                return new AldousBroder(h, w);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    class MazeAnimation extends AnimationTimer
    {
        private long lastUpdate = 0;
        private long updateSpeed;
        private boolean isRunning;
        private DisplayPane display;

        public MazeAnimation(DisplayPane display, long updateSpeed_inMilliSeconds) {
            this.display = display;
            setUpdateSpeed(updateSpeed_inMilliSeconds);
        }

        @Override
        public void start()
        {
            super.start();
            isRunning = true;
        }

        @Override
        public void stop()
        {
            super.stop();
            isRunning = false;
        }

        @Override
        public void handle(long now)
        {

            if (now - lastUpdate >= updateSpeed && isRunning)
            {

                lastUpdate = now;

                if (display.updateAlgorithm() | display.incrementStateCount()) {
                    display.updateDisplay();
                } else {
                    isRunning = false;
                    System.out.println("Should be shutting off");
                    this.stop();
                }
            }
        }

        public void setUpdateSpeed(long time_in_ms) {
            this.updateSpeed = time_in_ms * 1_000_000;
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
