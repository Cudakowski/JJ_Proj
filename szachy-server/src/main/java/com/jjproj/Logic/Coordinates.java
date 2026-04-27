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
        if(!(obj instanceof Coordinates)){
            return false;
        }
        Coordinates other = (Coordinates) obj;
        if(file != other.file){
            return false;
        }
        //return rank.equals(other.rank);
        return file == other.file && rank == other.rank;
    }


    @Override
    public int hashCode() {
        return Objects.hash(file, rank); 
    }

    @Override
    public String toString() {
        return file.name() + rank;
    }

}
