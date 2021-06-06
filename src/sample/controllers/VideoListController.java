package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.ServerController;

import java.io.IOException;

public class VideoListController {


    @FXML
    private AnchorPane anchorPane;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private ServerController serverController;
    private String userType;

    public void setServerController (ServerController serverController) throws IOException {
        this.serverController = serverController;

        String[] allVideos = this.serverController.getAllVideos();
        for (int i = 0; i< allVideos.length/2; i++) {
            Label label = new Label(allVideos[i*2]);
            label.setLayoutX(500);
            label.setLayoutY(i*50+250);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/videoScene.fxml"));
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                event.consume();
            });
            anchorPane.getChildren().add(label);
        }
    }

    public void setUserType (String userType) {
        if(userType.equals("admin")){
            Button addVideo = new Button("Add video");
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

    @FXML
    void initialize() throws IOException {



    }
}
