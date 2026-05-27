package com.jjproj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {

    // --- Pola UI ---
    private Label timer;
    private Label statusGry;
    private Button save;
    private ListView<String> moveHistory;
    private final StackPane[][] squares = new StackPane[8][8];

    // --- Logika Gry ---
    private int whiteTime = 0;
    private int blackTime = 0;
    private boolean whiteTurn = true;
    private String mojKolor = "Bialy";
    private String wybranyCzas = "Bez ograniczen";
    private String lastBeatenPiece = "";
    private final String[][] pieces = new String[8][8];
    private final Map<String, List<String>> legalMovesMap = new HashMap<>();
    private Timeline gameTimer;
    
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

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-dark");

        // --- TOP BAR ---
        statusGry = new Label("Tura: Białe");
        statusGry.getStyleClass().add("statusGry-label");
        statusGry.setMinWidth(150);
        statusGry.setPrefWidth(150);

        timer = new Label("Czas: 00:00, 00:00");
        timer.getStyleClass().add("timer-label");
        timer.setMinWidth(400);
        timer.setPrefWidth(400);

        save = new Button("Zapisz");
        save.getStyleClass().add("btn-main");
        save.setMinWidth(150);
        save.setPrefWidth(150);

        boolean isTimed = !wybranyCzas.equals("Bez ograniczen");
        timer.setVisible(isTimed);
        save.setVisible(!isTimed);

        HBox topBar = new HBox(20, timer, save);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("panel-dark");
        root.setTop(topBar);

        // --- CENTER (Szachownica i współrzędne) ---
        GridPane board = createBoard();
        BorderPane boardWithCoords = setupBoardWithCoordinates(board);
        
        StackPane centerWrapper = new StackPane(boardWithCoords);
        centerWrapper.setPadding(new Insets(10));
        centerWrapper.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setCenter(centerWrapper);

        // --- RIGHT (Historia ruchów) ---
        moveHistory = new ListView<>();
        Label historyTitle = new Label("Historia ruchów");
        historyTitle.getStyleClass().add("side-title");
        
        HBox titleBox = new HBox(historyTitle);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox rightPanel = new VBox(10, titleBox, moveHistory);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(350);
        rightPanel.getStyleClass().add("panel-dark");
        root.setRight(rightPanel);

        // --- BOTTOM (Menu i Status) ---
        Button back = new Button("Powrót do menu");
        back.getStyleClass().add("btn-main");
        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });

        HBox backBox = new HBox(back);
        backBox.setAlignment(Pos.CENTER);

        Label status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        VBox bottomContainer = new VBox(10, backBox, status);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(10));
        bottomContainer.getStyleClass().add("panel-dark");
        root.setBottom(bottomContainer);

        // --- SCENE SETUP ---
        Scene scene = new Scene(root, 1200, 1200);
        stage.setMinWidth(1200);
        stage.setMinHeight(1200);
        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());

        if (isTimed) startTimer();

        SceneManager.registerStatusLabel(status);
        SceneManager.registerGameView(this);

        return scene;
    }

    private BorderPane setupBoardWithCoordinates(GridPane board) {
        HBox topLetters = createLetterRow();
        HBox bottomLetters = createLetterRow();
        VBox leftNumbers = createNumberCol();
        VBox rightNumbers = createNumberCol();

        BorderPane pane = new BorderPane();
        pane.setCenter(board);
        pane.setTop(topLetters);
        pane.setBottom(bottomLetters);
        pane.setLeft(leftNumbers);
        pane.setRight(rightNumbers);
        return pane;
    }

    private GridPane createBoard() {
        GridPane board = new GridPane();
        board.getStyleClass().add("board");
        board.setAlignment(Pos.CENTER);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = createSquare(row, col);
                squares[row][col] = square;
                board.add(square, col, row);
            }
        }

        if (mojKolor.equals("Czarny")) board.setRotate(180);
        refreshBoard();
        return board;
    }

    private StackPane createSquare(int row, int col) {
        StackPane square = new StackPane();
        square.setMinSize(80, 80);
        square.setPrefSize(80, 80);
        square.setMaxSize(80, 80);
        square.getStyleClass().add((row + col) % 2 == 0 ? "square-light" : "square-dark");

        square.setOnDragOver(e -> {
            if (e.getGestureSource() != square && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        square.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String[] data = db.getString().split(",");
                int oldRow = Integer.parseInt(data[0]);
                int oldCol = Integer.parseInt(data[1]);
                if (oldRow != row || oldCol != col) {
                    handleMove(oldRow, oldCol, row, col);
                    success = true;
                }
            }
            e.setDropCompleted(success);
            e.consume();
        });

        return square;
    }

    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = squares[row][col];
                square.getChildren().clear();

                if (!pieces[row][col].isEmpty()) {
                    Label piece = createPieceLabel(row, col);
                    square.getChildren().add(piece);
                }
            }
        }
    }

    private Label createPieceLabel(int row, int col) {
        Label piece = new Label(pieces[row][col]);
        piece.getStyleClass().add("chess-piece");
        if (mojKolor.equals("Czarny")) piece.setRotate(180);

        piece.setOnMouseClicked(e -> {
            if (!squares[row][col].getChildren().stream().anyMatch(n -> n.getStyleClass().contains("move-dot"))) {
                selectedRow = row;
                selectedCol = col;
                showPossibleMoves(row, col);
            }
            e.consume();
        });

        piece.setOnDragDetected(e -> {
            Dragboard db = piece.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(row + "," + col);
            db.setContent(content);
            e.consume();
        });

        return piece;
    }

    private void handleMove(int oldRow, int oldCol, int newRow, int newCol) {
        String fromSquare = "" + convertColToFile(oldCol) + convertRowToRank(oldRow);
        String toSquare = "" + convertColToFile(newCol) + convertRowToRank(newRow);

        legalMovesMap.clear();
        clearHighlights();
        clearSelectionStyle();

        lastBeatenPiece = pieces[newRow][newCol];
        pieces[newRow][newCol] = pieces[oldRow][oldCol];
        pieces[oldRow][oldCol] = "";

        refreshBoard();

        new Thread(() -> NetworkManager.sendCommand("MOVE|" + fromSquare + "|" + toSquare)).start();
    }

    private void showPossibleMoves(int row, int col) {
        clearHighlights();
        clearSelectionStyle();
        squares[row][col].getStyleClass().add("square-selected");

        String clickedSquare = "" + convertColToFile(col) + convertRowToRank(row);
        if (legalMovesMap.containsKey(clickedSquare)) {
            for (String dest : legalMovesMap.get(clickedSquare)) {
                int r = convertRankToRow(dest.charAt(1));
                int c = convertFileToCol(dest.charAt(0));
                
                StackPane square = squares[r][c];
                Label dot = new Label("●");
                dot.getStyleClass().add("move-dot");
                
                dot.setOnMouseClicked(e -> { handleMove(row, col, r, c); e.consume(); });
                square.setOnMouseClicked(e -> {
                    if (square.getChildren().stream().anyMatch(n -> n.getStyleClass().contains("move-dot"))) {
                        handleMove(row, col, r, c);
                    }
                });
                square.getChildren().add(dot);
            }
        }
    }

    // --- ZARZĄDZANIE CZASEM ---
    private void startTimer() {
        timer.setText("Czas: " + formatTime(whiteTime) + " | " + formatTime(blackTime));
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (whiteTurn) {
                whiteTime--;
                if (whiteTime <= 0) wygasłCzasGracza("Białe");
            } else {
                blackTime--;
                if (blackTime <= 0) wygasłCzasGracza("Czarne");
            }
            timer.setText("Czas: " + formatTime(whiteTime) + " | " + formatTime(blackTime));
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void wygasłCzasGracza(String ktoPrzegral) {
        stopTimer();
        clearMoves();
        String zwycięzca = ktoPrzegral.equals("Białe") ? "CZARNE" : "BIAŁE";
        SceneManager.setStatus("KONIEC GRY! Koniec czasu dla: " + ktoPrzegral + ". Wygrywa: " + zwycięzca);
    }

    private void ustawPoczatkowyCzas(String czasGry) {
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

    // --- POMOCNICZE ---
    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    public void changeTurn() {
        whiteTurn = !whiteTurn;
        statusGry.setText(whiteTurn ? "Tura: białe" : "Tura: czarne");
    }

    private void clearHighlights() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c].getChildren().removeIf(n -> n.getStyleClass().contains("move-dot"));
            }
        }
    }

    private void clearSelectionStyle() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c].getStyleClass().remove("square-selected");
            }
        }
    }

    private HBox createLetterRow() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(0);
        for (char f : new char[]{'A','B','C','D','E','F','G','H'}) {
            Label l = new Label(String.valueOf(f));
            l.getStyleClass().add("coord-label");
            l.setMinWidth(80);
            l.setPrefWidth(80);
            l.setAlignment(Pos.CENTER);
            hbox.getChildren().add(l);
        }
        return hbox;
    }

    private VBox createNumberCol() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(0);
        for (int i = 8; i >= 1; i--) {
            Label l = new Label(String.valueOf(i));
            l.getStyleClass().add("coord-label");
            l.setMinHeight(80);
            l.setPrefHeight(80);
            l.setAlignment(Pos.CENTER);
            vbox.getChildren().add(l);
        }
        return vbox;
    }

    // --- OBSŁUGA ZEWNĘTRZNEGO FORMATU (SERWER) ---
    public void updateBoardFromFEN(String fen) {
        String boardPart = fen.split(" ")[0];
        String[] ranks = boardPart.split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : ranks[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    int empty = Character.getNumericValue(c);
                    for (int i = 0; i < empty; i++) pieces[row][col++] = "";
                } else {
                    pieces[row][col++] = fenCharToUnicode(c);
                }
            }
        }
        refreshBoard();
    }

    private String fenCharToUnicode(char c) {
        switch (c) {
            case 'r': return "♜";
            case 'n': return "♞";
            case 'b': return "♝";
            case 'q': return "♛";
            case 'k': return "♚";
            case 'p': return "♟";
            case 'R': return "♖";
            case 'N': return "♘";
            case 'B': return "♗";
            case 'Q': return "♕";
            case 'K': return "♔";
            case 'P': return "♙";
            default: return "";
        }
    }

    public void applyLocalMove(String from, String to) {
        int oldRow = convertRankToRow(from.charAt(1)), oldCol = convertFileToCol(from.charAt(0));
        int newRow = convertRankToRow(to.charAt(1)), newCol = convertFileToCol(to.charAt(0));
        pieces[newRow][newCol] = pieces[oldRow][oldCol];
        pieces[oldRow][oldCol] = "";
        refreshBoard();
        changeTurn();
    }

    public void revertLocalMove(String from, String to) {
        int oldRow = convertRankToRow(from.charAt(1)), oldCol = convertFileToCol(from.charAt(0));
        int newRow = convertRankToRow(to.charAt(1)), newCol = convertFileToCol(to.charAt(0));
        pieces[oldRow][oldCol] = pieces[newRow][newCol];
        pieces[newRow][newCol] = lastBeatenPiece;
        refreshBoard();
    }

    public void updateLegalMoves(String dataString) {
        legalMovesMap.clear();
        if (dataString == null || dataString.trim().isEmpty()) return;
        for (String group : dataString.split(";")) {
            if (group.trim().isEmpty()) continue;
            String[] coords = group.split(",");
            if (coords.length > 1) {
                List<String> destinations = new ArrayList<>();
                for (int i = 1; i < coords.length; i++) destinations.add(coords[i]);
                legalMovesMap.put(coords[0], destinations);
            }
        }
    }

    public void stopTimer() { if (gameTimer != null) gameTimer.stop(); }
    public void clearMoves() { legalMovesMap.clear(); clearHighlights(); }
    public void addMoveToHistory(String notation) {
        moveHistory.getItems().add(notation);
        moveHistory.scrollTo(moveHistory.getItems().size() - 1);
    }

    private int convertFileToCol(char f) { return Character.toUpperCase(f) - 'A'; }
    private int convertRankToRow(char r) { return '8' - r; }
    private char convertColToFile(int c) { return (char) ('A' + c); }
    private char convertRowToRank(int r) { return (char) ('8' - r); }
}