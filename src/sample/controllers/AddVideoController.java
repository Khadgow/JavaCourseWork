package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.ServerController;

import java.io.File;
import java.io.IOException;

public class AddVideoController {

    @FXML
    private TextField titleInput;

    @FXML
    private Button openFile;

    @FXML
    private Label previousText;

    @FXML
    private Button submitButton;

    @FXML
    private Label errorText;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private File videoFile = null;
    private ServerController serverController;

    public void setServerController (ServerController serverController) {
        this.serverController = serverController;
    }

    @FXML
    void initialize() {
        submitButton.setOnAction(actionEvent -> {
            String title = titleInput.getText();
            if(title.trim().equals("")){
                errorText.setText("Название видео не должно быть пустым");
            } else if(videoFile == null) {
                errorText.setText("Нужно выбрать файл");
            } else {
                try {
                    serverController.sendFile(videoFile, title);
//                    int result =
//                    if(result == 0){
//                        errorText.setText("Название видео уже занято");
//                    } else {
//                        //do something
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            actionEvent.consume();
        });
        openFile.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a .mp4 file", "*.mp4");
            fileChooser.getExtensionFilters().add(filter);
            videoFile = file;
            actionEvent.consume();
        });

        previousText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/videoListScene.fxml"));
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            VideoListController videoListController = loader.getController();
            try {
                videoListController.setServerController(serverController);
            } catch (IOException e) {
                e.printStackTrace();
            }
            videoListController.setUserType("admin");
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            String css = this.getClass().getResource("../application.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
            event.consume();
        });
    }
}
