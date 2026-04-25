package com.jjproj;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {

        private int whiteTime = 0;
        private int blackTime = 0;
        private boolean whiteTurn = true;
        private Label timer;
        private Timeline gameTimer;
        private Label status;

        public Scene createScene(Stage stage) {

                // Tworzenie roota - borderPane - bedzie po wszystkich bokach cos :p
                BorderPane root = new BorderPane();

                
        //TOP (status + czas + zapis)

                // Naglowek czyja tura jest
                status = new Label("Tura: Białe");
                
                // Naglowek czas rozgrywki graczy
                timer = new Label("Czas: 00:00, 00:00");
                
                // Przycisk do zmiany tury gry
                Button changeTurnButton = new Button("Zmien ture");

                // Przycisk do zmiany tury gry
                Button save = new Button("Zapisz");

                // Klikajac zmienia sie tura 
                changeTurnButton.setOnAction(e -> changeTurn());
                
                // Ustawiam te wszystkie elementy Kolo siebie
                HBox topBar = new HBox(20, status, timer, changeTurnButton, save);
                
                // Dodaje miedzy nimi odstep 10 px
                topBar.setPadding(new Insets(10));

                // Ustawiam je pośrodku
                topBar.setAlignment(Pos.CENTER_LEFT);

                // Dodaje do roota
                root.setTop(topBar);


        // CENTER (plansza)
        
                // Tworzenie planszy - osobna funkcja (nizej implementacja)
                GridPane board = createBoard();

                // Robie taki wrapper zeby tam umiescic nasza plansze
                StackPane centerWrapper = new StackPane(board);

                // Dodaje odstep 20 px
                centerWrapper.setPadding(new Insets(20));

                // Dodaje wrapper z plansza do roota!
                root.setCenter(centerWrapper);


        // RIGHT (historia ruchów)

                // Tutaj robie historie ruchow
                ListView<String> moveHistory = new ListView<>();

                // Dodaje elementy - poki co pogladowo
                moveHistory.getItems().addAll(
                        "1. e4",
                        "1... e5",
                        "2. Nf3",
                        "2... Nc6",
                        "Dodac tu ruchy"
                );

                // Etykieta ze to jest historia ruchow
                Label historyTitle = new Label("Historia ruchów");
                
                // Robie z tego VBox - bedzie jedno nad drugim
                VBox rightPanel = new VBox(10, historyTitle, moveHistory);

                // Ustawiam odstepy po 10 px z kazdej strony
                rightPanel.setPadding(new Insets(10));

                // Ustawiam szerokosc tej historii ruchow
                rightPanel.setPrefWidth(200);
                
                // dodaje do naszego okna
                root.setRight(rightPanel);


        // BOTTOM (powrót)

                // Przycisk powrotu do menu
                Button back = new Button("Powrót do menu");
                

                // Jak sie klinknie to wraca do menu :D
                back.setOnAction(e -> {
                        MenuView menu = new MenuView();
                        stage.setScene(menu.createScene(stage));
                });


                // Dodaje to do Hboxa , moze cos jeszcze tu wymysle
                HBox bottom = new HBox(back);
                
                // Ustawiam eleganco na srodek
                bottom.setAlignment(Pos.CENTER);

                // Dodaje odstepy po 10 px z kazdej stony
                bottom.setPadding(new Insets(10));

                // dodaje hbox do naszego okna
                root.setBottom(bottom);

        // SCENE

                Scene scene = new Scene(root, 900, 800);

        
                // Stylizowanie elementow

                root.getStyleClass().add("root-dark");
                status.getStyleClass().add("status-label");
                timer.getStyleClass().add("timer-label");
                save.getStyleClass().add("btn-main");
                changeTurnButton.getStyleClass().add("btn-main");
                topBar.getStyleClass().add("panel-dark");
                historyTitle.getStyleClass().add("side-title");
                rightPanel.getStyleClass().add("panel-dark");
                back.getStyleClass().add("btn-main");
                bottom.getStyleClass().add("panel-dark");


                scene.getStylesheets().add(
                        getClass().getResource("/view.css").toExternalForm()
                );


                // Minimalna wielkość okna

                stage.setMinWidth(900);
                stage.setMinHeight(800);

                startTimer();

                return scene;
        }


        // PLANSZA

        private GridPane createBoard() {

                // Tworze grid pane - nasza plansza bedzie taka siatka
                GridPane board = new GridPane();

                // Ustawiam wyglad naszej planszy
                board.getStyleClass().add("board");

                // Plansza będzoe na srodku
                board.setAlignment(Pos.CENTER);

        // PIONKI

                // Pionki są w tablicy, na razie po prostu symbole z unicode, potem zmienie na cos fajniejszego
                String[][] pieces = {
                        {"♜","♞","♝","♛","♚","♝","♞","♜"},
                        {"♟","♟","♟","♟","♟","♟","♟","♟"},
                        {"","","","","","","",""},
                        {"","","","","","","",""},
                        {"","","","","","","",""},
                        {"","","","","","","",""},
                        {"♙","♙","♙","♙","♙","♙","♙","♙"},
                        {"♖","♘","♗","♕","♔","♗","♘","♖"}
                };


                for (int row = 0; row < 8; row++) 
                {
                        for (int col = 0; col < 8; col++)
                        {
                                // Dla kazdego pola robie osobne okienko
                                StackPane square = new StackPane();

                                // Ustawiam jego wielkość
                                square.setPrefSize(70, 70);

                                // Tutaj stylizuje - odpowiednio jasne albo ciemne pole w zaleznosci od parzystosci
                                if ((row + col) % 2 == 0)
                                        square.getStyleClass().add("square-light");
                                else
                                        square.getStyleClass().add("square-dark");


                                // Wiec tak, jesli jest pionek w danym miejscu, to tam tworze nowy label z nim, na pewno trzeba zmienic bo ruchy tych pionkow beda, ale teraz tak pogladowo
                                if (!pieces[row][col].equals("")) 
                                {
                                        // Kazdy pionek to label
                                        Label piece = new Label(pieces[row][col]);

                                        // Ustawiamy jego styl
                                        piece.getStyleClass().add("chess-piece");

                                        // Dodajemy pionka do pola w gridzie
                                        square.getChildren().add(piece);
                                }

                                // Do calego grida dodajemy pole w odpowiednie miejsce (col, row)
                                board.add(square, col, row);
                        }
                }

                return board;
        }




        private void startTimer() {

                gameTimer = new Timeline(
                        new KeyFrame(Duration.seconds(1), e -> {

                                if (whiteTurn)
                                        whiteTime++;
                                else
                                        blackTime++;

                                timer.setText(
                                        "Czas: "
                                        + formatTime(whiteTime)
                                        + ", "
                                        + formatTime(blackTime)
                                );
                        })
                );

                gameTimer.setCycleCount(Timeline.INDEFINITE);
                gameTimer.play();
        }

        private String formatTime(int totalSeconds) {

                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                return String.format("%02d:%02d", minutes, seconds);
        }

        public void changeTurn() {
                if (whiteTurn)
                        status.setText("Tura: czarne");
                else 
                        status.setText("Tura: białe");
                whiteTurn = !whiteTurn;
                
        }



}
// package com.jjproj;

