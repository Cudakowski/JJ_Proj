package com.jjproj.Logic;

import java.util.Objects;

import com.jjproj.Logic.piece.CoordinatesShift;

public class Coordinates {
    public final File file; // columns
    public final int rank; //rows

    public Coordinates(File file, int rank){
        this.file=file;
        this.rank=rank;
    }

    // public Coordinates shift(CoordinatesShift shift){
    //     return new Coordinates(File.values()[this.file.ordinal()+shift.fileShift],  this.rank+shift.rankShift);
    // }


    public Coordinates shift(CoordinatesShift shift) {
        int newFileIndex = this.file.ordinal() + shift.fileShift;
        int newRank = this.rank + shift.rankShift;
        //sprawdzenie granic 
        if (newFileIndex < 0 || newFileIndex >= 8) return null;
        if (newRank < 1 || newRank > 8) return null;
        
        return new Coordinates(File.values()[newFileIndex], newRank);
    }



    @Override 
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(obj==null || getClass() != obj.getClass()){
            return false;
        }
        Coordinates anotherCoordinates = (Coordinates) obj;
        if(file != anotherCoordinates.file){
            return false;
        }
        //return rank.equals(anotherCoordinates.rank);
        return file == anotherCoordinates.file && rank == anotherCoordinates.rank;
    }


    @Override
    public int hashCode() {
        return Objects.hash(file, rank); 
    }



}
