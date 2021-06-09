package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.controllers.LoginController;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public ServerController serverController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        serverController = new ServerController("localhost", 3001);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/loginScene.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setServerController(serverController);
        Image icon = new Image("./images/icon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Video news");
        Scene scene = new Scene(root, 1000, 800);
        String css = this.getClass().getResource("./application.css").toExternalForm();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("EXIT");
            try {
                serverController.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File dir = new File("C:\\Users\\Khadgow\\IdeaProjects\\VideoNews\\src\\sample\\cache");
            for(File file: dir.listFiles())
                if (!file.isDirectory())
                    file.deleteOnExit();
        });
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
