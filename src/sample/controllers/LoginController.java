package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.ServerController;

import java.io.File;
import java.io.IOException;




public class LoginController {

    @FXML
    private Label registerText;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorText;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private ServerController serverController;

    public void login (ActionEvent event) throws IOException {

        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username.trim().equals("") || password.trim().equals("")){
            errorText.setText("Имя пользователя и пароль не должны быть пустыми");
        }
        else {
            String result = serverController.authorization(username, password);
            System.out.println("result " + result);
            if (!result.equals("")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/videoListScene.fxml"));
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                VideoListController videoListController = loader.getController();
                videoListController.setVideos(serverController, result);
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                String css = this.getClass().getResource("../application.css").toExternalForm();
                scene.getStylesheets().add(css);
                stage.setScene(scene);
                stage.show();
            } else {
                errorText.setText("Неверные имя пользователя или пароль");
            }
        }
    }

    public void setServerController (ServerController serverController) {
        this.serverController = serverController;
    }

    @FXML
    void initialize() {
        registerText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/registerScene.fxml"));
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RegisterController registerController = loader.getController();
            registerController.setServerController(serverController);
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            event.consume();
        });
    }
}
