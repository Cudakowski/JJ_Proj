package com.jjproj;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class PreGameView {

    private final ToggleGroup colorGroup = new ToggleGroup();
    private final ToggleGroup timeGroup = new ToggleGroup();
    private Label statusLabel;

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("border-pane");

        // --- Struktura główna ---
        root.setTop(createHeader());
        root.setCenter(createMainContent(stage));
        root.setBottom(createFooter(stage));

        // --- Konfiguracja Sceny ---
        Scene scene = new Scene(root, 1150, 900);
        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
        
        return scene;
    }

    private HBox createHeader() {
        Label title = new Label("Konfiguracja Rozgrywki");
        title.getStyleClass().add("subtitle");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        HBox topBar = new HBox(title);
        topBar.setPadding(new Insets(30, 20, 10, 20));
        HBox.setHgrow(title, Priority.ALWAYS);
        return topBar;
    }

    private HBox createMainContent(Stage stage) {
        HBox container = new HBox(40, createSavedGamesPanel(stage), createNewGamePanel(stage));
        container.setPadding(new Insets(20, 60, 20, 60));
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private VBox createFooter(Stage stage) {
        statusLabel = new Label("Oczekiwanie na wybór...");
        statusLabel.getStyleClass().add("error-label");
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        SceneManager.registerStatusLabel(statusLabel);

        Button backBtn = new Button("← Wstecz");
        backBtn.getStyleClass().add("btn-main");
        backBtn.setOnAction(e -> stage.setScene(new MenuView().createScene(stage)));

        HBox navRow = new HBox(backBtn);
        navRow.setPadding(new Insets(10, 60, 0, 60));
        navRow.setAlignment(Pos.CENTER_LEFT);

        VBox footer = new VBox(15, navRow, statusLabel);
        footer.setPadding(new Insets(0, 0, 30, 0));
        return footer;
    }

    private VBox createSavedGamesPanel(Stage stage) {
        Label title = new Label("Kontynuuj zapisaną grę");
        title.getStyleClass().add("side-title");

        ListView<String> gamesList = new ListView<>(FXCollections.observableArrayList(
                "Gra #1 - biały vs czarny", 
                "Gra #2 - czarny vs biały", 
                "Gra #3 - losowa"
        ));
        gamesList.getStyleClass().add("modern-list");
        
        // Rozciąganie listy na całą dostępną wysokość
        VBox.setVgrow(gamesList, Priority.ALWAYS);

        Button resumeBtn = new Button("Wznów zaznaczoną");
        resumeBtn.setMaxWidth(Double.MAX_VALUE);
        resumeBtn.getStyleClass().add("btn-main");
        resumeBtn.setOnAction(e -> stage.setScene(new OnlinePlayersView().createScene(stage, "", "")));

        // Region wypychający przycisk na sam dół
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        return buildPanel(title, gamesList, spacer, resumeBtn);
    }

    private VBox createNewGamePanel(Stage stage) {
        Label title = new Label("Parametry nowej gry");
        title.getStyleClass().add("side-title");

        VBox colorSection = createOptionGroup("Wybierz kolor:",
                createRadio("BIAŁY (ty), CZARNY (oponent)", "Bialy", colorGroup, true),
                createRadio("CZARNY (ty), BIAŁY (oponent)", "Czarny", colorGroup, false),
                createRadio("LOSOWO", "Losowo", colorGroup, false));

        VBox timeSection = createOptionGroup("Limit czasu:",
                createRadio("bez ograniczeń", "Bez ograniczen", timeGroup, true),
                createRadio("10min (po 5min)", "10min", timeGroup, false),
                createRadio("20min (po 10min)", "20min", timeGroup, false),
                createRadio("40min (po 20min)", "40min", timeGroup, false),
                createRadio("60min (po 30min)", "60min", timeGroup, false));

        Button nextBtn = new Button("Dalej do wyboru gracza");
        nextBtn.setMaxWidth(Double.MAX_VALUE);
        nextBtn.getStyleClass().add("btn-main");
        nextBtn.setOnAction(e -> {
            String color = (String) colorGroup.getSelectedToggle().getUserData();
            String time = (String) timeGroup.getSelectedToggle().getUserData();
            stage.setScene(new OnlinePlayersView().createScene(stage, color, time));
        });

        // Region wypychający przycisk "Dalej" na sam dół, aby panele były równe
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        return buildPanel(title, colorSection, timeSection, spacer, nextBtn);
    }

    // --- Helpery (Clean Code) ---

    private VBox buildPanel(Node... children) {
        VBox panel = new VBox(20, children);
        panel.setPadding(new Insets(25));
        panel.getStyleClass().add("modern-panel");
        
        // Zapewnienie rozciągania panelu w poziomie i pionie
        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        
        return panel;
    }

    private VBox createOptionGroup(String labelText, RadioButton... radios) {
        VBox group = new VBox(10, new Label(labelText));
        group.getChildren().addAll(radios);
        group.getStyleClass().add("modern-option-group");
        return group;
    }

    private RadioButton createRadio(String text, String data, ToggleGroup group, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setUserData(data);
        rb.setToggleGroup(group);
        rb.setSelected(selected);
        return rb;
    }
}