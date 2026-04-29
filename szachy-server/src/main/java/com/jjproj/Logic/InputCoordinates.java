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
            String line = scanner.nextLine().trim().toUpperCase();

            if(line.length()!=2){
                System.out.println("invalid format");
                continue;

            }

            char fileChar = line.charAt(0);
            char rankChar = line.charAt(1);

            if(fileChar < 'A' || fileChar > 'H' || rankChar < '1' || rankChar > '8') {
                System.out.println("Invalid coordinates! File must be A-H and rank must be 1-8.");
                continue;
            }

            int fileIndex = fileChar - 'A';
            File file = File.values()[fileIndex];
            int rank = Character.getNumericValue(rankChar);

            return new Coordinates(file, rank);

        }
    }


    public static Coordinates inputPieceCoordinatesForColor(Color color, Board board) {
    while(true){
        System.out.println((color == Color.WHITE ? "BIAŁE" : "CZARNE") + " - wybierz figurę (np. e2):");
        Coordinates coordinates = input();
        
        if(board.isSquareEmpty(coordinates)){
            System.out.println("To pole jest puste!");
            continue;
        }
        
        Piece piece = board.getPiece(coordinates);
        if(piece.color != color){
            System.out.println("To nie Twoja figura!");
            continue;
        }
        Set<Coordinates> availableMoveSquares = piece.getAvailableMoveSquares(board);
        if(availableMoveSquares.isEmpty()){
            System.out.println("Ta figura nie ma dostępnych ruchów!");
            System.out.print("Dostępne ruchy: ");
            for (Coordinates c : availableMoveSquares) {
                System.out.print(c.file + "" + c.rank + " ");
            }
            System.out.println();
            continue;
        }
        
        return coordinates;
    }
}

    public static Coordinates inputAvailableSquare(Set<Coordinates> availableSquares) {
    while(true) {
        System.out.println("Wybierz docelowe pole (np. e4):");
        System.out.print("Dostępne ruchy: ");
        for (Coordinates c : availableSquares) {
            System.out.print(c.file + "" + c.rank + " ");
        }
        System.out.println();
        
        Coordinates input = input();
        
        if (!availableSquares.contains(input)) {
            System.out.println("Nieprawidłowy ruch!");
            continue;
        }
        return input;
        }
    }
}
