package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

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
}
