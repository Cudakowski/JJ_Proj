package com.jjproj.Logic.piece;

import com.jjproj.Logic.Color;
import com.jjproj.Logic.Coordinates;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CoordinatesShift {

    public final int fileShift;
    public final int rankShift;

    public CoordinatesShift(int fileShift, int rankShift){
        this.fileShift=fileShift;
        this.rankShift=rankShift;
    }
    
}
// java records: public record CoorditatesShift(int fileShift, int rankShift){}