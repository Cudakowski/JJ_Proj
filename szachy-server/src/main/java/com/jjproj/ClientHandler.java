package com.jjproj;

import java.io.IOException;
import java.net.Socket;

import com.jjproj.DatabaseIntegration.GamesTable;
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
    private Integer resumingGameId = null;

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

            case "LEAVE_GAME":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                if (this.isInGame && this.currentSession != null) {
                    this.currentSession.playerDisconnected(this);
                }
                break;

            case "PAUSE_REQUEST":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                if (this.isInGame && this.currentSession != null) {
                    this.currentSession.pauseGame(this.playerLogin);
                }
                break;

            case "GET_PAUSED_GAMES":
                System.out.println("[Odebrano] " + (playerLogin != null ? playerLogin : "Nieznajomy") + ": " + message);
                Integer myId = UsersTable.getUserId(this.playerLogin);
                if (myId != null) {
                    String pausedGames = GamesTable.getPausedGamesForUser(myId);
                    sendMessage("PAUSED_GAMES_LIST|" + pausedGames);
                }
                break;

            case "RESUME_GAME":
                if (data.length > 2) {
                    commandResumeGame(data[1], data[2]); // data[1] = gameId, data[2] = nazwa przeciwnika
                }
                break;

            default:
                System.out.println("[Ostrzeżenie] Otrzymano nieznaną komendę: " + command);
                break;
        }
    }

    public GameSession getCurrentSession() { return currentSession; }
    public void setInGame(boolean val) { this.isInGame = val; }

    private void commandResumeGame(String gameIdStr, String opponentName) {
        ClientHandler opponentClient = Server.onlineUsers.get(opponentName);
        
        if (opponentClient == null) {
            this.sendMessage("RESUME_ERROR|Gracz " + opponentName + " jest offline.");
            return;
        }
        if (opponentClient.isInGame) {
            this.sendMessage("RESUME_ERROR|Gracz " + opponentName + " jest już w innej grze.");
            return;
        }

        this.isWaitingForPlayer = true;
        this.waitingForPlayerName = opponentName;
        this.selectedInviteTime = "Bez ograniczen"; 
        this.resumingGameId = Integer.parseInt(gameIdStr);
        
        this.sendMessage("RESUME_WAITING|" + opponentName);
        
        opponentClient.sendMessage("INVITE_RESUME|" + this.playerLogin + "|" + gameIdStr);
    }

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
                
                
                currentSession.broadcast("GAME_OVER|" + wynikData[1] + "|" + wynikData[2]);
                currentSession.endGameSession();
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
            GameSession session;

            if (inviterClient.resumingGameId != null) {
                // WZNOWIENIE GRY Z BAZY
                int gameId = inviterClient.resumingGameId;
                String fen = GamesTable.getGameFen(gameId); 
                Integer myDbId = UsersTable.getUserId(this.playerLogin);
                
                boolean amIWhite = GamesTable.isUserWhiteInGame(gameId, myDbId); 
                
                if (amIWhite) {
                    bialyGracz = this;
                    czarnyGracz = inviterClient;
                } else {
                    bialyGracz = inviterClient;
                    czarnyGracz = this;
                }
                
                session = new GameSession(bialyGracz, czarnyGracz, fen, gameId);
                inviterClient.resumingGameId = null;

            } else {
                // NOWA GRA
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
                
                session = new GameSession(bialyGracz, czarnyGracz, inviterClient.selectedInviteTime);
            }
            
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
            this.resumingGameId = null;

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

            if (this.isInGame && this.currentSession != null) {
                this.currentSession.playerDisconnected(this);
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

    public void clearSession() {
        this.isInGame = false;
        this.currentSession = null;
    }
}


/*

create database szachy;

use szachy;

create table users (
	user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_login VARCHAR(50),
    user_password VARCHAR(50)
);

CREATE TABLE pieces (
    piece_id INT PRIMARY KEY AUTO_INCREMENT,
    piece_symbol CHAR(1),   -- np. 'P', 'R', 'N', 'B', 'Q', 'K'
    is_white_black BOOLEAN -- 0(biały) lub 1(black)
);



CREATE TABLE games (
    game_id INT PRIMARY KEY AUTO_INCREMENT,
    game_fen VARCHAR(90) NOT NULL, 
    is_over BOOLEAN DEFAULT FALSE,
    white_player_id INT NOT NULL,
    black_player_id INT NOT NULL,
    winner_white_black BOOLEAN NULL,    -- NULL dopóki gra trwa lub jest remis, 0(biały wygrany) lub 1(black wygrany)
    FOREIGN KEY(white_player_id) REFERENCES users(user_id),
    FOREIGN KEY(black_player_id) REFERENCES users(user_id)
);

CREATE TABLE moves (
    move_id INT PRIMARY KEY AUTO_INCREMENT,
    game_id INT NOT NULL,
    --  numer ruchu nie potrzebny bo będzie określany za omocą id ruchu które i tak jest zgodne hronologicznie
    move_from VARCHAR(2) NOT NULL,    -- np. 'E2'
    move_to VARCHAR(2) NOT NULL,      -- np. 'E4'
    -- nie wydaje mi się by zapamiętywanie notacji było potrzebne
    moved_piece_id INT NOT NULL,
    captured_piece_id INT NULL,       -- NULL, jeśli nikt nie zginął
    FOREIGN KEY(game_id) REFERENCES games(game_id),
    FOREIGN KEY(moved_piece_id) REFERENCES pieces(piece_id),
    FOREIGN KEY(captured_piece_id) REFERENCES pieces(piece_id)
);

INSERT INTO pieces (piece_symbol, is_white_black) VALUES 
-- FIGURY BIAŁE (0)
('P', 0), -- Pionek (Pawn)
('R', 0), -- Wieża (Rook)
('N', 0), -- Skoczek (kNight)
('B', 0), -- Goniec (Bishop)
('Q', 0), -- Hetman (Queen)
('K', 0), -- Król (King)

-- FIGURY CZARNE (1)
('p', 1), -- Pionek
('r', 1), -- Wieża
('n', 1), -- Skoczek
('b', 1), -- Goniec
('q', 1), -- Hetman
('k', 1); -- Król

*/