package com.jjproj.Logic;

public class BoardConsoleRenderer {

    public void render(Board board){
        
        for( int rank = 8; rank >=1 ; rank--){
            for( File file : File.values()){
                System.out.println(file+String.valueOf(rank));
            }
        }
    }
}
