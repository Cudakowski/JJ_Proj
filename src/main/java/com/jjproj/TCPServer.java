package com.jjproj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;


public class TCPServer extends TCPConnection {
    private ServerSocket serverSocket;

    public TCPServer() throws IOException {
        System.out.println("Waiting for clients");
        serverSocket = new ServerSocket(5000);
        socket = serverSocket.accept();
        System.out.println("Connection established");
        writer = new PrintWriter(socket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

}
