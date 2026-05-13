package com.jjproj;

import java.io.IOException;
import java.net.Socket;

import com.jjproj.DatabaseIntegration.UsersTable;

public class ClientHandler implements Runnable {
    
    private long lastPingTime = System.currentTimeMillis();
    private TCPConnection connection = null;
    private String playerLogin = null;

    public ClientHandler(Socket socket) {
        try {
            this.connection = new TCPConnection(socket);
        } catch (IOException e){
            System.err.println("Błąd przy tworzeniu połączenia TCP dla klienta: " + e.getMessage());
        }
    }

    public long getlastPingTime(){
        return lastPingTime;
    }

    @Override
    public void run() {

        if (connection == null) return;

        try {
            String message;
            
            while ((message = connection.awaitString()) != null) {
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                
                processMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Rozłączono klienta (nagła utrata połączenia): " + 
                                (playerLogin != null ? playerLogin : "Nieznajomy"));
        } finally {
            clearAfterDisconnect();
        }
    }

    public void sendMessage(String wiadomosc) {
        if (connection != null) {
            connection.sendString(wiadomosc);
        }
    }

    private void processMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return; 
        }

        String[] data = message.split("\\|");
        String command = data[0];

        switch (command) {
            
            case "LOGIN":
                commandLogin(data);
                break;
                
            case "REGISTER":
                commandRegister(data);
                break;
                
            case "INVITE":
                // obsluzZaproszenie(data);
                break;
                
            case "PING":
                lastPingTime = System.currentTimeMillis();
                sendMessage("PONG");
                break;
                
            case "MOVE":
                //lastPingTime = System.currentTimeMillis();
                // obsluzRuch(data);
                break;

            default:
                System.out.println("[Ostrzeżenie] Otrzymano nieznaną komendę: " + command);
                break;
        }
    }

    private void commandRegister(String[] data) {
        if (data.length >= 3) {
            String enteredLogin = data[1];
            String enteredPassword = data[2];
            
            // TODO: Zapytanie do bazy danych MySQL
            boolean isCorrect = UsersTable.registerUser(enteredLogin, enteredPassword);

            if(isCorrect){
                if (Server.onlineUsers.containsKey(enteredLogin)) {
                    sendMessage("REGISTER_FAILED|Gracz jest juz zalogowany");
                    return;
                }

                this.playerLogin = enteredLogin;
                Server.onlineUsers.put(this.playerLogin, this);
                
                sendMessage("REGISTER_SUCCESS|" + this.playerLogin);
                System.out.println("Gracz " + this.playerLogin + " zarejestrowal sie poprawnie");

            } else {
                sendMessage("REGISTER_FAILED|Gracz o takiej nazwie juz istnieje");
            }
        } else {
            sendMessage("REGISTER_FAILED|Brak loginu lub hasla");
        }
    }

    private void commandLogin(String[] data) {
        if (data.length >= 3) {
            String enteredLogin = data[1];
            String enteredPassword = data[2];
            
            // TODO: Zapytanie do bazy danych MySQL
            boolean isCorrect = UsersTable.checkLogin(enteredLogin, enteredPassword);

            if(isCorrect){
                if (Server.onlineUsers.containsKey(enteredLogin)) {
                    sendMessage("LOGIN_FAILED|Gracz jest juz zalogowany");
                    return;
                }

                this.playerLogin = enteredLogin;
                Server.onlineUsers.put(this.playerLogin, this);
                
                sendMessage("LOGIN_SUCCESS|" + this.playerLogin);
                System.out.println("Gracz " + this.playerLogin + " zalogowal sie poprawnie");

            } else {
                sendMessage("LOGIN_FAILED|Niepoprawny login lub haslo");
            }
        } else {
            sendMessage("LOGIN_FAILED|Brak loginu lub hasla");
        }
    }

    private void clearAfterDisconnect() {
        if (playerLogin != null) {
            Server.onlineUsers.remove(playerLogin);
            System.out.println("Usunieto " + playerLogin + " z listy online.");
        }
        
        if (connection != null) {
            connection.close();
        }
    }

    public void pingAbsenceDisconnection() {
        System.out.println("Odlaczanie gracza " + playerLogin + " z powodu braku odpowiedzi");

        if (connection != null) {
            connection.close(); 
        }
    }
}

// Kto wysyła?,Komenda,Argumenty,Przykładowe użycie,Opis
// Klient,LOGIN,Nick Hasło,`LOGIN,Tomek
// Klient,REGISTER,Nick Hasło,`REGISTER,Adam
// Klient,PING,(brak),PING,"System Heartbeat (bicia serca). Klient wysyła to co np. 3 sekundy, żeby pokazać, że żyje."
// Serwer,PONG,(brak),PONG,Odpowiedź serwera na PING.
// Serwer,LOGIN_SUCCESS,Nick,`LOGIN_SUCCESS,Tomek`
// Serwer,LOGIN_FAILED,Powód,`LOGIN_FAILED,Złe hasło`

// Kto wysyła?,Komenda,Argumenty,Przykładowe użycie,Opis
// Klient,GET_USER_LIST,(brak),GET_USER_LIST,Klient prosi o listę zalogowanych graczy do wyświetlenia w Menu.
// Serwer,USER_LIST,Lista nicków,`USER_LIST,"Adam,Kasia,Michal`"
// Klient,INVITE,NickPrzeciwnika,`INVITE,Adam`
// Serwer,INVITE_FROM,NickZapraszającego,`INVITE_FROM,Tomek`
// Klient,ACCEPT,NickZapraszającego,`ACCEPT,Tomek`
// Klient,DECLINE,NickZapraszającego,`DECLINE,Tomek`
// Serwer,INVITE_REJECTED,Powód,`INVITE_REJECTED,Odrzucił`
// Serwer,GAME_START,TwójKolor Przeciwnik BiałyCzas CzarnyCzas,`GAME_START,Bialy

// Kto wysyła?,Komenda,Argumenty,Przykładowe użycie,Opis
// Klient,MOVE,Skąd Dokąd,`MOVE,E2
// Serwer,BOARD_UPDATE,NowyFEN,`BOARD_UPDATE,rnbqkbnr/pppp1ppp...`
// Serwer,LEGAL_MOVES,ListaRuchów,`LEGAL_MOVES,"E2:E3,E4"
// Serwer,TIME_UPDATE,BiałyCzasMs CzarnyCzasMs,`TIME_UPDATE,595000
// Serwer,GAME_OVER,KtoWygrał Powód,`GAME_OVER,Bialy
// Klient,PAUSE_REQUEST,(brak),PAUSE_REQUEST,Gracz chce wstrzymać grę (wyjść do menu). Serwer musi zapisać aktualny stan (FEN) do bazy.
// Serwer,GAME_PAUSED,(brak),GAME_PAUSED,"Potwierdzenie z serwera, że można bezpiecznie opuścić ekran gry."
