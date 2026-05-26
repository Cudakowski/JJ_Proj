package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.jjproj.Logic.Board;
import java.util.Collections;
import com.jjproj.Logic.File;

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

                if(newCoordinates != null) { // czy nie wychodzi poza planszę
                    if(isSquareAvailableForMove(newCoordinates, board)){
                        result.add(newCoordinates);
                    }
                }
            }

        }
        return result;
    }

    protected boolean isSquareAvailableForMove(Coordinates coordinates, Board board) {
       
        if(!board.isSquareEmpty(coordinates) && 
            board.getPiece(coordinates) != null && 
            board.getPiece(coordinates).color == this.color){
                return false;  // własna figura – nie można
        }
        
        if(canJumpOverPieces()) {
            return true;
        }
        //dla innych figur sprawdzany czy sciezka jest pusta
        return isPathClear(coordinates, board);
    }
    

    protected boolean isPathClear(Coordinates target, Board board) {

        int fileStep = Integer.compare(target.file.ordinal(),this.coordinates.file.ordinal());
        int rankStep = Integer.compare(target.rank,this.coordinates.rank);

        // move to target coordinate
        int currentFileIndex = this.coordinates.file.ordinal();
        int currentRank = this.coordinates.rank;

        while(true) {

            currentFileIndex += fileStep;
            currentRank += rankStep;

            // sprawdzenie czy nie wyszliśmy poza zakres
            if(currentFileIndex < 0 || currentFileIndex >= 8 || currentRank < 1 || currentRank > 8) {
                return false;
            }

            Coordinates current = new Coordinates(File.values()[currentFileIndex],currentRank);

            if(current.equals(target)) {
                return true;
            }
            if(!board.isSquareEmpty(current)) {
                return false;
            }
        }
    }
    protected boolean canJumpOverPieces() {
        return false;
    }   

    protected Set<CoordinatesShift> getPieceMoves(){
        //return null;
        return Collections.emptySet(); 
    }
}
