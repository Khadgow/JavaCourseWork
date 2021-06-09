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

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label errorText;

    @FXML
    private Label loginText;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private ServerController serverController;

    public void register (ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        if(username.trim().equals("") || password.trim().equals("") || email.trim().equals("")){
            errorText.setText("Email, имя пользователя и пароль не должны быть пустыми");
        }
        int response = serverController.registration(username, password, email);
        if(response == 1){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/videoListScene.fxml"));
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            VideoListController videoListController = loader.getController();
            videoListController.setVideos(serverController, "user");
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            String css = this.getClass().getResource("../application.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        } else  {
            errorText.setText("Email или имя пользователя уже заняты");
        }

    }

    public void setServerController (ServerController serverController) {
        this.serverController = serverController;
    }

    @FXML
    void initialize() {
        loginText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/loginScene.fxml"));
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoginController loginController = loader.getController();
            loginController.setServerController(serverController);
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            event.consume();
        });
    }

}
