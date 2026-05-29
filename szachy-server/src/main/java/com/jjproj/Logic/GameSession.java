package com.jjproj.Logic;

import com.jjproj.Logic.piece.*;
import java.util.Set;
import com.jjproj.ClientHandler;
import com.jjproj.DatabaseIntegration.GamesTable;
import com.jjproj.DatabaseIntegration.MovesTable;
import com.jjproj.DatabaseIntegration.PiecesTable;
import com.jjproj.DatabaseIntegration.UsersTable;

public class GameSession {
    
    private final Board board;
    private Color currentTurn;
    private boolean gameOver;
    private final String gameTime;

    private int whiteTimeLeft;
    private int blackTimeLeft;
    private long lastTimeCheck;
    private boolean isTimeLimited;

    private final ClientHandler whitePlayer;
    private final ClientHandler blackPlayer;

    private Integer gameId;
    private Integer whitePlayerDbId;
    private Integer blackPlayerDbId;

    private int halfMoveClock = 0; // liczba półruchów bez bicia
    private int fullMoveNumber = 1; //numer pełnego ruchu

    //dla roszady
    private boolean whiteKingsideCastle  = true;// biała krótka (K)
    private boolean whiteQueensideCastle = true; // biała długa (Q)
    private boolean blackKingsideCastle  = true; // czarna krótka (k)
    private boolean blackQueensideCastle = true; // czarna długa (q)

    //dla en passant
    private Coordinates enPassantTarget = null;//Pole bicia w przelocie

    public GameSession(ClientHandler whitePlayer, ClientHandler blackPlayer, String gameTime) {
        this.board = new Board();
        this.board.setupDefaultPiecesPositions();
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        this.gameTime=gameTime;

        switch (gameTime) {
            case "10min": whiteTimeLeft = 300; blackTimeLeft = 300; isTimeLimited = true; break;
            case "20min": whiteTimeLeft = 600; blackTimeLeft = 600; isTimeLimited = true; break;
            case "40min": whiteTimeLeft = 1200; blackTimeLeft = 1200; isTimeLimited = true; break;
            case "60min": whiteTimeLeft = 1800; blackTimeLeft = 1800; isTimeLimited = true; break;
            default: isTimeLimited = false; break; // Bez ograniczen
        }
        this.lastTimeCheck = System.currentTimeMillis();
        
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;

        this.whitePlayerDbId = UsersTable.getUserId(whitePlayer.getPlayerLogin());
        this.blackPlayerDbId = UsersTable.getUserId(blackPlayer.getPlayerLogin());
    }

    public GameSession(ClientHandler whitePlayer, ClientHandler blackPlayer, String fen, int gameIdFromDB) {
        this.board = new Board();
        this.gameTime = "Bez ograniczen"; // Wznawiane gry nie mają limitu czasowego
        this.isTimeLimited = false;
        this.gameOver = false;
        
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameId = gameIdFromDB;
        
        this.whitePlayerDbId = UsersTable.getUserId(whitePlayer.getPlayerLogin());
        this.blackPlayerDbId = UsersTable.getUserId(blackPlayer.getPlayerLogin());

        String[] fenParts = fen.split(" ");
        
        this.board.setupFromFEN(fen);
        
        if (fenParts.length > 1) {
            this.currentTurn = fenParts[1].equals("w") ? Color.WHITE : Color.BLACK;
        } else {
            this.currentTurn = Color.WHITE;
        }
        
        if (fenParts.length > 2) {
            String castling = fenParts[2];
            this.whiteKingsideCastle = castling.contains("K");
            this.whiteQueensideCastle = castling.contains("Q");
            this.blackKingsideCastle = castling.contains("k");
            this.blackQueensideCastle = castling.contains("q");
            
            if (!this.whiteKingsideCastle) board.revokeCastlingRight(Color.WHITE, true);
            if (!this.whiteQueensideCastle) board.revokeCastlingRight(Color.WHITE, false);
            if (!this.blackKingsideCastle) board.revokeCastlingRight(Color.BLACK, true);
            if (!this.blackQueensideCastle) board.revokeCastlingRight(Color.BLACK, false);
        }
        
        if (fenParts.length > 3 && !fenParts[3].equals("-")) {
            this.enPassantTarget = CoordinatesParser.parse(fenParts[3].toUpperCase());
            this.board.setEnPassantTarget(this.enPassantTarget);
        }
        
        if (fenParts.length > 5) {
            try {
                this.halfMoveClock = Integer.parseInt(fenParts[4]);
                this.fullMoveNumber = Integer.parseInt(fenParts[5]);
            } catch (NumberFormatException ignored) {}
        }
    }

