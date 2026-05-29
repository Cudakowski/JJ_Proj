package com.jjproj;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;


public class NetworkManager {
    
    private static TCPConnection connection;
    private static Thread listenerThread;
    private static ScheduledExecutorService clock;
    private static long sendTime = 0;
    private static AtomicBoolean isLoggingOrLogged = new AtomicBoolean(false);
    private static final String hostIP4="localhost";
    private static final int hostPort=5000;

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

    public static void onRegister(String wpisanyLogin, String wpisaneHaslo, String wpisanePonownieHaslo) {

        if(!isLoggingOrLogged.compareAndSet(false,true)){
            System.out.println("Blokada podwojnej rejestracji");
            return;
        }

        if (!wpisaneHaslo.equals(wpisanePonownieHaslo)) {
            SceneManager.setStatus("Hasła nie są zgodne");
            isLoggingOrLogged.set(false);
            return;
        }// TODO: wymagania haseł czyli min 3 znaki, cyfra, znak specjalny itd.

        Thread bridgeThread = new Thread(() -> {
        
            boolean isConnected = NetworkManager.connect(hostIP4, hostPort);
            
            
            if (isConnected) {
                NetworkManager.sendCommand("REGISTER|" + wpisanyLogin + "|" + wpisaneHaslo);
                SceneManager.setStatus("Weryfikacja danych");
                
            } else {
                SceneManager.setStatus("Brak połączenia z serwerem");
                isLoggingOrLogged.set(false);

            }
            
        });
        
        bridgeThread.setDaemon(true);
        bridgeThread.start();
    }

