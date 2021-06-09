package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.ServerController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class VideoListController {


    @FXML
    private AnchorPane anchorPane;

    private Stage stage;
    private Scene scene;
    private Parent root;



    public void setVideos (ServerController serverController, String userType) throws IOException {
        anchorPane.getChildren().clear();

        File imageFile = new File("C:\\Users\\Khadgow\\IdeaProjects\\VideoNews\\src\\Images\\icon.png");
        String localUrl = imageFile.toURI().toURL().toString();
        Image image = new Image(localUrl);
        ImageView icon = new ImageView();

        icon.setImage(image);
        icon.setLayoutX(440);
        icon.setLayoutY(26);
        icon.setFitWidth(150);
        icon.setFitHeight(150);
        anchorPane.getChildren().add(icon);
        String[] allVideos = serverController.getAllVideos();
        for (int i = 0; i< allVideos.length/2; i++) {
            Label label = new Label(allVideos[i*2]);

            label.setLayoutX(300);
            label.setLayoutY(i*50+250);
            int finalI = i;
            String[] finalAllVideos = allVideos;
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/videoScene.fxml"));
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                VideoController videoController = loader.getController();
                videoController.setUserType(userType);
                videoController.setServerController(serverController);
                try {
                    String path = serverController.getFile(finalAllVideos[finalI *2]);
                    videoController.setVideoPath(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                event.consume();
            });
            String[] finalAllVideos1 = allVideos;
            if(userType.equals("admin")){
            Button deleteButton = new Button("Удалить");
            deleteButton.setStyle("-fx-background-color: #fa2222; -fx-text-fill: white; -fx-border-radius: 1px;");
            deleteButton.setLayoutX(600);
            deleteButton.setLayoutY(i*50+250);
            deleteButton.setOnAction(actionEvent -> {
                try {
                    int result = serverController.deleteVideo(finalAllVideos1[finalI*2]);
                    if(result == 1) {
                        setVideos(serverController, userType);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
                anchorPane.getChildren().add(deleteButton);
            }
            anchorPane.getChildren().add(label);

        }
        if(userType.equals("admin")){
            Button addVideo = new Button("Добавить видео");
            addVideo.setLayoutX(700);
            addVideo.setLayoutY(50);
            addVideo.setStyle("-fx-background-color: #3EA6FF; -fx-text-fill: white; -fx-border-radius: 1px;");
            addVideo.setOnAction(actionEvent -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/addVideoScene.fxml"));
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AddVideoController addVideoController = loader.getController();
                addVideoController.setServerController(serverController);
                stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                actionEvent.consume();
            });
            anchorPane.getChildren().add(addVideo);
        }
    }


}
