package com.jjproj;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SceneManager {

    public static Label status;
    private static Stage mainStage;
    private static ObservableList<String> activeUsersList;
    private static WaitingForPlayerView activeWaitingForPlayerView;
    private static Label activeBadge;
    private static boolean areThereNewNotifications = false;
    private static boolean isNotificationsViewActive = false;
    private static GameView activeGameView;

    // w każdym wiev
    public static void registerStatusLabel(Label label) {
        status = label;
    }

    // w czakaniu na odpowiedź
    public static void registerWaitingView(WaitingForPlayerView view) {
        activeWaitingForPlayerView = view;
    }

    public static void registerNotificationBadge(Label badge) {
        activeBadge = badge;
        
        Platform.runLater(() -> {
            if (activeBadge != null) {
                activeBadge.setVisible(areThereNewNotifications);
            }
        });
    }

    public static void setBadgeVisibility(boolean state) {
        areThereNewNotifications = state;
        Platform.runLater(() -> {
            if (activeBadge != null) {
                activeBadge.setVisible(state);
            }
        });
    }

    public static void setStatus(String s) {
        Platform.runLater(() -> {
            if (status != null) {
                status.setText(s);
            } else {
                System.out.println("[STATUS UI UKRYTY]: " + s);
            }
        });
    }

    // tylko przy starcie aplikacji
    public static void setWindow(Stage stage) {
        mainStage = stage;
    }

    

    

    public static void switchToMenu() {
        Platform.runLater(() -> {
            if (mainStage != null) {
                setNotificationsViewActive(false);
                MenuView view = new MenuView(); 
                mainStage.setScene(view.createScene(mainStage));
            }
        });
    }

    public static void switchToGame(String mojKolor, String przeciwnik, String czasGry) {
        Platform.runLater(() -> {
            if (mainStage != null) {
                setNotificationsViewActive(false);
                GameView view = new GameView(); 
                mainStage.setScene(view.createScene(mainStage, mojKolor, przeciwnik, czasGry));
            }
        });
    }


    

    public static void addWaitingStatus(String logMessage) {
        Platform.runLater(() -> {
            if (activeWaitingForPlayerView != null) {
                activeWaitingForPlayerView.addStatus(logMessage);
            }
        });
    }

    public static void receiveInvitation(String sender, String message) {
        if(!isNotificationsViewActive){
            setBadgeVisibility(true);
        }

        Platform.runLater(() -> {
            NotificationsView.addNotification(sender, message);
        });
    }

    

    public static void registerPlayersList(ObservableList<String> list) {
        activeUsersList = list;
    }

    public static void updatePlayersList(String[] users) {
        Platform.runLater(() -> {
            if (activeUsersList != null) {
                activeUsersList.setAll(users);
            }
        });
    }

    public static void clearPlayersList() {
        Platform.runLater(() -> {
            if (activeUsersList != null) {
                activeUsersList.clear();
            }
        });
    }

    public static void addInviteNotification(String nadawca) {
        Platform.runLater(() -> {
            if (activeUsersList != null) {
                activeUsersList.add(nadawca);
            }
        });
    }

    public static void setNotificationsViewActive(boolean isActive) {
        isNotificationsViewActive = isActive;
        if (isActive) {
            setBadgeVisibility(false);
        }
    }
    
    // GameView commands
    public static void registerGameView(GameView gameView) {
        activeGameView = gameView;
    }

    public static void updateBoard(String fen) {
        Platform.runLater(() -> {
            if (activeGameView != null) {
                activeGameView.updateBoardFromFEN(fen);
            }
        });
    }

    public static void setLegalMoves(String moveData) {
        Platform.runLater(() -> {
            if (activeGameView != null) {
                activeGameView.updateLegalMoves(moveData);
            }
        });
    }

    public static void applyOpponentMove(String from, String to) {
        Platform.runLater(() -> {
            if (activeGameView != null) {
                activeGameView.applyLocalMove(from, to);
            }
        });
    }

    public static void revertMyMove(String from, String to) {
        Platform.runLater(() -> {
            if (activeGameView != null) {
                activeGameView.revertLocalMove(from, to);
            }
        });
    }

    public static void handleGameOver(String winner, String cause) {
        Platform.runLater(() -> {
            setStatus("KONIEC GRY! " + cause + ". Wygrywa: " + winner);
            
            if (activeGameView != null) {
                activeGameView.stopTimer();
                activeGameView.clearMoves();
            }
        });
    }

    public static void addMoveToHistory(String moveNotation) {
        Platform.runLater(() -> {
            if (activeGameView != null) {
                activeGameView.addMoveToHistory(moveNotation);
            }
        });
    }

    public static void syncTime(int whiteSec, int blackSec) {
        Platform.runLater(() -> {
            if (activeGameView != null) {
                activeGameView.syncTimeWithServer(whiteSec, blackSec);
            }
        });
    }

    public static void handleGamePaused(String requester) {
        Platform.runLater(() -> {
            setStatus("Gra wstrzymana i zapisana przez: " + requester);
            
            if (activeGameView != null) {
                activeGameView.stopTimer();
                activeGameView.clearMoves();
            }
            
            // // Dajemy graczom 2 sekundy na przeczytanie komunikatu, po czym wracamy do menu
            // new Thread(() -> {
            //     try {
            //         Thread.sleep(2000);
            //     } catch (InterruptedException e) {}
                
            //     switchToMenu();
            // }).start();
        });
    }

    private static ObservableList<String> activePausedGamesList;

    public static void registerPausedGamesList(ObservableList<String> list) {
        activePausedGamesList = list;
    }

    public static void updatePausedGamesList(String data) {
        Platform.runLater(() -> {
            if (activePausedGamesList != null) {
                activePausedGamesList.clear();
                if (data == null || data.trim().isEmpty()) {
                    activePausedGamesList.add("Brak przerwanych gier.");
                    return;
                }
                
                // data to np: "1:Romek:Bialy,5:Mietek:Czarny"
                String[] games = data.split(",");
                for (String g : games) {
                    String[] parts = g.split(":");
                    if (parts.length == 3) {
                        activePausedGamesList.add("[" + parts[0] + "] Przeciwnik: " + parts[1] + " (Twój kolor: " + parts[2] + ")");
                    }
                }
            }
        });
    }
    
    public static void switchToWaitingForResume(String opponentName) {
        Platform.runLater(() -> {
            if (mainStage != null) {
                setNotificationsViewActive(false);
                WaitingForPlayerView waitingView = new WaitingForPlayerView();
                mainStage.setScene(waitingView.createScene(mainStage, opponentName));
            }
        });
    }
}
