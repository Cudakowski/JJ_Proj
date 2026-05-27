package com.jjproj.Logic;

import com.jjproj.Logic.piece.*;
import java.util.Set;

import java.util.HashSet;
public class GameStateChecker {

    

    //sprawdzamy czy król danego koloru jest szachowany
    public static boolean isKingInCheck(Color color, Board board){
        Coordinates kingCoordinates = findKing(color, board);

        if( kingCoordinates == null){
            return false;
        }

        Color opponentColor = (color ==Color.WHITE) ? Color.BLACK : Color.WHITE;
        
        // idzemy po wszystkim figuram przeciwnika
        for( int rank =1; rank <=8; rank++){
            for( File file : File.values()){
                Coordinates coordinates = new Coordinates(file, rank);
                Piece piece = board.getPiece(coordinates);

                if(piece !=null && piece.color == opponentColor){
                    if (piece instanceof King) {
                        int fileDiff = Math.abs(coordinates.file.ordinal() - kingCoordinates.file.ordinal());
                        int rankDiff = Math.abs(coordinates.rank - kingCoordinates.rank);
                        
                        if (fileDiff <= 1 && rankDiff <= 1) {
                            return true; 
                        }
                        continue;
                    }



                    Set<Coordinates> moves = piece.getAvailableMoveSquares(board);

                    if(moves.contains(kingCoordinates)){
                        return true; //król jest atakowany
                    }
                }
            }
        }
        return false;
    }

    //szykamy współrzędne króla o pożądanym kolorze
    private static Coordinates findKing(Color color, Board board){
        for( int rank =1; rank <=8; rank++){
            for( File file: File.values()){
                Coordinates coordinates = new Coordinates(file, rank);
                Piece piece = board.getPiece(coordinates);
                
                if( piece instanceof King && piece.color == color){
                    return coordinates;
                }
            }
            
        }
        return null;
    }

    public static boolean hasLegalMoves(Color color, Board board){

        for( int rank =1; rank <=8; rank++){
            for( File file : File.values()){

                Coordinates coordinates = new Coordinates(file,rank);
                Piece piece = board.getPiece(coordinates);

                if( piece != null && piece.color == color ){
                    Set<Coordinates> legalMoves = getLegalMoves(piece, coordinates, board);

                    if(!legalMoves.isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static Set<Coordinates> getLegalMoves(Piece piece, Coordinates from, Board board){
        Set<Coordinates> legalMoves = new HashSet<>();
        Set<Coordinates> candidatesToMove = piece.getAvailableMoveSquares(board);

        for(Coordinates to : candidatesToMove){
            // symulujemy ruch na kopii planszy
            Board copy = board.copy();
            copy.movePiece(from, to);

            //jeśli po ruchu nasz król nie jest szachowany->ruch jest legalny
            if(!isKingInCheck(piece.color, copy)){
                legalMoves.add(to);
            }

        }
        return legalMoves;
    }

    //mat. król jest szachowany i nie ma żadnych legalnych ruchów(
    public static boolean isCheckMate(Color color, Board board){
        return isKingInCheck(color, board) && !hasLegalMoves(color, board);
    }

    //pat. król nie jest szachowany, ale nie ma żadnych legalnych ruchów(
    public static boolean isCheckPate( Color color, Board board){
        return !isKingInCheck(color, board) && !hasLegalMoves(color, board);
    } 

    //sprawdzamy czy pionek powinien się zmienic na inną figurę
    public static boolean shouldPromote(Piece piece, Coordinates coordinates){
        if(!( piece instanceof Pawn)){
            return false;
        }

        return (piece.color== Color.WHITE && coordinates.rank == 8) ||
            (piece.color == Color.BLACK && coordinates.rank == 1);
    }

    //zamiana pionka na wybraną figurę
    public static void promotePawn(Coordinates coordinates, Piece newPiece, Board board){
        board.removePiece(coordinates);
        board.setPiece(coordinates, newPiece);
    }

    
}
