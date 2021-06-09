package sample.controllers;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.ServerController;

public class VideoController {

    @FXML
    private MediaView video;

    @FXML
    private ImageView pauseContinueButton;

    @FXML
    private ImageView volumeIcon;

    @FXML
    private Slider progressBar;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label backText;

    private ServerController serverController;

    private Stage stage;
    private Scene scene;
    private Parent root;
    MediaPlayer mediaPlayer;
    private String videoPath;
    private String userType;
    public void setServerController (ServerController serverController) {
        this.serverController = serverController;
    }
    public void setUserType (String userType) {
        this.userType = userType;
    }

    public void setVideoPath (String videoPath) throws MalformedURLException {
        this.videoPath = videoPath;
        AtomicBoolean isPaused = new AtomicBoolean(false);
        AtomicBoolean isMuted = new AtomicBoolean(false);
        File mediaFile = new File(videoPath);
        Media media = new Media(mediaFile.toURI().toURL().toString());
        File pauseIconFile = new File("src/Images/WhiteIcons/pause.png");
        File playIconFile = new File("src/Images/WhiteIcons/play.png");
        Image pauseIcon = new Image(pauseIconFile.toURI().toURL().toString());
        Image playIcon = new Image(playIconFile.toURI().toURL().toString());
        File volumeIconFile = new File("src/Images/WhiteIcons/volume.png");
        File muteIconFile = new File("src/Images/WhiteIcons/mute.png");
        Image volumeImage = new Image(volumeIconFile.toURI().toURL().toString());
        Image muteIcon = new Image(muteIconFile.toURI().toURL().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        video.setMediaPlayer(mediaPlayer);
        volumeSlider.setValue(mediaPlayer.getVolume()*100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue()/100);
                if(volumeSlider.getValue() == 0 && !isMuted.get()){
                    isMuted.set(true);
                    volumeIcon.setImage(muteIcon);

                } else if(isMuted.get()) {
                    isMuted.set(false);
                    volumeIcon.setImage(volumeImage);
                }
            }
        });

        mediaPlayer.currentTimeProperty().addListener(
                new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                        progressBar.setValue(newValue.toSeconds());
                    }
                }
        );

        progressBar.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                    }
                });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                javafx.util.Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
            }
        });

        pauseContinueButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(isPaused.get()) {
                mediaPlayer.play();
                isPaused.set(false);
                pauseContinueButton.setImage(pauseIcon);
            } else {
                mediaPlayer.pause();
                isPaused.set(true);
                pauseContinueButton.setImage(playIcon);
            }

            event.consume();
        });
        volumeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            if(isMuted.get()) {
                volumeSlider.setValue(100);
                isMuted.set(false);
                volumeIcon.setImage(volumeImage);
            } else {
                volumeSlider.setValue(0);
                isMuted.set(true);
                volumeIcon.setImage(muteIcon);
            }
            event.consume();
        });
        this.mediaPlayer = mediaPlayer;
        mediaPlayer.play();

    }

    @FXML
    void initialize() throws MalformedURLException {
        backText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            mediaPlayer.stop();
            File videoFile = new File(videoPath);
            videoFile.deleteOnExit();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/videoListScene.fxml"));
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            VideoListController videoListController = loader.getController();
            try {
                videoListController.setVideos(serverController, userType);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            event.consume();
        });

    }
}