    public static void onLogin(String wpisanyLogin,String wpisaneHaslo) {

        if(!isLoggingOrLogged.compareAndSet(false,true)){
            System.out.println("Blokada podwojnego logowania");
            return;
        }

        Thread bridgeThread = new Thread(() -> {
            boolean isConnected = NetworkManager.connect(hostIP4, hostPort);
            
            
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
        String cause="?";

        switch (command) {

            // ping pong
            case "PONG":
                System.out.println("ping: " + calculateLag(/*data*/) + "ms");
                break;
            
            // logowanie
            case "LOGIN_SUCCESS":
                System.out.println("Zalogowano");
                startPinging();
                isLoggingOrLogged.set(true);
                SceneManager.switchToMenu();
                
                break;

            case "LOGIN_FAILED":
                cause = data.length > 1 ? data[1] : "Nieznany blad";
                
                System.out.println("Odmowa dostepu: " + cause);
                
                SceneManager.setStatus("Blad logowania: " + cause );
                
                disconnect(); 
                break;

            case "REGISTER_SUCCESS":
                System.out.println("Zarejestrowano");
                startPinging();
                isLoggingOrLogged.set(true);
                SceneManager.switchToMenu();
                break;

            case "REGISTER_FAILED":
                cause = data.length > 1 ? data[1] : "Nieznany blad";
                
                System.out.println("Odmowa rejestracji: " + cause);
                
                SceneManager.setStatus("Blad rejestracji: " + cause );
                
                disconnect(); 
                break;

            // zapraszanie
            case "USER_LIST":
                //System.out.println("Pobrano liste uzytkownikow");
                SceneManager.setStatus("Pobrano liste uzytkownikow");
                if (data.length > 1) {
                    String[] availablePlayers = data[1].split(",");
                    
                    SceneManager.updatePlayersList(availablePlayers);
                    SceneManager.setStatus("Lista zaktualizowana.");
                } else {
                    SceneManager.clearPlayersList();
                    SceneManager.setStatus("Brak innych graczy online.");
                }
                break;


            case "INVITE_RECEIVED":
                if (data.length >= 4) {
                    String sender = data[1];
                    String color = data[2];
                    String time = data[3];
                    
                    String informacjaDlaOdbiorcy = "";
                    if (color.equals("Bialy")) {
                        informacjaDlaOdbiorcy = "Twój kolor: Czarny";
                    } else if (color.equals("Czarny")) {
                        informacjaDlaOdbiorcy = "Twój kolor: Biały";
                    } else {
                        informacjaDlaOdbiorcy = "Kolor: Losowo";
                    }
                    
                    String tresc = sender + " zaprasza do gry! (" + time + "; " + informacjaDlaOdbiorcy + ")";
                    
                    SceneManager.receiveInvitation(sender, tresc);
                }
                break;

            case "INVITE_ERROR":
                if (data.length > 1) {
                    SceneManager.setStatus(data[1]);
                    SceneManager.addWaitingStatus("Error: " +data[1]);
                }
                break;

            case "INVITE_REJECTED":
                if (data.length > 1) {
                    String rejecter = data[1];
                    SceneManager.addWaitingStatus("Gracz " + rejecter + " odrzucił zaproszenie.");
                }
                break;

            case "INVITE_CANCELLED":
                if (data.length > 1) {
                    String canceller = data[1];
                    
                    Platform.runLater(() -> {
                        NotificationsView.markAsCancelled(canceller);
                    });
                    
                    //SceneManager.setStatus("Zaproszenie od " + canceller + " wygasło.");
                }
                break;

            case "INVITE_EXPIRED":
                if (data.length > 1) {
                    cause = data[1];
                    
                    SceneManager.addWaitingStatus(cause);
                    SceneManager.addWaitingStatus("Zaproszenie wygasło. Możesz anulować.");
                    
                }
                break;

            case "GAME_START":
                if (data.length >= 3) {
                    String mojKolor = data[1];
                    String przeciwnik = data[2];
                    String czasGry = data[3];
                    
                    System.out.println("Start gry. Gram jako " + mojKolor + " przeciwko: " + przeciwnik);

                    
                    SceneManager.switchToGame(mojKolor,przeciwnik,czasGry);
                    //if (mojKolor.equals("Czarny")) {
                    //    SceneManager.setStatus("Oczekiwanie na ruch przeciwnika...");
                    //}
                }
                break;
            
            // w grze
            case "BOARD_UPDATE":
                if (data.length > 1) {
                    String fen = data[1];
                    SceneManager.updateBoard(fen);
                }
                break;

            case "LEGAL_MOVES":
                if (data.length > 1) {
                    SceneManager.setLegalMoves(data[1]);
                    SceneManager.setStatus("Twoja tura");
                }
                break;
                
            case "OPPONENT_MOVED":
                if (data.length >= 3) {
                    SceneManager.applyOpponentMove(data[1], data[2]);
                }
                break;
                
            case "MOVE_ERROR":
                if (data.length >= 4) {
                    // Cofamy własny zły ruch
                    SceneManager.revertMyMove(data[2], data[3]);
                    SceneManager.setStatus("Nielegalny ruch: " + data[1]);
                }
                break;
                
            case "MOVE_ACCEPTED":
                // Potwierdzenie odebrania ruchu
                SceneManager.setStatus("Oczekiwanie na ruch przeciwnika...");
                break;

            case "GAME_OVER":
                if (data.length >= 3) {
                    String winner = data[1];
                    cause = data[2];
                    
                    SceneManager.handleGameOver(winner, cause);
                }
                break;

            case "NEW_MOVE":
                if (data.length > 1) {
                    SceneManager.addMoveToHistory(data[1]);
                }
                break;
            
            case "TIME_UPDATE":
                if (data.length >= 3) {
                    int wTime = Integer.parseInt(data[1]);
                    int bTime = Integer.parseInt(data[2]);
                    SceneManager.syncTime(wTime, bTime);
                }
                break;
            
            case "GAME_PAUSED":
                if (data.length > 1) {
                    String requester = data[1];
                    SceneManager.handleGamePaused(requester);
                }
                break;

            case "PAUSED_GAMES_LIST":
                String gamesData = data.length > 1 ? data[1] : "";
                SceneManager.updatePausedGamesList(gamesData);
                break;
                
            case "RESUME_ERROR":
                if (data.length > 1) {
                    SceneManager.setStatus(data[1]);
                }
                break;
                
            case "RESUME_WAITING":
                if (data.length > 1) {
                    String opponent = data[1];
                    SceneManager.switchToWaitingForResume(opponent);
                }
                break;
            case "INVITE_RESUME":
                if (data.length >= 3) {
                    String sender = data[1];
                    String gameId = data[2];
                    
                    String tresc = sender + " proponuje wznowienie przerwanej partii (Gra #" + gameId + ")";
                    SceneManager.receiveInvitation(sender, tresc);
                }
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