    public void startGame() {
        this.whitePlayer.sendMessage("GAME_START|Bialy|" + this.blackPlayer + "|" + gameTime);
        this.blackPlayer.sendMessage("GAME_START|Czarny|" + this.whitePlayer+ "|" + gameTime);
            
        System.out.println("Rozpoczęto partię: " + this.whitePlayer + " (B) vs " + this.blackPlayer + " (C)");


        String startingFEN = boardToFEN();

        if (this.gameId == null && whitePlayerDbId != null && blackPlayerDbId != null) {
            this.gameId = GamesTable.createNewGame(whitePlayerDbId, blackPlayerDbId, startingFEN);
            System.out.println("Zarejestrowano nową grę w bazie danych. ID: " + this.gameId);
        } else {
            System.out.println("Wznowiono partię z bazy danych. ID: " + this.gameId);

            java.util.List<String[]> historicalMoves = MovesTable.getMovesForGame(this.gameId);
            int loopFullMoveNum = 1;
            boolean loopWhiteTurn = true;
            
            for (String[] m : historicalMoves) {
                String from = m[0];
                String to = m[1];
                String notation;
                
                if (loopWhiteTurn) {
                    notation = loopFullMoveNum + " ⚫ " + from.toLowerCase() + "-" + to.toLowerCase();
                    loopWhiteTurn = false;
                } else {
                    notation = loopFullMoveNum + " ⚪ " + from.toLowerCase() + "-" + to.toLowerCase();
                    loopWhiteTurn = true;
                    loopFullMoveNum++;
                }
                
                broadcast("NEW_MOVE|" + notation);
            }
        }
        
        whitePlayer.sendMessage("BOARD_UPDATE|" + startingFEN);
        blackPlayer.sendMessage("BOARD_UPDATE|" + startingFEN);

        String legalMoves = getAllLegalMovesFor(); 
        if (this.currentTurn == Color.WHITE) {
            whitePlayer.sendMessage("LEGAL_MOVES|" + legalMoves);
        } else {
            blackPlayer.sendMessage("LEGAL_MOVES|" + legalMoves);
        }
    }

  
    private void updateCastlingRights(Coordinates from, Piece piece) {
        if (piece instanceof King) {
            board.revokeCastlingRight(piece.color, true);
            board.revokeCastlingRight(piece.color, false);
        }
        if (piece instanceof Rook) {
            if(from.equals(new Coordinates(File.H, 1)))
                board.revokeCastlingRight(Color.WHITE, true);
            if(from.equals(new Coordinates(File.A, 1)))
                board.revokeCastlingRight(Color.WHITE, false);
            if(from.equals(new Coordinates(File.H, 8)))
                board.revokeCastlingRight(Color.BLACK, true);
            if(from.equals(new Coordinates(File.A, 8)))
                board.revokeCastlingRight(Color.BLACK, false);
        }
    }
    // перемещает ладью при рокировке
    private void handleCastling(Coordinates kingFrom, Coordinates kingTo) {
        int fileDiff = kingTo.file.ordinal() - kingFrom.file.ordinal();
        if (Math.abs(fileDiff) != 2) return;

        boolean kingSide = fileDiff > 0;
        int rank = kingFrom.rank;

        Coordinates rookFrom = new Coordinates(
            kingSide ? File.H : File.A, rank
        );
        Coordinates rookTo = new Coordinates(
            File.values()[kingFrom.file.ordinal() + (kingSide ? 1 : -1)],
            rank
        );
        board.movePiece(rookFrom, rookTo);
    }
    public Color getColorByLogin(String login) {
        if(whitePlayer.getPlayerLogin().equals(login)){
            return Color.WHITE;
        }
        if(blackPlayer.getPlayerLogin().equals(login)){
            return Color.BLACK;
        }
        return null;
    }
    private String colorToString(Color color) {
        return color == Color.WHITE ? "WHITE" : "BLACK";
    }