// import com.jjproj.Logic.Board;
// import com.jjproj.Logic.Coordinates;
// import com.jjproj.Logic.File;
// import com.jjproj.Logic.Color;
// import com.jjproj.Logic.piece.Piece;
// import com.jjproj.Logic.piece.Pawn;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.layout.*;
// import javafx.stage.Stage;

// public class GameView {

//     private Board board; // Twoja plansza z logiką
    
//     // Konstruktor z Board
//     public GameView(Board board) {
//         this.board = board;
//     }
    
//     public Scene createScene(Stage stage) {

//         BorderPane root = new BorderPane();
//         root.setStyle("-fx-background-color: #1e1e1e;");

//         // TOP (status + czas + zapis)
//         Label status = new Label("Tura: Białe");
//         status.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

//         Label timer = new Label("Czas: 00:00, 00:00");
//         timer.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

//         Button save = new Button("Zapisz");
//         styleButton(save);

//         HBox topBar = new HBox(20, status, timer, save);
//         topBar.setPadding(new Insets(10));
//         topBar.setAlignment(Pos.CENTER_LEFT);
//         topBar.setStyle("-fx-background-color: #2b2b2b;");
//         root.setTop(topBar);

//         // CENTER (plansza)
//         GridPane chessBoard = createBoard(); // ← zmieniłam nazwę na chessBoard
//         StackPane centerWrapper = new StackPane(chessBoard);
//         centerWrapper.setPadding(new Insets(20));
//         root.setCenter(centerWrapper);

