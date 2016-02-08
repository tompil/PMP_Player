package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import music_file_pack.MusicFile;

public class Main extends Application{
    MusicFile musicFile;

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("DogePlayer");
            primaryStage.getIcons().add(new Image("sample/app.png"));
            MediaController mediaController = new MediaController(musicFile);
            Pane controlPane = mediaController.getControlPane();
            Scene scene = new Scene(controlPane, 400, 120);
            scene.getStylesheets().add("stylesheet.css");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch(MediaController.MoreThanOneInstance me) {
            System.out.println(me);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
