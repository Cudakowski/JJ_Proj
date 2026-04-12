import javafx.application.Application;
import javafx.stage.Stage;

public class SzchyOnline extends Application {

    @Override
    public void start(Stage stage) {
        MenuView menu = new MenuView();
        stage.setScene(menu.createScene(stage));
        stage.setTitle("Szachy");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



