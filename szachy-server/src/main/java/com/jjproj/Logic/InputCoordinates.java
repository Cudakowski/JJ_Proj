package com.jjproj.Logic;
import java.util.Scanner;

import com.jjproj.Logic.piece.CoordinatesShift;
import com.jjproj.Logic.piece.Piece;
import java.util.Set;

public class InputCoordinates {

    private static final Scanner scanner = new Scanner(System.in);

    public static Coordinates input(){
        while(true){
            System.out.println("enter coordinates (A1)");
            String line = scanner.nextLine();

            if(line.length()!=2){
                System.out.println("invalid format");
                continue;

            }

            char fileChar = Character.toUpperCase(line.charAt(0));
            char rankChar = line.charAt(1);

            if(!Character.isLetter(fileChar)){
                System.out.println("invalid format");
                continue;
            }
            if(!Character.isDigit(rankChar)){
                System.out.println("invalid format");
                continue;
            }

            File file;
            try {
                file = File.valueOf(String.valueOf(fileChar));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid file! Please use letters A through H");
                continue;
            }
           
             int rank = Character.getNumericValue(rankChar);
            if (rank < 1 || rank > 8) {
                System.out.println("Invalid rank! Please use numbers 1 through 8");
                continue;
            }
            return new Coordinates(file, rank);

        }
    }

    //
    public static Coordinates inputPieceCoordinatesForColor(Color color, Board board){
        while(true){
            System.out.println("enter your move");
            Coordinates coordinates = input();
            if(board.isSquareEmpty(coordinates)){
                System.out.println("is empty Square");
                continue;
            }
            Piece piece = board.getPiece(coordinates);
            //zeby color of piece byl takii samy jak nasz 
            if(piece.color!=color){
                System.out.println("You can't move someone else's piece! Wrong color!!!!");
                continue;
            }
            ///czy piece moze isc
           Set<Coordinates> availableMoveSquares=piece.getAvailableMoveSquares(board);
           if(availableMoveSquares.size()==0){
            System.out.println("Blocked piece");
            continue;
           }

           return coordinates;

        }
    }

    public static Coordinates inputAvailableSquare(Set<Coordinates> coordinates){
        while(true){
            System.out.println("enter your move for selected piece");
            Coordinates input=input();
            if(!coordinates.contains(input)){
                System.out.println("not available square");
                continue;
            }
            return input;
        }
            
    }

    public static void main(String[] args){
        Coordinates coordinates = input();
        System.out.println("coordinates"+coordinates);
    }

}
