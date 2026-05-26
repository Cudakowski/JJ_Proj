package com.jjproj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {

    private int whiteTime = 0;
    private int blackTime = 0;
    private boolean whiteTurn = true;
    private Label timer;
    private Timeline gameTimer;
    private Label statusGry;
    private StackPane[][] squares = new StackPane[8][8];

    private String mojKolor = "Bialy";
    private String wybranyCzas = "Bez ograniczen";
    private Button save; 

    // Pionki są w tablicy, na razie po prostu symbole z unicode, potem zmienie na cos fajniejszego
    // String[][] pieces = {
    //     {"♜","♞","♝","♛","♚","♝","♞","♜"},
    //     {"♟","♟","♟","♟","♟","♟","♟","♟"},
    //     {"","","","","","","",""},
    //     {"","","","","","","",""},
    //     {"","","","","","","",""},
    //     {"","","","","","","",""},
    //     {"♙","♙","♙","♙","♙","♙","♙","♙"},
    //     {"♖","♘","♗","♕","♔","♗","♘","♖"}
    // };

    private String lastBeatenPiece ="";

    String[][] pieces = new String[8][8];
    private Map<String, List<String>> legalMovesMap = new HashMap<>();
    private ListView<String> moveHistory;

    // Do klikania na figure zeby widziec gdzie moozna ja poruszyc
    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameView() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                pieces[row][col] = "";
            }
        }
    }

    public Scene createScene(Stage stage, String mojKolor, String przeciwnik, String czasGry) {
        this.mojKolor = mojKolor;
        this.wybranyCzas = czasGry;
        
        ustawPoczatkowyCzas(czasGry);


        // Tworzenie roota - borderPane - bedzie po wszystkich bokach cos :p
        BorderPane root = new BorderPane();

        Label status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);
            
     //TOP (statusGry + czas + zapis)

        // Naglowek czyja tura jest
        statusGry = new Label("Tura: Białe");
        
        // Naglowek czas rozgrywki graczy
        timer = new Label("Czas: 00:00, 00:00");


                
        // Przycisk do zmiany tury gry
        Button changeTurnButton = new Button("Zmien ture");

        // Przycisk do zmiany tury gry
        save = new Button("Zapisz");



        // Klikajac zmienia sie tura 
        changeTurnButton.setOnAction(e -> changeTurn());

        if (wybranyCzas.equals("Bez ograniczen")) {
            timer.setVisible(false);       // Ukrywamy licznik
            save.setVisible(true);   // Pokazujemy przycisk zapisu
        } else {
            timer.setVisible(true);        // Pokazujemy licznik
            save.setVisible(false);  // Ukrywamy przycisk zapisu (blokada zapisu na czas)
        }
        
        // Ustawiam te wszystkie elementy Kolo siebie
        HBox topBar = new HBox(20, statusGry, timer, changeTurnButton, save);
        
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
        moveHistory = new ListView<>();

        // Dodaje elementy - poki co pogladowo
        // moveHistory.getItems().addAll(
        //         "1. e4",
        //         "1... e5",
        //         "2. Nf3",
        //         "2... Nc6",
        //         "Dodac tu ruchy"
        // );

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


        // BOTTOM (powrót do menu)

        // Przycisk powrotu do menu
        Button back = new Button("Powrót do menu");

        // Jak sie klinknie to wraca do menu :D
        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });



        

        HBox backBox = new HBox(back);
        backBox.setAlignment(Pos.CENTER);

        VBox bottomContainer = new VBox(10, backBox, status);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(10));


        root.setBottom(bottomContainer);

     // SCENE

        Scene scene = new Scene(root, 900, 800);


        // Stylizowanie elementow

        root.getStyleClass().add("root-dark");
        statusGry.getStyleClass().add("statusGry-label");
        timer.getStyleClass().add("timer-label");
        save.getStyleClass().add("btn-main");
        changeTurnButton.getStyleClass().add("btn-main");
        topBar.getStyleClass().add("panel-dark");
        historyTitle.getStyleClass().add("side-title");
        rightPanel.getStyleClass().add("panel-dark");
        back.getStyleClass().add("btn-main");
        bottomContainer.getStyleClass().add("panel-dark"); 


        scene.getStylesheets().add(
                getClass().getResource("/View.css").toExternalForm()
        );

        statusGry.setMinWidth(150);
        statusGry.setPrefWidth(150);

        timer.setMinWidth(180);
        timer.setPrefWidth(180);

        save.setMinWidth(150);
        save.setPrefWidth(150);

        changeTurnButton.setMinWidth(150);
        changeTurnButton.setPrefWidth(150);

        // Minimalna wielkość okna

        stage.setMinWidth(900);
        stage.setMinHeight(800);

        if (!wybranyCzas.equals("Bez ograniczen")) {
            startTimer(); // Odpalamy zegar tylko, gdy gra jest na czas
        }

        SceneManager.registerStatusLabel(status);
        SceneManager.registerGameView(this);

        return scene;
        // TODO: jakiś napis u góry kto z kim gra
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
                    // if (!pieces[r][c].equals("")) {
                    // e.setDropCompleted(false);
                    // e.consume();
                    // return;
                    // }

                    String fromSquare = "" + convertColToFile(oldCol) + convertRowToRank(oldRow);
                    String toSquare = "" + convertColToFile(c) + convertRowToRank(r);
                    
                    if (!legalMovesMap.containsKey(fromSquare) || !legalMovesMap.get(fromSquare).contains(toSquare)) {
                        e.setDropCompleted(false);
                        e.consume();
                        return;
                    }

                    legalMovesMap.clear();

                    lastBeatenPiece = pieces[r][c];

                    // // przesuniacie figuty
                    // String piece = pieces[oldRow][oldCol];

                    // // nowe pole dodaje figure a stare zostanie piste
                    // pieces[r][c] = piece;
                    // pieces[oldRow][oldCol] = "";

                    // // funkcja odswiezania planszy
                    // refreshBoard();

                    
                    
                    String piece = pieces[oldRow][oldCol];
                    pieces[r][c] = piece;
                    pieces[oldRow][oldCol] = "";
                    refreshBoard();
                    
                    new Thread(() -> {
                        NetworkManager.sendCommand("MOVE|" + fromSquare + "|" + toSquare);
                    }).start();

                    success = true;
                }

                e.setDropCompleted(success);
                e.consume();
                });

                // dodaje pole do planszy
                board.add(square, col, row);
            }
        }

        if (mojKolor.equals("Czarny")) {
            board.setRotate(180);
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

                    if (mojKolor.equals("Czarny")) {
                        piece.setRotate(180);
                    }

                    final int r = row; // zapisuje pozycje pierwotne
                    final int c = col;

                    piece.setOnMouseClicked(e -> {
                        selectedRow = r;
                        selectedCol = c;
                        showPossibleMoves(r, c);
                    });

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
    // private void startTimer() {

    //     // Tworze timeline (animacja dzialajaca coo 1 s)
    //     gameTimer = new Timeline(
    //         new KeyFrame(Duration.seconds(1), e -> {

    //             if (whiteTurn)
    //                     whiteTime++;
    //             else
    //                     blackTime++;

    //             timer.setText("Czas: "+ formatTime(whiteTime)+ ", "+ formatTime(blackTime)); // wypisuje czas odliczany dla bualych i czarnych
    //         })
    //     );
    //     // tutaj zeby sie do dzialo w nieskonczaonosc
    //     gameTimer.setCycleCount(Timeline.INDEFINITE);

    //     // start timera
    //     gameTimer.play();
    // }

    // tutaj zamieniam sekundy na minuty i sekundy
    private String formatTime(int totalSeconds) {

        int minutes = totalSeconds/60;
        int seconds = totalSeconds%60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    // to potrzebne do zmiany tury, raczej nie bedzie potem takiego przycisku ale dodalam ja na razie bo chcialam zobaczyc jak to bedzoe wygladalo z timerem i wgl :DDD
    public void changeTurn() {
        if (whiteTurn)
                statusGry.setText("Tura: czarne");
        else 
                statusGry.setText("Tura: białe");
        whiteTurn = !whiteTurn;
            
    }

    // Funkcja do czyszczenia kropek
    private void clearHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
            StackPane square = squares[row][col];

            square.getChildren().removeIf(node -> node.getStyleClass().contains("move-dot"));
            }
        }
    }

    //Funkcja pokazujaca mozliwe ruchy (na razie do okola) !!!!!!!!!!!!!!!!!!!!!!!!!! Połączyc z backendem
    private void showPossibleMoves(int row, int col) {
        clearHighlights();

        String clickedSquare = "" + convertColToFile(col) + convertRowToRank(row);
        
        if (legalMovesMap.containsKey(clickedSquare)) {
            
            List<String> allowedDestinations = legalMovesMap.get(clickedSquare);
            
            for (String dest : allowedDestinations) {
                int r = convertRankToRow(dest.charAt(1));
                int c = convertFileToCol(dest.charAt(0));
                
                StackPane square = squares[r][c];
                Label dot = new Label("●");
                dot.getStyleClass().add("move-dot");
                square.getChildren().add(dot);
            }
        }
    }
    
    public void updateBoardFromFEN(String fen) {
        String boardPart = fen.split(" ")[0]; 
        
        String[] ranks = boardPart.split("/"); 

        for (int row = 0; row < 8; row++) {
            String rankData = ranks[row];
            int col = 0;
            
            for (char c : rankData.toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptySquares = Character.getNumericValue(c);
                    for (int i = 0; i < emptySquares; i++) {
                        pieces[row][col++] = "";
                    }
                } else {
                    pieces[row][col++] = fenCharToUnicode(c);
                }
            }
        }
        
        refreshBoard();
    }

    private String fenCharToUnicode(char pieceChar) {
        switch (pieceChar) {
            case 'r': return "♜"; // czarna wieża
            case 'n': return "♞"; // czarny skoczek
            case 'b': return "♝"; // czarny goniec
            case 'q': return "♛"; // czarny hetman
            case 'k': return "♚"; // czarny król
            case 'p': return "♟"; // czarny pionek
            case 'R': return "♖"; // biała wieża
            case 'N': return "♘"; // biały skoczek
            case 'B': return "♗"; // biały goniec
            case 'Q': return "♕"; // biały hetman
            case 'K': return "♔"; // biały król
            case 'P': return "♙"; // biały pionek
            default: return "";
        }
    }

    // Wywoływane u Czarnego, gdy Biały zrobił poprawny ruch
    public void applyLocalMove(String from, String to) {
        int oldRow = convertRankToRow(from.charAt(1));
        int oldCol = convertFileToCol(from.charAt(0));
        
        int newRow = convertRankToRow(to.charAt(1));
        int newCol = convertFileToCol(to.charAt(0));
        
        // Przesuwamy figurę w tablicy
        pieces[newRow][newCol] = pieces[oldRow][oldCol];
        pieces[oldRow][oldCol] = "";
        
        // Odświeżamy GUI
        refreshBoard();
        changeTurn();
    }
    
    public void revertLocalMove(String from, String to) {
        int oldRow = convertRankToRow(from.charAt(1));
        int oldCol = convertFileToCol(from.charAt(0));
        
        int newRow = convertRankToRow(to.charAt(1));
        int newCol = convertFileToCol(to.charAt(0));
        
        pieces[oldRow][oldCol] = pieces[newRow][newCol];
        pieces[newRow][newCol] = lastBeatenPiece;
        
        refreshBoard();
    }

    private int convertFileToCol(char file) {
        return Character.toUpperCase(file) - 'A';
    }

    private int convertRankToRow(char rank) {
        return '8' - rank; 
    }
    
    private char convertColToFile(int col) {
        return (char) ('A' + col);
    }
    
    private char convertRowToRank(int row) {
        return (char) ('8' - row);
    }

    public void updateLegalMoves(String dataString) {
        legalMovesMap.clear();
        
        if (dataString == null || dataString.trim().isEmpty()) return;

        String[] pieceMoves = dataString.split(";");
        
        for (String movesGroup : pieceMoves) {
            if (movesGroup.trim().isEmpty()) continue;
            
            String[] coords = movesGroup.split(",");
            if (coords.length > 1) {
                String from = coords[0];
                List<String> toList = new ArrayList<>();
                
                for (int i = 1; i < coords.length; i++) {
                    toList.add(coords[i]);
                }
                
                legalMovesMap.put(from, toList);
            }
        }
    }

    // Zatrzymuje czas po macie/remisie
    public void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    // Blokuje możliwość ruszania figur
    public void clearMoves() {
        legalMovesMap.clear();
        clearHighlights();
    }

    public void addMoveToHistory(String moveNotation) {
        moveHistory.getItems().add(moveNotation);
        moveHistory.scrollTo(moveHistory.getItems().size() - 1); //przewijanie na sam dół
    }

    private void ustawPoczatkowyCzas(String czasGry) {
        // Pamiętaj: 10min to sumaryczny czas gry, czyli po 5 minut (300 sekund) na gracza!
        switch (czasGry) {
            case "10min":
                whiteTime = 300;
                blackTime = 300;
                break;
            case "20min":
                whiteTime = 600;
                blackTime = 600;
                break;
            case "40min":
                whiteTime = 1200;
                blackTime = 1200;
                break;
            case "60min":
                whiteTime = 1800;
                blackTime = 1800;
                break;
            default:
                whiteTime = 0;
                blackTime = 0;
                break;
        }
    }

    private void startTimer() {
        // Ustawiamy tekst początkowy przed startem animacji
        timer.setText("Czas: " + formatTime(whiteTime) + " | " + formatTime(blackTime));

        gameTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                if (whiteTurn) {
                    whiteTime--;
                    if (whiteTime <= 0) {
                        wygasłCzasGracza("Białe");
                    }
                } else {
                    blackTime--;
                    if (blackTime <= 0) {
                        wygasłCzasGracza("Czarne");
                    }
                }

                timer.setText("Czas: " + formatTime(whiteTime) + " | " + formatTime(blackTime));
            })
        );
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void wygasłCzasGracza(String ktoPrzegral) {
        stopTimer();
        clearMoves();
        String zwycięzca = ktoPrzegral.equals("Białe") ? "CZARNE" : "BIAŁE";
        SceneManager.setStatus("KONIEC GRY! Koniec czasu dla: " + ktoPrzegral + ". Wygrywa: " + zwycięzca);
        
    }
}
