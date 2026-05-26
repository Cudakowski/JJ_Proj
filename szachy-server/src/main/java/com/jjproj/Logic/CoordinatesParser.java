package com.jjproj.Logic;


public class CoordinatesParser {
    public static Coordinates parse(String input){
        if(input==null|| input.length()!=2){
            return null;
        }
        input = input.trim().toUpperCase();
        char fileChar = input.charAt(0);
        char rankChar = input.charAt(1);

        if(fileChar <'A' || fileChar > 'H'){
            return null;
        }
        if(rankChar < '1' || rankChar >'8'){
            return null;
        }

        File file = File.values()[fileChar - 'A'];
        int rank = Character.getNumericValue(rankChar);

        return new Coordinates(file, rank);

    }

    public static String toString(Coordinates coordinates){
        return coordinates.file.name() + coordinates.rank;
    }
    
}
