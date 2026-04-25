package com.jjproj.Logic.piece;

import com.jjproj.Logic.Board;
import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;
import java.util.HashSet;
import java.util.Set;


abstract public class Piece {

    public final Color color; // nie mozemy zmieniac
    public Coordinates coordinates; //mozemy zmieniac

    public Piece(Color color, Coordinates coordinates){
        this.color=color;
        this.coordinates=coordinates;
    }
    //для рассчета координат нужно пройтись по каждому сдвигу(((
    public Set<Coordinates> getAvailableMoveSquares(Board board){
        Set<Coordinates> result = new HashSet<>();
        for( CoordinatesShift shift: getPieceMoves()){
            if(coordinates.shift(shift)!=null){
                Coordinates newCoordinates = coordinates.shift(shift);

                if(isSquareAvailableForMove(newCoordinates, board)){
                    result.add(newCoordinates);
                }
            }

        }
        return result;
    }

    // private boolean isSquereAvailableForMove(Coordinates coordinates, Board board){
    //     return board.isSquareEmpty(coordinates)|| board.getPiece(coordinates) != color;

    // }

    // SprawdZenie czy pole puste LUB czy na polu jest figura przeciwnika
    private boolean isSquareAvailableForMove(Coordinates coordinates, Board board) {
    return board.isSquareEmpty(coordinates) || 
           (board.getPiece(coordinates) != null && 
            board.getPiece(coordinates).color != this.color); 
}
    //сдвиг координат
    //фигуры не могут сбивать фигуры своего цвета.
    //фигуры не могут уходить за границу доски
    //мы можем рвссчитать сдвиги и проверить корректна ли клетка для данной фигуры
    protected abstract Set<CoordinatesShift> getPieceMoves(){

    }
}
