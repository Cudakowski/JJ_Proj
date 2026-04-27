package com.jjproj;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
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
        private StackPane[][] squares = new StackPane[8][8];

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

                HBox titleBox = new HBox(historyTitle);
                titleBox.setAlignment(Pos.CENTER);
                
                // Robie z tego VBox - bedzie jedno nad drugim
                VBox rightPanel = new VBox(10, titleBox, moveHistory);

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

                status.setMinWidth(150);
                status.setPrefWidth(150);

                timer.setMinWidth(180);
                timer.setPrefWidth(180);

                save.setMinWidth(150);
                save.setPrefWidth(150);

                changeTurnButton.setMinWidth(150);
                changeTurnButton.setPrefWidth(150);

                // Minimalna wielkość okna

                stage.setMinWidth(900);
                stage.setMinHeight(800);

                startTimer();

                return scene;
        }


        // PLANSZA

 private GridPane createBoard() {

        GridPane board = new GridPane();
        board.getStyleClass().add("board");
        board.setAlignment(Pos.CENTER);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                StackPane square = new StackPane();
                square.setPrefSize(80, 80);

                if ((row + col) % 2 == 0)
                    square.getStyleClass().add("square-light");
                else
                    square.getStyleClass().add("square-dark");

                final int r = row; // final zeby moglo byc w eventach
                final int c = col;

                // tutaj zapisuje pole zebym potem latwiej mogla sie odwolac od niefo
                squares[row][col] = square;

                // DROP (obzluga przeciagania)
                square.setOnDragOver(e -> {
                    if (e.getGestureSource() != square && e.getDragboard().hasString()) { // zeby nie przeciagnac na to samo pole i jesli cos juz jest (to potem zmienie bo wiadomo pionk sie zbija xd)
                        e.acceptTransferModes(TransferMode.MOVE); // jesli powzysze spelnion to mozna figurke dac na dane pole
                    }
                    e.consume();// nie rpzekazywac dalej zdarzenia
                });

                // square.setOnDragEntered(e -> square.getStyleClass().add("square-hover"));

                // square.setOnDragExited(e -> square.getStyleClass().remove("square-hover"));

                // zakomentowalam bo z jakiegos powodu sie zmienia rozmiar calego wiersza przy przeciaganiu

                square.setOnDragDropped(e -> {

                // pobieranie danych
                Dragboard db = e.getDragboard();
                boolean success = false;

                if (db.hasString()) {

                        // dane figurki
                        String[] data = db.getString().split(",");

                        int oldRow = Integer.parseInt(data[0]);
                        int oldCol = Integer.parseInt(data[1]);

                        // 1. ten sam square - blokada
                        if (oldRow == r && oldCol == c) {
                        e.setDropCompleted(false);
                        e.consume();
                        return;
                        }

                        // 2. pole zajęte ----------(to do zmiany przy zbijaniu)
                        if (!pieces[r][c].equals("")) {
                        e.setDropCompleted(false);
                        e.consume();
                        return;
                        }

                        // przesuniacie figuty
                        String piece = pieces[oldRow][oldCol];

                        // nowe pole dodaje figure a stare zostanie piste
                        pieces[r][c] = piece;
                        pieces[oldRow][oldCol] = "";

                        // funkcja odswiezania planszy
                        refreshBoard();

                        success = true;
                }

                e.setDropCompleted(success);
                e.consume();
                });

                // dodaje pole do planszy
                board.add(square, col, row);
            }
        }

        // potem cala plansze odswiezam
        refreshBoard();

        return board;
    }

    // czyszcze plansze i dodaje pionki od nowa - dzieki temu plansza sie aktualizuje
    private void refreshBoard() {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                // pobieram pole
                StackPane square = squares[row][col];
                square.getChildren().clear(); // czyszce pole

                if (!pieces[row][col].equals("")) { // jesli jest figura to ponizej towrze label ze znaczkeim figury

                    Label piece = new Label(pieces[row][col]);
                    piece.getStyleClass().add("chess-piece");

                    final int r = row; // zapisuje pozycje pierwotne
                    final int c = col;

                    // start przeciagania
                    piece.setOnDragDetected(e -> {

                        // dworze dragboard
                        Dragboard db = piece.startDragAndDrop(TransferMode.MOVE);

                        // zapisuje dane
                        ClipboardContent content = new ClipboardContent();
                        content.putString(r + "," + c);

                        // podczas przeciagania dane leca dlaej z figurka
                        db.setContent(content);

                        e.consume();
                    });

                    // dodaje podaja figurke do pola
                    square.getChildren().add(piece);
                }
            }
        }
    }

        // Uruchamianie liczbika czasu
        private void startTimer() {

                // Tworze timeline (animacja dzialajaca coo 1 s)
                gameTimer = new Timeline(
                        new KeyFrame(Duration.seconds(1), e -> {

                                if (whiteTurn)
                                        whiteTime++;
                                else
                                        blackTime++;

                                timer.setText("Czas: "+ formatTime(whiteTime)+ ", "+ formatTime(blackTime)); // wypisuje czas odliczany dla bualych i czarnych
                        })
                );
                // tutaj zeby sie do dzialo w nieskonczaonosc
                gameTimer.setCycleCount(Timeline.INDEFINITE);

                // start timera
                gameTimer.play();
        }

        // tutaj zamieniam sekundy na minuty i sekundy
        private String formatTime(int totalSeconds) {

                int minutes = totalSeconds/60;
                int seconds = totalSeconds%60;

                return String.format("%02d:%02d", minutes, seconds);
        }

        // to potrzebne do zmiany tury, raczej nie bedzie potem takiego przycisku ale dodalam ja na razie bo chcialam zobaczyc jak to bedzoe wygladalo z timerem i wgl :DDD
        public void changeTurn() {
                if (whiteTurn)
                        status.setText("Tura: czarne");
                else 
                        status.setText("Tura: białe");
                whiteTurn = !whiteTurn;
                
        }










}