    public void broadcast(String msg){
        whitePlayer.sendMessage(msg); 
        blackPlayer.sendMessage(msg);
    }

    public String getAllLegalMovesFor(){
        StringBuilder moves = new StringBuilder();

        for( int rank =1; rank <=8; rank++){
            for( File file: File.values()){
                Coordinates fromCoords = new Coordinates(file, rank);
                Piece piece = board.getPiece(fromCoords);
                if(piece != null && piece.color==currentTurn){
                    moves.append(fromCoords);
                    moves.append(",");

                    Set<Coordinates> legalMoves = GameStateChecker.getLegalMoves(piece, fromCoords, board);
                    for (Coordinates coordinates : legalMoves) {
                        moves.append(coordinates);
                        moves.append(",");
                    }

                    moves.append(";");// "A1,A3,A5,;B2,C3,D2,;"
                }
            }
        }


        return moves.toString();
    }


    // for drawing the board rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR

    public String boardToFEN(){
        StringBuilder fen = new StringBuilder();

        for( int rank =8; rank >=1; rank --){
            int empty =0;
            for( File file: File.values()){
                Piece piece = board.getPiece(new Coordinates(file, rank));
                if( piece!= null){
                    if( empty >0){
                        fen.append(empty); // dodanie liczby pustych pol
                        empty=0;
                    }
                    fen.append(pieceToFEN(piece));
                }
                else{
                    empty++;
                }
            }
            if(empty >0){
                fen.append(empty);
            }
            if(rank >1){
                fen.append("/");
            }
        }
        //czja kolejka
        fen.append(currentTurn == Color.WHITE ? " w " : " b ");

        //roszada
        StringBuilder castling = new StringBuilder();
        if(whiteKingsideCastle) {
            castling.append("K");
        }
        if(whiteQueensideCastle){
            castling.append("Q");
        }
        if(blackKingsideCastle){
            castling.append("k");
        }
        if(blackQueensideCastle){
            castling.append("q");
        }
        fen.append(castling.length() > 0 ? castling : "-");

        //dla en passant
        fen.append(" ");
        fen.append(enPassantTarget != null  ? CoordinatesParser.toString(enPassantTarget) : "-");

        fen.append(" ").append(halfMoveClock);
        fen.append(" ").append(fullMoveNumber);

        return fen.toString();
    }

