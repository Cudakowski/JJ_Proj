package com.jjproj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class TCPConnection {
    protected Socket socket;
    protected PrintWriter writer;
    protected BufferedReader reader;

    public void sendString(String str){
        System.out.println("sending");
        writer.println(str);
    }

    public String awaitString(){
        System.out.println("awaiting");
        try {
            return reader.readLine();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
