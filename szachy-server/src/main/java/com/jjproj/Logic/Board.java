package com.jjproj.Logic;

import java.util.Collections;
import java.util.HashMap;

import com.jjproj.Logic.piece.Bishop;
import com.jjproj.Logic.piece.King;
import com.jjproj.Logic.piece.Knight;
import com.jjproj.Logic.piece.Pawn;
import com.jjproj.Logic.piece.Piece;
import com.jjproj.Logic.piece.Queen;
import com.jjproj.Logic.piece.Rook;

public class Board {
    private HashMap<Coordinates, Piece> pieces = new HashMap<>();

    // public HashMap<Coordinates, Piece> getPieces() {
    //     return pieces;
    // }

    public Piece getPiece(Coordinates coordinates){
        return pieces.get(coordinates);
    }


    public void setPiece(Coordinates coordinates, Piece piece ){
        if(piece == null) {
            throw new IllegalArgumentException("Piece cannot be null");
        }
        piece.coordinates=coordinates;
        pieces.put(coordinates,piece);
    }

    public void removePiece(Coordinates coordinates){
        pieces.remove(coordinates);
    }

    public void movePiece(Coordinates from, Coordinates to){
        Piece piece=getPiece(from);
        if(piece!=null){
            removePiece(from);
            setPiece(to, piece);
        }
    }

    public boolean isSquareEmpty(Coordinates coordinates){
            return !pieces.containsKey(coordinates); // jerzeli key jest- piece jest
    }
    public void setupDefaultPiecesPositions() {
        setupPawns();
        setupRooks();
        setupBishops();
        setupKnights();
        setupQueens();
        setupKings();
    }
    private void setupPawns() {
        for (File file : File.values()) {
            setPiece(new Coordinates(file, 2), new Pawn(Color.WHITE, new Coordinates(file, 2)));
            setPiece(new Coordinates(file, 7), new Pawn(Color.BLACK, new Coordinates(file, 7)));
        }
    }

    private void setupRooks() {
        setPiece(new Coordinates(File.A, 1), new com.jjproj.Logic.piece.Rook(Color.WHITE, new Coordinates(File.A, 1)));
        setPiece(new Coordinates(File.H, 1), new com.jjproj.Logic.piece.Rook(Color.WHITE, new Coordinates(File.H, 1)));
        setPiece(new Coordinates(File.A, 8), new com.jjproj.Logic.piece.Rook(Color.BLACK, new Coordinates(File.A, 8)));
        setPiece(new Coordinates(File.H, 8), new com.jjproj.Logic.piece.Rook(Color.BLACK, new Coordinates(File.H, 8)));
    }

    private void setupBishops() {
        setPiece(new Coordinates(File.C, 1), new Bishop(Color.WHITE, new Coordinates(File.C, 1)));
        setPiece(new Coordinates(File.F, 1), new Bishop(Color.WHITE, new Coordinates(File.F, 1)));
        setPiece(new Coordinates(File.C, 8), new Bishop(Color.BLACK, new Coordinates(File.C, 8)));
        setPiece(new Coordinates(File.F, 8), new Bishop(Color.BLACK, new Coordinates(File.F, 8)));
    }  
    private void setupKnights() {
        setPiece(new Coordinates(File.B, 1), new Knight(Color.WHITE, new Coordinates(File.B, 1)));
        setPiece(new Coordinates(File.G, 1), new Knight(Color.WHITE, new Coordinates(File.G, 1)));
        setPiece(new Coordinates(File.B, 8), new Knight(Color.BLACK, new Coordinates(File.B, 8)));
        setPiece(new Coordinates(File.G, 8), new Knight(Color.BLACK, new Coordinates(File.G, 8)));
    }

    private void setupQueens() {
        setPiece(new Coordinates(File.D, 1), new Queen(Color.WHITE, new Coordinates(File.D, 1)));
        setPiece(new Coordinates(File.D, 8), new Queen(Color.BLACK, new Coordinates(File.D, 8)));
    }

    private void setupKings() {
        setPiece(new Coordinates(File.E, 1), new King(Color.WHITE, new Coordinates(File.E, 1)));
        setPiece(new Coordinates(File.E, 8), new King(Color.BLACK, new Coordinates(File.E, 8)));
    }  
    
}
