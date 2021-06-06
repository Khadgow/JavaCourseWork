package sample.controllers;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;


import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media;
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

    ServerController serverController;


    public void setServerController (ServerController serverController) {
        this.serverController = serverController;
    }

    @FXML
    void initialize() throws MalformedURLException {

        AtomicBoolean isPaused = new AtomicBoolean(false);
        AtomicBoolean isMuted = new AtomicBoolean(false);
        File mediaFile = new File("src/sample/assets/TEST.mp4");
        Media media = new Media(mediaFile.toURI().toURL().toString());
        //Media media = new Media("https://firebasestorage.googleapis.com/v0/b/videonews-ccf0b.appspot.com/o/Rick%20Astley%20-%20Never%20Gonna%20Give%20You%20Up%20(Video).mp4?alt=media");
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

        mediaPlayer.play();

    }
}