//         // RIGHT (historia ruchów)
//         ListView<String> moveHistory = new ListView<>();
//         moveHistory.getItems().addAll(
//                 "1. e4",
//                 "1... e5",
//                 "2. Nf3",
//                 "2... Nc6",
//                 "Dodac tu ruchy"
//         );

//         moveHistory.setStyle(
//                 "-fx-control-inner-background: #2b2b2b;" +
//                 "-fx-text-fill: white;"
//         );

//         VBox rightPanel = new VBox(10,
//                 new Label("Historia ruchów"),
//                 moveHistory
//         );

//         rightPanel.setPadding(new Insets(10));
//         rightPanel.setPrefWidth(200);
//         rightPanel.setStyle("-fx-background-color: #2b2b2b;");

//         root.setRight(rightPanel);

//         // BOTTOM (powrót)
//         Button back = new Button("Powrót do menu");
//         styleButton(back);

//         back.setOnAction(e -> {
//             MenuView menu = new MenuView();
//             stage.setScene(menu.createScene(stage));
//         });

//         HBox bottom = new HBox(back);
//         bottom.setAlignment(Pos.CENTER);
//         bottom.setPadding(new Insets(10));
//         bottom.setStyle("-fx-background-color: #2b2b2b;");

//         root.setBottom(bottom);

//         // SCENE
//         Scene scene = new Scene(root, 900, 800);
//         stage.setMinWidth(900);
//         stage.setMinHeight(800);

//         return scene;
//     }

//     // PLANSZA
//     private GridPane createBoard() {
//         GridPane boardGrid = new GridPane(); // ← zmieniłam nazwę
//         boardGrid.setStyle("-fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);");
//         boardGrid.setAlignment(Pos.CENTER);

//         for (int row = 0; row < 8; row++) {
//             for (int col = 0; col < 8; col++) {
//                 int rank = 8 - row;
//                 File file = File.values()[col];

//                 StackPane square = new StackPane();
//                 square.setPrefSize(70, 70);

//                 String color = ((row + col) % 2 == 0) ? "#f0d9b5" : "#b58863";
//                 square.setStyle("-fx-background-color: " + color + ";");

//                 Coordinates coords = new Coordinates(file, rank);
//                 //Piece piece = this.board.pieces.get(coords); // ← użyj this.board
//                 Piece piece = this.board.getPieces().get(coords);

//                 // Jeśli jest figura, dodaj jej symbol/obrazek
//                 if (piece != null) {
//                     Label pieceLabel = new Label(getPieceSymbol(piece));
//                     pieceLabel.setStyle(getPieceStyle(piece));
//                     pieceLabel.setFont(javafx.scene.text.Font.font(40));
//                     square.getChildren().add(pieceLabel);
//                 }

//                 boardGrid.add(square, col, row); // ← boardGrid, nie board
//             }
//         }

//         return boardGrid;
//     }

//     // Dodaj te metody!
//     private String getPieceSymbol(Piece piece) {
//         if (piece instanceof Pawn) {
//             return piece.color == Color.WHITE ? "♙" : "♟";
//         }
//         // Tu dodasz inne figury później
//         return "?";
//     }

//     private String getPieceStyle(Piece piece) {
//         if (piece.color == Color.WHITE) {
//             return "-fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 1, 0.5, 0, 0);";
//         } else {
//             return "-fx-text-fill: black; -fx-font-weight: bold;";
//         }
//     }

//     // BUTTON STYLE
//     private void styleButton(Button btn) {
//         btn.setStyle(
//                 "-fx-background-color: #444;" +
//                 "-fx-text-fill: white;" +
//                 "-fx-font-size: 14px;" +
//                 "-fx-background-radius: 8;" +
//                 "-fx-padding: 6 14 6 14;"
//         );

//         btn.setOnMouseEntered(e ->
//                 btn.setStyle(
//                         "-fx-background-color: #666;" +
//                         "-fx-text-fill: white;" +
//                         "-fx-font-size: 14px;" +
//                         "-fx-background-radius: 8;" +
//                         "-fx-padding: 6 14 6 14;"
//                 )
//         );

//         btn.setOnMouseExited(e ->
//                 btn.setStyle(
//                         "-fx-background-color: #444;" +
//                         "-fx-text-fill: white;" +
//                         "-fx-font-size: 14px;" +
//                         "-fx-background-radius: 8;" +
//                         "-fx-padding: 6 14 6 14;"
//                 )
//         );
//     }
// }
