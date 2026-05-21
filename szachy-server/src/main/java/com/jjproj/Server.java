package com.jjproj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class Server {
    public static ConcurrentHashMap<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public static ScheduledExecutorService clock = Executors.newScheduledThreadPool(1);

    public static volatile boolean isRunning = true;

    public static void startServer() {
        int port = 5000;

        // sprzątenie po połączeniu z bazą dancyh i po zegarze
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            
            if (clock != null && !clock.isShutdown()) {
                clock.shutdownNow();
            }
            
            AbandonedConnectionCleanupThread.checkedShutdown();
        }));

        // thread zegara
        startClock();

        // thread konsoli
        startConsole();

        // thread nasłuchujący i tworzenie threadów połączeń
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serwer wystartowal na porcie " + port);
            System.out.println("Oczekiwanie na polaczenia");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Połączono z: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                
                new Thread(handler).start();
            }

        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Błąd głównego serwera: " + e.getMessage());
            }
        }
    }

    private static void startClock() {
        Runnable statusCheck = () -> {
            try {
                long currentTIme = System.currentTimeMillis();

                // PING
                for (ClientHandler client : onlineUsers.values()) {
                    
                    long timeSinceLastSignal = currentTIme - client.getlastPingTime();
                    
                    if (timeSinceLastSignal > 10000) {
                        client.pingAbsenceDisconnection();
                    }
                }

                // TODO: Czy komuś skończył się czas w partii (wysłanie GAME_OVER)
                
            } catch (Exception e) {
                System.err.println("Błąd w wątku zegarowym: " + e.getMessage());
            }
        };

        clock.scheduleAtFixedRate(statusCheck, 5, 5, TimeUnit.SECONDS);
    }

    private static void startConsole() {
        Thread consoleThread = new Thread(() -> {

            java.util.Scanner scanner = new java.util.Scanner(System.in);

            while (isRunning) {
                String cmd = scanner.nextLine();

                if (cmd.equalsIgnoreCase("stop")) {
                    isRunning = false;
                    System.out.println("Zatrzymywanie serwera");

                    System.exit(0); // wywoła Shutdown Hook

                } else if (cmd.equalsIgnoreCase("users")) {
                    System.out.println("Zalogowani gracze: " + onlineUsers.keySet());
                }
            }

            scanner.close();

        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }
}
