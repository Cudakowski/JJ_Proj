package com.jjproj.Logic.piece;

import com.jjproj.Logic.Board;
import com.jjproj.Logic.Color;
import com.jjproj.Logic.File;
import com.jjproj.Logic.Coordinates;
import com.jjproj.Logic.GameStateChecker;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class King extends Piece{
    public King(Color color, Coordinates coordinates){
        super(color, coordinates);
    }
     @Override
    protected Set<CoordinatesShift> getPieceMoves() {
        Set<CoordinatesShift> moves = new HashSet<>();
        
        moves.add(new CoordinatesShift(1, 0));   // prawo
        moves.add(new CoordinatesShift(-1, 0));  // lewo
        moves.add(new CoordinatesShift(0, 1));   // góra
        moves.add(new CoordinatesShift(0, -1));  // dół
        moves.add(new CoordinatesShift(1, 1));   // prawo-góra
        moves.add(new CoordinatesShift(1, -1));  // prawo-dół
        moves.add(new CoordinatesShift(-1, 1));  // lewo-góra
        moves.add(new CoordinatesShift(-1, -1)); // lewo-dół
        
        moves.add(new CoordinatesShift(2, 0)); //krótka roszada (w prawo o 2)
        moves.add(new CoordinatesShift(-2, 0)); //długa roszada (w lewo o 2)
        return moves;
    }
    @Override
    protected boolean isSquareAvailableForMove(Coordinates target, Board board) {
        int fileDiff = target.file.ordinal() - this.coordinates.file.ordinal();

        // обычный ход
        if(Math.abs(fileDiff) != 2) {
            return super.isSquareAvailableForMove(target, board);
        }

        // roszada
        boolean kingSide = fileDiff > 0;

        // проверяем право на рокировку
        if(!board.hasCastlingRight(this.color, kingSide)) {
            return false;
        }

        // проверяем что путь между королём и ладьёй свободен
        int direction = kingSide ? 1 : -1;
        int steps = kingSide ? 2 : 3;
        for(int i = 1; i <= steps; i++) {
            int newFileIndex = this.coordinates.file.ordinal() + direction * i;
            Coordinates between = new Coordinates(File.values()[newFileIndex],this.coordinates.rank);
            if(!board.isSquareEmpty(between)){
                return false;
            } 
        }

        // проверяем что король не проходит через шах
        // проверяем исходную клетку, промежуточную и конечную
        for(int i = 0; i <= 2; i++) {
            int newFileIndex = this.coordinates.file.ordinal() + direction * i;
            Coordinates check = new Coordinates(File.values()[newFileIndex],this.coordinates.rank);
            Board copy = board.copy();
            copy.removePiece(this.coordinates);
            copy.setPiece(check, new King(this.color, check));
            if (GameStateChecker.isKingInCheck(this.color, copy)) {
                return false;
            }
        }

        return true;
    }
}
