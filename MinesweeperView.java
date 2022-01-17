import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
/**
 * MinesweeperView class that creates the UI of the MinesweeperGame using JavaFX.
 * @author McKenzie Cameron
 * @version 1.0
 */
public class MinesweeperView extends Application {
    private Scene startScreen;
    private Scene sweeperField;
    private Scene endScreen;

    /**
     * start() method that starts the JavaFX UI.
     * @param primaryStage The Stage that displays the UI of the MinesweeperGame.
     */
    public void start(Stage primaryStage) {
        startScreen = null;
        sweeperField = null;
        endScreen = null;
        primaryStage.setTitle("Minesweeper");
        createMainMenu(primaryStage);

        primaryStage.setScene(startScreen);
        primaryStage.show();
    }
    /**
     * createMainMenu() method that creates the main menu UI of the MinesweeperGame.
     * @param primaryStage The stage used to display the JavaFX Scene.
     */
    public void createMainMenu(Stage primaryStage) {
        if (startScreen == null) {
            // Create a VBox pane
            VBox box = new VBox(600);
            box.setSpacing(75);
            box.setPadding(new Insets(15, 15, 15, 15));
            box.setAlignment(Pos.BASELINE_CENTER);
            // Welcome Message
            Text welcomeMessage = new Text("Minesweeper time! :D");
            welcomeMessage.setFont(Font.font(20));
            box.getChildren().add(welcomeMessage);
            // ComboBox for selecting the difficulty
            ComboBox<Difficulty> difficultySelector = new ComboBox<>();
            difficultySelector.setPromptText("Select difficulty...");
            //difficultySelector.getItems().addAll("Easy", "Medium", "Hard");
            difficultySelector.getItems().addAll(
                    Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD);
            box.getChildren().add(difficultySelector);
            // TextField for allowing user to enter their name
            TextField nameInputBox = new TextField();
            nameInputBox.setMaxWidth(250);
            nameInputBox.setPromptText("Enter name...");
            box.getChildren().add(nameInputBox);
            // Button for starting the game
            Button startButton = new Button("Start");
            startButton.setStyle("-fx-font-size:20");
            box.getChildren().add(startButton);
            // The startButton is clicked
            startButton.setOnMouseClicked(e -> {
                if (difficultySelector.getValue() == null || nameInputBox.getText().isEmpty()) {
                    // An error Alert if there is no name input or no difficulty is chosen
                    Alert blankError = new Alert(Alert.AlertType.WARNING,
                            "Invalid inputs. Please enter a valid name and choose a difficulty.");
                    blankError.showAndWait();
                } else {
                    playMinesweeper(primaryStage, difficultySelector.getValue(), nameInputBox.getText());
                }
            });
            primaryStage.setScene(startScreen);
            startScreen = new Scene(box, 600, 600);
        }
    }
    /**
     * playMinesweeper() method creates the MinesweeperGame UI using JavaFX.
     * @param primaryStage The stage used to display the Scene.
     * @param gameDifficulty The Difficulty of the game chosen by the player.
     * @param name The name of the player.
     */
    public void playMinesweeper(Stage primaryStage, Difficulty gameDifficulty, String name) {
        if (sweeperField == null) {
            // Call the MinesweeperGame constructor and pass in the selected difficulty
            MinesweeperGame game = new MinesweeperGame(gameDifficulty);
            GridPane sweeperPane = new GridPane();
            Button[][] tileButtons = new Button[15][15];
            // Iterate over the tileButtons
            for (int i = 0; i < tileButtons.length; i++) {
                for (int j = 0; j < tileButtons[i].length; j++) {
                    // Creating buttons with red X's
                    tileButtons[i][j] = new Button("X");
                    tileButtons[i][j].setStyle("-fx-text-fill: red");
                    tileButtons[i][j].setPrefWidth(30);
                    sweeperPane.add(tileButtons[i][j], i, j);
                    final Button currentButton = tileButtons[i][j];
                    final int y = j;
                    final int x = i;
                    currentButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            boolean[][] tileRevealed = game.getIsVisible();
                            // If the clicked Tile has yet to be revealed...
                            if (!tileRevealed[y][x]) {
                                // The check() method is called to get the revealed Tiles
                                Tile[] move = game.check(y, x);
                                //System.out.println(move.length);
                                // Iterate over all Tile elements in the move Tile array
                                for (Tile tile : move) {
                                    // Getting the number that goes on the Tile
                                    String tileScore = String.valueOf(tile.getBorderingMines());
                                    // Add the number of adjacent mine Tiles to the current Tile button
                                    tileButtons[tile.getX()][tile.getY()].setText(tileScore);
                                    // Changed the revealed tileButton's color to blue
                                    tileButtons[tile.getX()][tile.getY()].setStyle("-fx-text-fill: blue");
                                }
                            }
                            // Call the named private inner class Result's constructor to get end game state
                            new Result(primaryStage, game, name);
                        }
                    });
                }
            }
            sweeperField = new Scene(sweeperPane, 450, 380);
            primaryStage.setScene(sweeperField);
        }
    }
    /**
     * main() method to launch the JavaFX program when an IDE is being used.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Private inner class that checks if the user won or lost the game and
     * restarts the canvas scene.
     * @author McKenzie Cameron
     * @version 1.0
     */
    private class Result {
        /**
         * Constructor that takes in the MineSweeperGame object and name of player.
         * @param primaryStage Stage where the scene takes place.
         * @param game The MinesweeperGame object.
         * @param name The name of the player.
         */
        Result(Stage primaryStage, MinesweeperGame game, String name) {
            Pane endPane = new Pane();
            Button newGameButton = new Button("New Game");
            newGameButton.setFont(Font.font(20));
            newGameButton.setLayoutY(50);
            endPane.getChildren().add(newGameButton);
            // if-else if to determine if the game was won or lost
            if (game.isWon()) {
                Label winLabel = new Label("Congratulations, " + name);
                winLabel.setFont(Font.font(15));
                endPane.getChildren().add(winLabel);
                endScreen = new Scene(endPane, 300, 100);
                primaryStage.setScene(endScreen);
            } else if (game.isLost()) {
                Label loseLabel = new Label("You lost, " + name);
                loseLabel.setFont(Font.font(15));
                endPane.getChildren().add(loseLabel);
                endScreen = new Scene(endPane, 300, 100);
                primaryStage.setScene(endScreen);
            }
            // newGameButton is clicked
            newGameButton.setOnMouseClicked(e -> {
                // Calls the start() method, taking in the original primaryStagae, restarting the game.
                start(primaryStage);
            });
        }
    }
}
