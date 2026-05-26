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
                MenuView view = new MenuView(); 
                mainStage.setScene(view.createScene(mainStage));
            }
        });
    }

    public static void switchToGame(String mojKolor, String przeciwnik, String czasGry) {
        Platform.runLater(() -> {
            if (mainStage != null) {
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
}
