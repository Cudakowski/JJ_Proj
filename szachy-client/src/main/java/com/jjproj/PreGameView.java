package com.jjproj;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Layout JavaFX (bez FXML) zgodny z mockupem:
 * - lewa: lista gier
 * - prawa: nowa gra
 * - dół: wstecz
 *
 * Zakłada użycie Twojego CSS (styleClass)
 */
public class PreGameView {

public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("border-pane");

        // top
        Label title = new Label("Przed rozpoczęciem gry...");




        HBox topBar = new HBox(title);
        topBar.setPadding(new Insets(20, 20, 10, 20));
        
        root.setTop(topBar);

        // left
        Label leftTitle = new Label("Lista rozpoczętych gier");


        ListView<String> gamesList = new ListView<>();

        ObservableList<String> items = FXCollections.observableArrayList(
                "Gra #1 - biały vs czarny",
                "Gra #2 - czarny vs biały",
                "Gra #3 - losowa"
        );

        gamesList.setItems(items);

        Button resumeBtn = new Button("Wznów grę i zaproś");

        resumeBtn.setMaxWidth(Double.MAX_VALUE);

        resumeBtn.setOnAction(e -> {
            OnlinePlayersView onlinePlayersView = new OnlinePlayersView();
            stage.setScene(onlinePlayersView.createScene(stage));                
        });


        VBox leftPanel = new VBox(15, leftTitle, gamesList, resumeBtn);
        leftPanel.setPadding(new Insets(20));




        // right
        Label rightTitle = new Label("Nowa gra");


        ToggleGroup colorGroup = new ToggleGroup();

        RadioButton c1 = new RadioButton("BIAŁY (ty), CZARNY (oponent)");
        c1.setToggleGroup(colorGroup);
        c1.setSelected(true);

        RadioButton c2 = new RadioButton("CZARNY (ty), BIAŁY (oponent)");
        c2.setToggleGroup(colorGroup);

        RadioButton c3 = new RadioButton("losowo");
        c3.setToggleGroup(colorGroup);

        Label wybierzKolor = new Label("Wybierz kolor: ");

        VBox colorBox = new VBox(10,
               wybierzKolor, c1, c2, c3
        );

        ToggleGroup timeGroup = new ToggleGroup();

        RadioButton t1 = new RadioButton("w sumie z czasem");
        t1.setToggleGroup(timeGroup);
        t1.setSelected(true);

        RadioButton t2 = new RadioButton("bez ograniczenia");
        t2.setToggleGroup(timeGroup);

        Label czasGry  = new Label("Czas gry: ");

        VBox timeBox = new VBox(10,
                czasGry, t1, t2
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button nextBtn = new Button("Dalej");

        nextBtn.setMaxWidth(Double.MAX_VALUE);

        nextBtn.setOnAction(e -> {
            OnlinePlayersView onlinePlayersView = new OnlinePlayersView();
            stage.setScene(onlinePlayersView.createScene(stage));                
        });

        VBox rightPanel = new VBox(20,
                rightTitle,
                colorBox,
                timeBox,
                spacer,
                nextBtn
        );
        rightPanel.setPadding(new Insets(20));


        // srodek
        HBox center = new HBox(30, leftPanel, rightPanel);
        center.setPadding(new Insets(20));

        root.setCenter(center);

        center.setAlignment(javafx.geometry.Pos.CENTER);

        // dol
        Button backBtn = new Button("Wstecz");
        backBtn.setOnAction(e -> {
        MenuView menuView = new MenuView();
        stage.setScene(menuView.createScene(stage));
        });



        HBox backBtnContainer = new HBox(backBtn);
        backBtnContainer.setAlignment(Pos.CENTER_LEFT);


        VBox bottomContainer = new VBox(10, backBtnContainer, SzachyOnline.status);
        bottomContainer.setPadding(new Insets(10, 20, 20, 20));
        bottomContainer.setAlignment(Pos.CENTER); 

        root.setBottom(bottomContainer);


        // Stylizowanie
        root.getStyleClass().add("root-dark");
        title.getStyleClass().add("subtitle");
        topBar.getStyleClass().add("top-bar");
        leftTitle.getStyleClass().add("side-title");
        gamesList.getStyleClass().add("list-view");
        leftPanel.getStyleClass().add("panel-dark");
        resumeBtn.getStyleClass().add("btn-main");
        leftTitle.getStyleClass().add("side-title");
        resumeBtn.getStyleClass().add("btn-main");
        rightTitle.getStyleClass().add("side-title");
        nextBtn.getStyleClass().add("btn-main");
        rightPanel.getStyleClass().add("panel-dark");
        backBtn.getStyleClass().add("btn-main");
        wybierzKolor.getStyleClass().add("status-label");
        czasGry.getStyleClass().add("status-label");


        gamesList.setPrefHeight(500);
        leftPanel.setPrefWidth(400);
        rightPanel.setPrefWidth(500);

        HBox.setHgrow(title, Priority.ALWAYS);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(javafx.geometry.Pos.CENTER);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        
        rightPanel.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        colorBox.setAlignment(javafx.geometry.Pos.CENTER);
        timeBox.setAlignment(javafx.geometry.Pos.CENTER);

        rightTitle.setMaxWidth(Double.MAX_VALUE);
        rightTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);


        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(
                getClass().getResource("/View.css").toExternalForm()
        );

        return scene;
    }
}
