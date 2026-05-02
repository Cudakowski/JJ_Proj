package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Knight extends Piece {
    public Knight(Color color, Coordinates coordinates){
        super(color, coordinates);
    }

    @Override
    protected  Set<CoordinatesShift> getPieceMoves(){
        return new HashSet<>(Arrays.asList(
            //JEZELII PO CENTRU
            new CoordinatesShift(1,2),
            new CoordinatesShift(2,1),

            new CoordinatesShift(2,-1),
            new CoordinatesShift(1,-2),

            new CoordinatesShift(-2,-1),
            new CoordinatesShift(-1,-2),
            
            new CoordinatesShift(-2,1),
            new CoordinatesShift(-1,2)

        ));
    }
    @Override
    protected boolean canJumpOverPieces() {
        return true;
    }

}
