package com.jjproj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPConnection implements AutoCloseable {
    
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public TCPConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public void sendString(String str) {
        if (socket != null && !socket.isClosed()) {
            writer.println(str);
        }
    }

    public String awaitString() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Błąd podczas zamykania połączenia TCP: " + e.getMessage());
        }
    }
}