    private char pieceToFEN(Piece piece){
        char symbol;

        if( piece instanceof Pawn){
            symbol='p';
        }
        else if( piece instanceof Rook){
            symbol='r';
        }
        else if( piece instanceof Bishop){
            symbol='b';
        }
        else if( piece instanceof Knight){
            symbol='n';
        }
        else if( piece instanceof King){
            symbol='k';
        }
        else if( piece instanceof Queen){
            symbol='q';
        }
        else {
            symbol = '?';
        }
        return piece.color == Color.WHITE ? Character.toUpperCase(symbol) : symbol;
        
    }
    public String applyMove(String from, String to, String playerLogin) {

        if(gameOver){
            return "ERROR|GAME_ALREADY_OVER";
        } 

        Color playerColor = getColorByLogin(playerLogin);
        
        if(playerColor == null){
            return "ERROR|UNKNOWN_PLAYER";
        }      
        if(playerColor != currentTurn){
            return "ERROR|NOT_YOUR_TURN";
        }

        Coordinates fromCoords = CoordinatesParser.parse(from);
        Coordinates toCoords   = CoordinatesParser.parse(to);

        if(fromCoords == null || toCoords == null){
            return "ERROR|INVALID_COORDINATES";
        }

        Piece piece = board.getPiece(fromCoords);

        if(piece == null) {
             return "ERROR|NO_PIECE_THERE";
        }            
        if(piece.color != playerColor){
            return "ERROR|NOT_YOUR_PIECE";
        }

        Set<Coordinates> legalMoves = GameStateChecker.getLegalMoves(piece, fromCoords, board);
        if(!legalMoves.contains(toCoords)){
            return "ERROR|ILLEGAL_MOVE";
        }

        checkTime();
        if(gameOver) return "ERROR|TIMEOUT";

        boolean isCapture = !board.isSquareEmpty(toCoords);

        // en passant sprawdzamy do rucha
        int fileDiff = Math.abs(toCoords.file.ordinal() - fromCoords.file.ordinal());
        boolean isEnPassant = piece instanceof Pawn && fileDiff == 1 && board.isSquareEmpty(toCoords);

        Piece capturedPiece = null;
        if (isEnPassant) {
            capturedPiece = board.getPiece(new Coordinates(toCoords.file, fromCoords.rank));
        } else if (isCapture) {
            capturedPiece = board.getPiece(toCoords);
        }

        //robimy ruch
        board.movePiece(fromCoords, toCoords);

        //usuwanie schwytanego pionka podczas bicia en passant
        if (isEnPassant) {
            Coordinates capturedPawn = new Coordinates(toCoords.file, fromCoords.rank);
            board.removePiece(capturedPawn);
        }

        //poruszanie wieżą podczas roszady
        if (piece instanceof King) {
            handleCastling(fromCoords, toCoords);
        }

        //aktualizacja enPassantTarget
        board.setEnPassantTarget(null);
        enPassantTarget = null;
        if (piece instanceof Pawn && Math.abs(toCoords.rank - fromCoords.rank) == 2) {
            int epRank = (fromCoords.rank + toCoords.rank)/2;
            Coordinates epTarget = new Coordinates(fromCoords.file, epRank);
            board.setEnPassantTarget(epTarget);
            enPassantTarget = epTarget;
        }

        //aktualizacja praw do roszady
        updateCastlingRights(fromCoords, piece);

        //промоция(автоматически в ферзя)
        Piece moved = board.getPiece(toCoords);
        if (GameStateChecker.shouldPromote(moved, toCoords)) {
            board.removePiece(toCoords);
            board.setPiece(toCoords, new Queen(playerColor, toCoords));
        }

        //for FEN
        if(piece instanceof Pawn || isCapture){
            halfMoveClock = 0;
        } else {
             halfMoveClock++;
        }

        
        String moveNotation;
        if (currentTurn == Color.WHITE) {
            moveNotation = fullMoveNumber + " ⚫ " + from.toLowerCase() + "-" + to.toLowerCase();
        } else {
            moveNotation = fullMoveNumber + " ⚪ " + from.toLowerCase() + "-" + to.toLowerCase();
        }

        if(currentTurn == Color.BLACK){
            fullMoveNumber++;
        }
        
        //zmienić kolejke
        Color opponent = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        currentTurn = opponent;
        this.lastTimeCheck = System.currentTimeMillis();

         //sprawdzanie stanu gry
        String fen = boardToFEN();

        if (this.gameId != null) {
            char movedSym = pieceToFEN(piece);
            boolean movedIsBlack = (piece.color == Color.BLACK);
            Integer movedPieceId = PiecesTable.getPieceId(movedSym, movedIsBlack);

            Integer capturedPieceId = null;
            if (capturedPiece != null) {
                char capSym = Character.toUpperCase(pieceToFEN(capturedPiece));
                boolean capIsBlack = (capturedPiece.color == Color.BLACK);
                capturedPieceId = PiecesTable.getPieceId(capSym, capIsBlack);
            }

            if (movedPieceId != null) {
                boolean isSaved = MovesTable.saveMove(this.gameId, from, to, movedPieceId, capturedPieceId);
                if (!isSaved) {
                    System.out.println("[BAZA DANYCH] Błąd SQL - nie udało się zapisać ruchu w tabeli moves!");
                }
            } else {
                System.out.println("[BAZA DANYCH] UWAGA: Nie znaleziono ID dla figury '" + movedSym + "' (Czarna: " + movedIsBlack + ") w tabeli pieces!");
            }
            GamesTable.updateGameFen(this.gameId, fen);
        }

        if(GameStateChecker.isCheckMate(opponent, board)) {
            gameOver = true;
            if (this.gameId != null) GamesTable.setGameOver(this.gameId, playerColor == Color.BLACK);
            return "GAME_OVER|" + colorToString(playerColor) + "|Szach-mat";
        }
        if(GameStateChecker.isCheckPate(opponent, board)) {
            gameOver = true;
            if (this.gameId != null) GamesTable.setGameOver(this.gameId, null);
            return "GAME_OVER|Remis|Pat";
        }
        if(GameStateChecker.isKingInCheck(opponent, board)) {
            return "MOVE_OK|CHECK|" + fen+ "|" + moveNotation;
        }

        return "MOVE_OK|" + fen+ "|" + moveNotation;
    }

