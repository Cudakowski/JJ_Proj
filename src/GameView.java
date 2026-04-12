import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GameView {

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

       //TOP (status + czas + zapis)

        Label status = new Label("Tura: Białe");
        status.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label timer = new Label("Czas: 00:00, 00:00");
        timer.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button save = new Button("Zapisz");
        styleButton(save);

        HBox topBar = new HBox(20, status, timer, save);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2b2b2b;");
        root.setTop(topBar);


        // CENTER (plansza)
  
        GridPane board = createBoard();
        StackPane centerWrapper = new StackPane(board);
        centerWrapper.setPadding(new Insets(20));
        root.setCenter(centerWrapper);


        // RIGHT (historia ruchów)

        ListView<String> moveHistory = new ListView<>();
        moveHistory.getItems().addAll(
                "1. e4",
                "1... e5",
                "2. Nf3",
                "2... Nc6",
                "Dodac tu ruchy"
        );

        moveHistory.setStyle(
                "-fx-control-inner-background: #2b2b2b;" +
                "-fx-text-fill: white;"
        );

        VBox rightPanel = new VBox(10,
                new Label("Historia ruchów"),
                moveHistory
        );

        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(200);
        rightPanel.setStyle("-fx-background-color: #2b2b2b;");

        root.setRight(rightPanel);


        // BOTTOM (powrót)

        Button back = new Button("Powrót do menu");
        styleButton(back);

        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });

        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10));
        bottom.setStyle("-fx-background-color: #2b2b2b;");

        root.setBottom(bottom);

        // SCENE

        Scene scene = new Scene(root, 900, 800);

        stage.setMinWidth(900);
        stage.setMinHeight(800);

        return scene;
    }


    // PLANSZA

    private GridPane createBoard() {
        GridPane board = new GridPane();
        board.setStyle("-0, 0fx-effect: dropshadow(gaussian, black, 20, 0.5, );");
        board.setAlignment(Pos.CENTER);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                StackPane square = new StackPane();
                square.setPrefSize(70, 70);

                String color = ((row + col) % 2 == 0) ? "#f0d9b5" : "#b58863";
                square.setStyle("-fx-background-color: " + color + ";");

                board.add(square, col, row);
            }
        }

        return board;
    }


    // BUTTON STYLE

    private void styleButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: #444;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 6 14 6 14;"
        );

        btn.setOnMouseEntered(e ->
                btn.setStyle(
                        "-fx-background-color: #666;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 14 6 14;"
                )
        );

        btn.setOnMouseExited(e ->
                btn.setStyle(
                        "-fx-background-color: #444;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 14 6 14;"
                )
        );
    }
}
