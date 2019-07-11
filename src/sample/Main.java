package sample;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Scene scene;
    private static Parent main;
    private static Parent intro;
    @Override
    public void start(Stage primaryStage) throws Exception {
         main = FXMLLoader.load(getClass().getResource("sample.fxml"));
        intro = FXMLLoader.load(getClass().getResource("intro.fxml"));
        scene = new Scene(intro);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void changeToMain(){
        scene.setRoot(main);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
