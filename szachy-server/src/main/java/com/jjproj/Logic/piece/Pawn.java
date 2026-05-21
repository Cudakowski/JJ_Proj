package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;
import com.jjproj.Logic.Board;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    public Pawn(Color color, Coordinates coordinates){
        super(color, coordinates);
    }
    @Override
    protected Set<CoordinatesShift> getPieceMoves() {
        Set<CoordinatesShift> moves = new HashSet<>();
        
        int direction = (color == Color.WHITE) ? 1 : -1;
        int startRank = (color == Color.WHITE) ? 2 : 7;
        
        //ruch o 1 pole do przodu
        moves.add(new CoordinatesShift(0, direction));
        
        //ruch o 2 pola do przodu(tylko z pozycji startowej)
        if(coordinates.rank == startRank) {
            moves.add(new CoordinatesShift(0, 2 * direction));
        }
        
        //bicie po skosie(lewo i prawo)
        moves.add(new CoordinatesShift(-1, direction));
        moves.add(new CoordinatesShift(1, direction));
        
        return moves;
    }
   @Override
    protected boolean isSquareAvailableForMove(Coordinates target, Board board) {
        int fileDiff = Math.abs(target.file.ordinal() - this.coordinates.file.ordinal());
        int rankDiff = target.rank - this.coordinates.rank;
        int direction = (color == Color.WHITE) ? 1 : -1;

        // Движение вперёд
        if(fileDiff == 0) {
            if(!board.isSquareEmpty(target)){
                return false;
            }
            if(Math.abs(rankDiff) == 2) {
                Coordinates middle = new Coordinates(this.coordinates.file,this.coordinates.rank + direction);
                return board.isSquareEmpty(middle);
            }
            return Math.abs(rankDiff) == 1;
        }

        // Взятие по диагонали
        if (fileDiff == 1 && rankDiff == direction) {
            Piece targetPiece = board.getPiece(target);
            return targetPiece != null && targetPiece.color != this.color;
        }

        return false;
    }
}
