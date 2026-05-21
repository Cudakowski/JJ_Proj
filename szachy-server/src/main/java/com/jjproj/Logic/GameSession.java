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

        // trzeba wprowadzić pełną notację 
        fen.append(" ");
        fen.append(this.currentTurn == Color.WHITE ? "w" : "b");
        // TODO: tutaj możliwości roszady, ruch enpassant, półruchy, ruchy
        fen.append(" KQkq - 0 0");


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

}
