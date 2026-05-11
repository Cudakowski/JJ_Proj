package com.jjproj;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class NetworkManager {
    
    private static TCPConnection connection;
    private static Thread listenerThread;
    private static ScheduledExecutorService clock;
    private static long sendTime = 0;
    private static AtomicBoolean isLoggingOrLogged = new AtomicBoolean(false);

    public static boolean connect(String host, int port) {
        try {
            connection = new TCPConnection(host, port);
            System.out.println("Polaczono z serwerem");
            
            startListening();
            return true;
        } catch (IOException e) {
            System.err.println("Nie udalo sie polaczyc: " + e.getMessage());
            return false;
        }
    }

    public static void login(String wpisanyLogin,String wpisaneHaslo) {

        if(!isLoggingOrLogged.compareAndSet(false,true)){
            System.out.println("Blokada podwojnego logowania");
            return;
        }

        Thread bridgeThread = new Thread(() -> {
            boolean isConnected = NetworkManager.connect("localhost", 5000);
            
            
            if (isConnected) {
                NetworkManager.sendCommand("LOGIN|" + wpisanyLogin + "|" + wpisaneHaslo);

                SceneManager.setStatus("Weryfikacja danych");
                
            } else {

                SceneManager.setStatus("Brak połączenia z serwerem");

                isLoggingOrLogged.set(false);
            }
            
        });
        
        bridgeThread.setDaemon(true);
        bridgeThread.start();
    }



    public static void sendCommand(String command) {
        if (connection != null) {
            connection.sendString(command);
        }
    }

    public static void disconnect() {
        
        isLoggingOrLogged.set(false);
        if (connection != null) {
            connection.close();
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (clock != null && !clock.isShutdown()) {
            clock.shutdownNow(); 
        }
    }

    private static void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String message;
                
                while ((message = connection.awaitString()) != null) {
                    processMessage(message);
                }
            } catch (IOException e) {

                System.out.println("Rozlaczono z serwerem.");
                disconnect();

                
                //SceneManager.setStatus("Utracono połączenie");
                
            }
        });
        
        // Wątek zamykany automatycznie przy wyłączeniu gry
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    // TODO :
    // mozna by tu wprowadzić liczenie laga za pomocą różnicy w czasie między 
    // obecnym czasem a czasem przysłanym przez serwer w komendzie PING
    private static long calculateLag(/*String[] data*/) {
        if (sendTime == 0) return 0; 

        return System.currentTimeMillis() - sendTime;
    }

    private static void processMessage(String message) {
        String[] data = message.split("\\|");
        String command = data[0];

        switch (command) {
            case "PONG":
                System.out.println("ping: " + calculateLag(/*data*/) + "ms");
                break;
                
            case "LOGIN_SUCCESS":
                System.out.println("Zalogowano");
                startPinging();
                isLoggingOrLogged.set(true);
                SceneManager.switchToMenu();
                
                break;

            case "LOGIN_FAILED":
                String cause = data.length > 1 ? data[1] : "Nieznany blad";
                
                System.out.println("Odmowa dostepu: " + cause);
                
                SceneManager.setStatus("Blad logowania: " + cause );
                
                disconnect(); 
                break;
                
            case "BOARD_UPDATE":
                // String nowyFEN = dane[1];
                // TODO: 
                // Platform.runLater(() -> {
                    
                // });
                break;
                
            default:
                System.out.println("Nieznana komenda z serwera: " + command);
                break;
        }
    }


    private static void startPinging() {
        clock = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        
        Runnable wyslijPing = () -> {
            sendTime = System.currentTimeMillis();
            sendCommand("PING");
        };

        clock.scheduleAtFixedRate(wyslijPing, 3, 3, TimeUnit.SECONDS);
    }
}
