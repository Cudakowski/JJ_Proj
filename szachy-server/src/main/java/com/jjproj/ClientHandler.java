package com.jjproj;

import java.io.IOException;
import java.net.Socket;

import com.jjproj.DatabaseIntegration.UsersTable;
import com.jjproj.Logic.GameSession;

public class ClientHandler implements Runnable {
    
    private long lastPingTime = System.currentTimeMillis();
    private TCPConnection connection = null;
    private String playerLogin = null;
    private boolean isInGame=false;
    private boolean isWaitingForPlayer = false;
    private String waitingForPlayerName = null;
    private GameSession currentSession = null;
    private String selectedInviteTime = "Bez ograniczen";
    private String selectedInviteColor = "Bialy";

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

    public String getPlayerLogin() {
        return playerLogin;
    }

    private void processMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return; 
        }

        String[] data = message.split("\\|");
        String command = data[0];

        switch (command) {
            
            // login
            case "LOGIN":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandLogin(data);
                break;
                
            case "REGISTER":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandRegister(data);
                break;

            // usage
            case "GET_USER_LIST":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandGetUserList(data);
                break;
                
            case "INVITE":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandInvite(data);
                break;

            case "CANCEL_INVITE":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandCancelInvite(data);
                break;
            
            case "ACCEPT":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandAccept(data);
                break;
                
            case "DECLINE":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                commandDecline(data);
                break;

            
            // ping
            case "PING":
                lastPingTime = System.currentTimeMillis();
                sendMessage("PONG");
                break;
            
            // game
            case "MOVE":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                
                commandMove(data);
                break;

            default:
                System.out.println("[Ostrzeżenie] Otrzymano nieznaną komendę: " + command);
                break;
        }
    }

    public GameSession getCurrentSession() { return currentSession; }
    public void setInGame(boolean val) { this.isInGame = val; }

    private void commandMove(String[] data){
        if (currentSession != null && data.length >= 3) {
            String zPola = data[1];
            String naPole = data[2];
            
            String wynikRuchu = currentSession.applyMove(zPola, naPole, this.playerLogin);
            String[] wynikData = wynikRuchu.split("\\|");
            
            if (wynikData[0].equals("ERROR")) {
                this.sendMessage("MOVE_ERROR|" + wynikData[1] + "|" + zPola + "|" + naPole);
                
                ClientHandler przeciwnik = currentSession.getOpponent(this);
                przeciwnik.sendMessage("GAME_STATUS|Przeciwnik wykonał nielegalny ruch.");
            } else if (wynikData[0].equals("GAME_OVER")) {
                ClientHandler przeciwnik = currentSession.getOpponent(this);
                if (przeciwnik != null) {
                    przeciwnik.sendMessage("OPPONENT_MOVED|" + zPola + "|" + naPole);
                    przeciwnik.isInGame = false;
                }
                
                this.isInGame = false;
                
                currentSession.broadcast("GAME_OVER|" + wynikData[1] + "|" + wynikData[2]);
            }
            else if (wynikData[0].equals("MOVE_OK")) {
                String moveNotation = wynikData[wynikData.length - 1];
                currentSession.broadcast("NEW_MOVE|" + moveNotation);

                ClientHandler przeciwnik = currentSession.getOpponent(this);
                przeciwnik.sendMessage("OPPONENT_MOVED|" + zPola + "|" + naPole);
                
                String legalMovesDlaPrzeciwnika = currentSession.getAllLegalMovesFor();
                przeciwnik.sendMessage("LEGAL_MOVES|" + legalMovesDlaPrzeciwnika);
                
                this.sendMessage("MOVE_ACCEPTED");
            }
        }
    }

    private void commandAccept(String[] data) {
        if (data.length > 1) {
            String inviterName = data[1];
            
            ClientHandler inviterClient = Server.onlineUsers.get(inviterName);
            
            if (inviterClient == null) {
                this.sendMessage("INVITE_ERROR|Gracz " + inviterName + " nie jest zalogowany");
                return;
            }

            if (inviterClient.isInGame) {
                this.sendMessage("INVITE_ERROR|Gracz " + inviterName + " jest już w grze");
                return;
            }

            if (!inviterClient.isWaitingForPlayer) {
                this.sendMessage("INVITE_ERROR|Gracz opuscil poczekalnie");
                return;
            }

                
            this.isInGame = true;
            inviterClient.isInGame = true;
            inviterClient.isWaitingForPlayer = false;
            this.waitingForPlayerName = null;



            ClientHandler bialyGracz;
            ClientHandler czarnyGracz;

            String ostatecznyKolor = inviterClient.selectedInviteColor;
            
            if (ostatecznyKolor.equalsIgnoreCase("Losowo")) {
                ostatecznyKolor = (Math.random() < 0.5) ? "Bialy" : "Czarny";
            } 
            
            if (ostatecznyKolor.equalsIgnoreCase("Czarny")) {
                bialyGracz = this;          
                czarnyGracz = inviterClient; 
            } else {
                bialyGracz = inviterClient; 
                czarnyGracz = this;         
            }
            
            
            
            
            GameSession session = new GameSession(bialyGracz, czarnyGracz, inviterClient.selectedInviteTime);
            
            this.currentSession = session;
            inviterClient.currentSession = session;
            
            
            session.startGame();
            
        }
    }

    @Override
    public String toString() {
        return playerLogin;
    }

    private void commandCancelInvite(String[] data) {
        this.isWaitingForPlayer = false;
        this.waitingForPlayerName = null;

        if (data.length > 1) {
            String invitedUser = data[1];
            ClientHandler invitedClient = Server.onlineUsers.get(invitedUser);
            
            if (invitedClient != null) {
                
                invitedClient.sendMessage("INVITE_CANCELLED|" + this.playerLogin);
            }
        }
    }

    private void commandDecline(String[] data) {
        if (data.length > 1) {
            String inviterName = data[1];
            
            ClientHandler inviterClient = Server.onlineUsers.get(inviterName);
            
            if (inviterClient != null) {
                inviterClient.sendMessage("INVITE_REJECTED|" + this.playerLogin);
            }
        }
    }

    private void commandGetUserList(String[] data) {
        java.util.StringJoiner playerList = new java.util.StringJoiner(",");

        for (String playerName : Server.onlineUsers.keySet()) {
            
            if (this.playerLogin != null && !playerName.equals(this.playerLogin)) {
                playerList.add(playerName);
            }
        }


        sendMessage("USER_LIST|" + playerList.toString());
    }


    private void commandInvite(String[] data) {
        if (data.length >= 4) {
            String invitedUser = data[1];
            String color = data[2];
            String time = data[3];
            
            ClientHandler invitedClient = Server.onlineUsers.get(invitedUser);
            
            if (invitedClient == null) {
                this.sendMessage("INVITE_ERROR|Gracz jest juz offline");
                return;
            }

            if(invitedClient.isInGame){
                this.sendMessage("INVITE_ERROR|Gracz jest juz w grze");
                return;
            }

            this.isWaitingForPlayer = true;
            this.waitingForPlayerName = invitedUser;
            this.selectedInviteColor = color;
            this.selectedInviteTime = time;

            invitedClient.sendMessage("INVITE_RECEIVED|" + this.playerLogin + "|" + color + "|" + time);
        
            
        }
    }



    private void commandRegister(String[] data) {
        if (data.length >= 3) {
            String enteredLogin = data[1];
            String enteredPassword = data[2];
            
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
            
            for (ClientHandler client : Server.onlineUsers.values()) {
                if (client.isWaitingForPlayer && playerLogin.equals(client.waitingForPlayerName)) {
                    
                    client.isWaitingForPlayer = false;
                    client.waitingForPlayerName = null;
                    
                    client.sendMessage("INVITE_EXPIRED|Gracz " + playerLogin + " offline");
                }
            }

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
// Serwer,REGISTER_SUCCESS,Nick,`REGISTER_SUCCESS,Tomek`
// Serwer,REGISTER_FAILED,Powód,`REGISTER_FAILED,`

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


/*

create database szachy;

use szachy;

create table users (
	user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_login VARCHAR(50),
    user_password VARCHAR(50)
);

select * from users;

*/