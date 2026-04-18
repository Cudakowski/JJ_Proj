package com.jjproj.Logic;

import java.util.HashMap;

import com.jjproj.Logic.piece.Pawn;
import com.jjproj.Logic.piece.Piece;

public class Board {
    HashMap<Coordinates, Piece> pieces = new HashMap<>();

    public void setPiece(Coordinates coordinates, Piece piece ){
        piece.coordinates=coordinates;
        pieces.put(coordinates,piece);
    }

    public void setupDefaultPiecesPositions(){
        //place the pawns
        for(File file : File.values()){
            setPiece(new Coordinates(file, 2), new Pawn(Color.WHITE, new Coordinates(file, 2)));
            setPiece(new Coordinates(file, 7), new Pawn(Color.BLACK, new Coordinates(file, 7)));
        }

    }
}
