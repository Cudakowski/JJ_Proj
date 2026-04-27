package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

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
        
        return moves;
    }
}
