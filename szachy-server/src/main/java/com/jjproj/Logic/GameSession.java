package com.jjproj.Logic;

import com.jjproj.Logic.piece.*;
import java.util.Set;
import com.jjproj.Logic.Coordinates;
import com.jjproj.ClientHandler;
import com.jjproj.Logic.Board;
import com.jjproj.Logic.GameStateChecker;

public class GameSession {
    
    private final Board board;
    private Color currentTurn;
    private boolean gameOver;

    private final ClientHandler whitePlayer;
    private final ClientHandler blackPlayer;

    private int halfMoveClock = 0;// liczba półruchów bez bicia
    private int fullMoveNumber = 1;//numer pełnego ruchu

    //dla roszady
    private boolean whiteKingsideCastle  = true;// biała krótka (K)
    private boolean whiteQueensideCastle = true; // biała długa (Q)
    private boolean blackKingsideCastle  = true; // czarna krótka (k)
    private boolean blackQueensideCastle = true; // czarna długa (q)

    //dla en passant
    private Coordinates enPassantTarget = null;//Pole bicia w przelocie

    public GameSession(ClientHandler whitePlayer, ClientHandler blackPlayer) {
        this.board = new Board();
        this.board.setupDefaultPiecesPositions();
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public void startGame() {
        String startingFEN = boardToFEN();
        
        whitePlayer.sendMessage("BOARD_UPDATE|" + startingFEN);
        blackPlayer.sendMessage("BOARD_UPDATE|" + startingFEN);
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
    private Color getColorByLogin(String login) {
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

    public void gameOver(){
        whitePlayer.
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

        boolean isCapture = !board.isSquareEmpty(toCoords);

        // en passant sprawdzamy do rucha
        int fileDiff = Math.abs(toCoords.file.ordinal() - fromCoords.file.ordinal());
        boolean isEnPassant = piece instanceof Pawn && fileDiff == 1 && board.isSquareEmpty(toCoords);

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
        if(currentTurn == Color.BLACK){
            fullMoveNumber++;
        }

        //zmienić kolejkę
        Color opponent = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        currentTurn = opponent;

         //sprawdzanie stanu gry
        String fen = boardToFEN();
        if(GameStateChecker.isCheckMate(opponent, board)) {
            gameOver = true;
            return "GAME_OVER|" + colorToString(playerColor) + "|Szach-mat";
        }
        if(GameStateChecker.isCheckPate(opponent, board)) {
            gameOver = true;
            return "GAME_OVER|Remis|Pat";
        }
        if(GameStateChecker.isKingInCheck(opponent, board)) {
            return "MOVE_OK|CHECK|" + fen;
        }

        return "MOVE_OK|" + fen;
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

}
