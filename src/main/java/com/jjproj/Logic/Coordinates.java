package com.jjproj.Logic;

import java.util.Objects;

public class Coordinates {
    public final File file; // columns
    public final Integer rank; //rows

    public Coordinates(File file, Integer rank){
        this.file=file;
        this.rank=rank;
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
        return rank.equals(anotherCoordinates.rank);
    }

    // @Override
    // public int hashCode(){
    //     int result=file.hashCode();
    //     result = 31*result + rank.hashCode();
    //     return result;
    // }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank); 
    }
}