    public boolean isGameOver(){ 
        return gameOver; 
    }
    public Color getCurrentTurn() { 
        return currentTurn; 
    }

    
    // public String getWhitePlayer(){ 
    //     return whitePlayer; 
    // }
    // public String getBlackPlayer(){ 
    //     return blackPlayer; 
    // }
    public String getInitialFEN() { 
        return boardToFEN(); 
    }

    public ClientHandler getOpponent(ClientHandler curr){
        if (whitePlayer == curr){
            return blackPlayer;
        }
        return whitePlayer;
    }

    public void checkTime() {
        if (gameOver || !isTimeLimited) return;

        long now = System.currentTimeMillis();
        int passedSeconds = (int) ((now - lastTimeCheck) / 1000);

        if (passedSeconds >= 1) {
            if (currentTurn == Color.WHITE) {
                whiteTimeLeft -= passedSeconds;
            } else {
                blackTimeLeft -= passedSeconds;
            }
            
            lastTimeCheck = now;

            if (whiteTimeLeft <= 0) {
                gameOver = true;
                if (this.gameId != null) GamesTable.setGameOver(this.gameId, true);
                broadcast("GAME_OVER|Czarny|Koniec czasu");
                endGameSession();
            } else if (blackTimeLeft <= 0) {
                gameOver = true;
                if (this.gameId != null) GamesTable.setGameOver(this.gameId, false);
                broadcast("GAME_OVER|Bialy|Koniec czasu");
                endGameSession();
            } else {
                broadcast("TIME_UPDATE|" + whiteTimeLeft + "|" + blackTimeLeft);
            }
        }
    }
    

    public void endGameSession() {
        this.gameOver = true;
        if (whitePlayer != null) whitePlayer.clearSession();
        if (blackPlayer != null) blackPlayer.clearSession();
    }

    public void playerDisconnected(ClientHandler disconnectedPlayer) {
        if (gameOver) return; 

        this.gameOver = true;
        ClientHandler winner = getOpponent(disconnectedPlayer);
        String winnerColor = (winner == whitePlayer) ? "Bialy" : "Czarny";

        if (winner != null) {
            if (this.gameId != null) {
                Boolean isBlackWinner = (winner == blackPlayer);
                GamesTable.setGameOver(this.gameId, isBlackWinner);
            }
            winner.sendMessage("GAME_OVER|" + winnerColor + "|Walkower - przeciwnik opuścił grę");
        }
        
        endGameSession();
    }

    public void pauseGame(String requesterLogin) {
        if (gameOver) return;

        this.gameOver = true; 

        broadcast("GAME_PAUSED|" + requesterLogin);
        
        endGameSession();
        
        //System.out.println("Gra została wstrzymana i zapisana przez: " + requesterLogin);
    }

}
