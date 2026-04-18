package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

abstract public class Piece {

    public final Color color; // nie mozemy zmieniac
    public Coordinates coordinates; //mozemy zmieniac

    public Piece(Color color, Coordinates coordinates){
        this.color=color;
        this.coordinates=coordinates;
    }
}
