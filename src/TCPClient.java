import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient extends TCPConnection{
    
    public TCPClient() throws IOException {
        System.out.println("Client started");
        socket = new Socket("localhost",5000);
        writer = new PrintWriter(socket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}
