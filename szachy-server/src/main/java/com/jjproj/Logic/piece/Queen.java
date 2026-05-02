package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Queen extends Piece {
    public Queen(Color color, Coordinates coordinates){
        super(color, coordinates);
    }
    @Override
    protected Set<CoordinatesShift> getPieceMoves() {
        Set<CoordinatesShift> moves = new HashSet<>();
        
        //po liniach prostych
        for(int i = 1; i <= 7; i++) {
            moves.add(new CoordinatesShift(i, 0));   // prawo
            moves.add(new CoordinatesShift(-i, 0));  // lewo
            moves.add(new CoordinatesShift(0, i));   // góra
            moves.add(new CoordinatesShift(0, -i));  // dół
        }
        
        // po skosach
        for(int i = 1; i <= 7; i++) {
            moves.add(new CoordinatesShift(i, i));    // prawo-góra
            moves.add(new CoordinatesShift(i, -i));   // prawo-dół
            moves.add(new CoordinatesShift(-i, i));   // lewo-góra
            moves.add(new CoordinatesShift(-i, -i));  // lewo-dół
        }
        
        return moves;
    }
    // @Override
    // protected boolean isSquareAvailableForMove(Coordinates coordinates, Board board) {
    //     if (!super.isSquareAvailableForMove(coordinates, board)) {
    //         return false;
    //     }
    //     return isPathClear(coordinates, board);
    // }
}
