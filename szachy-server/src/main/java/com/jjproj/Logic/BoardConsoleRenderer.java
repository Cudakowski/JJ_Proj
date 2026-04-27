package com.jjproj.Logic;

import com.jjproj.Logic.piece.Piece;
import com.jjproj.Logic.piece.Pawn;
import com.jjproj.Logic.piece.Rook;
import com.jjproj.Logic.piece.Knight;
import com.jjproj.Logic.piece.Bishop;
import com.jjproj.Logic.piece.Queen;
import com.jjproj.Logic.piece.King;

public class BoardConsoleRenderer {

    public void render(Board board){
        System.out.println("\n  A B C D E F G H");
        for( int rank = 8; rank >=1 ; rank--){
            System.out.print(rank + " ");
            for( File file : File.values()){
               Coordinates coords = new Coordinates(file, rank);
                Piece piece = board.getPiece(coords);
                System.out.print(getPieceSymbol(piece) + " ");
            }
            System.out.println(rank);
        }
         System.out.println("  A B C D E F G H\n");
    }
    private String getPieceSymbol(Piece piece) {
        if(piece == null) return "·";
        if(piece instanceof Pawn)   return piece.color == Color.WHITE ? "♙" : "♟";
        if(piece instanceof Rook)   return piece.color == Color.WHITE ? "♖" : "♜";
        if(piece instanceof Knight) return piece.color == Color.WHITE ? "♘" : "♞";
        if(piece instanceof Bishop) return piece.color == Color.WHITE ? "♗" : "♝";
        if(piece instanceof Queen)  return piece.color == Color.WHITE ? "♕" : "♛";
        if(piece instanceof King)   return piece.color == Color.WHITE ? "♔" : "♚";
        return "?";
    }
}
