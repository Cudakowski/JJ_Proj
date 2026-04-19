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

        public Scene createScene(Stage stage) {

                // Tworzenie roota - borderPane - bedzie po wszystkich bokach cos :p
                BorderPane root = new BorderPane();

                
        //TOP (status + czas + zapis)

                // Naglowek czyja tura jest
                Label status = new Label("Tura: Białe");
                
                // Naglowek czas rozgrywki graczy
                timer = new Label("Czas: 00:00, 00:00");
                
                // Przycisk do zapisania gry
                Button save = new Button("Zapisz");
                
                // Ustawiam te wszystkie elementy Kolo siebie
                HBox topBar = new HBox(20, status, timer, save);
                
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
                save.getStyleClass().add("btn-game");
                topBar.getStyleClass().add("panel-dark");
                historyTitle.getStyleClass().add("side-title");
                rightPanel.getStyleClass().add("panel-dark");
                back.getStyleClass().add("btn-game");
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
                whiteTurn = !whiteTurn;
        }



}
