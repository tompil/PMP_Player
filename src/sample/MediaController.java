package sample;

import java.io.File;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import music_file_pack.MP3file;
import music_file_pack.MusicFile;
import music_file_pack.MusicFileException;
import music_file_pack.UserMediaFileInterface;

public class MediaController implements UserMediaFileInterface {
    private MusicFile musicFile;
    private static boolean isClassCreated = false;

    private boolean fileChooserOpened = false;
    private File lastDirectory;

    private Text title;
    private Slider timeSlider;
    private boolean isSliderClicked = false;
    private Slider volumeSlider;
    private Label timeLabel;
    private Button PlayPauseButton;

    public class MoreThanOneInstance extends Exception {
        public MoreThanOneInstance() {
            super("Cannot create more than one instance of class MediaController()!");
        }
    }

    public MediaController() throws MoreThanOneInstance {
        if(!isClassCreated) {
            isClassCreated =true;
        }
        else {
            throw new MoreThanOneInstance();
        }
    }

    public MediaController(MusicFile musicFile) throws MoreThanOneInstance {
        if(!isClassCreated) {
            isClassCreated =true;
            this.musicFile = musicFile;
        }
        else {
            throw new MoreThanOneInstance();
        }
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    public Pane getControlPane() {
        timeSlider = getTimeSlider();
        volumeSlider = getVolumeSlider();
        BorderPane borderPane = new BorderPane();
        borderPane.setId("root");
        HBox bottomHBox = getHBox();

        timeLabel = getTimeLabel("00:00/00:00");
        Button openFileButton = getOpenFileButton();
        Button stopButton = getStopButton();
        PlayPauseButton = getPlayPauseButton();
        title = getText("..::DogePlayer::..");
        bottomHBox.getChildren().addAll(PlayPauseButton, stopButton, openFileButton, volumeSlider, timeLabel);


        borderPane.setTop(title);
        borderPane.setCenter(timeSlider);
        borderPane.setBottom(bottomHBox);
        return borderPane;
    }



    private HBox getHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        return hbox;
    }

    private Text getText(String text) {
        Text title = new Text(text);
        title.setId("title");
        title.setWrappingWidth(400);
        return title;
    }

    private Slider getTimeSlider() {
        Slider timeSlider = new Slider();
        timeSlider.setId("timeslider");
        timeSlider.setMaxWidth(390);
        timeSlider.setDisable(true);
        timeSlider.setMax(1.0);

        timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isSliderClicked = true;
                if(musicFile.isOpened()) {
                    musicFile.setPlaybackTime(musicFile.getDuration() * timeSlider.getValue());
                    updateTime();
                    if(!musicFile.isPlaying()) {
                        try {
                            musicFile.play();
                            Image playImage = new Image(getClass().getResourceAsStream("play.png"));
                            PlayPauseButton.setGraphic(new ImageView(playImage));
                        }
                        catch(Exception e) {

                        }
                    }
                }
                isSliderClicked = false;
            }

        });

        return timeSlider;
    }

    private Slider getVolumeSlider() {
        Slider volumeSlider = new Slider();
        volumeSlider.setId("volumeslider");
        volumeSlider.setMaxWidth(200);
        volumeSlider.setValue(100.0);
        volumeSlider.setMax(1.0);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging() && musicFile != null && musicFile.isOpened()) {
                    musicFile.setVolume(volumeSlider.getValue());
                }
            }
        });
        return volumeSlider;
    }

    private Label getTimeLabel(String defaultText) {
        Label timeLabel = new Label(defaultText);
        timeLabel.setPrefWidth(100);
        timeLabel.setId("timelabel");
        return timeLabel;
    }

    private Button getOpenFileButton() {
        Button openFileButton = new Button();
        Image fileImage = new Image(getClass().getResourceAsStream("open.png"));
        openFileButton.setGraphic(new ImageView(fileImage));
        openFileButton.setId("openButton");
        MediaController mc = this;
        openFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EventType eventType = event.getEventType();
                System.out.print(eventType);
//                if(eventType == MouseEvent.MOUSE_CLICKED) {
                    if (!fileChooserOpened) {
                        FileChooser fc = new FileChooser();
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp3 file", "*.mp3"));

                        if (lastDirectory != null) {
                            fc.setInitialDirectory(lastDirectory);
                        }

                        fileChooserOpened = true;
                        File file = fc.showOpenDialog(null);
                        fileChooserOpened = false;

                        if (file != null) {
                            if (musicFile != null) {
                                musicFile.close();
                            }
                            lastDirectory = file.getParentFile();

                            musicFile = new MP3file(file.getAbsolutePath());
                            musicFile.setUserObject(mc);

                            try {
                                musicFile.open();
                                title.setText(musicFile.getArtist() + " - " + musicFile.getTitle());
                                musicFile.setVolume(volumeSlider.getValue());
                                musicFile.play();
                            } catch (MusicFileException e) {
                                System.out.println(e.getStackTrace());
                            }
                        }

                    }
                }
           // }
        });

        openFileButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openFileButton.setStyle("-fx-background-color: #33cdff;");
            }
        });

        openFileButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openFileButton.setStyle("-fx-background-color: transparent;");
            }
        });

        return openFileButton;
    }

    private Button getPlayPauseButton() {
        Button playPauseButton = new Button();
        Image playImage = new Image(getClass().getResourceAsStream("play.png"));
        playPauseButton.setGraphic(new ImageView(playImage));
        playPauseButton.setId("playPauseButton");
        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if(musicFile != null && musicFile.isOpened()) {
                        if (musicFile.isReady() || musicFile.isPaused() || musicFile.isStopped()) {
                            musicFile.play();
                        }
                        else {
                            musicFile.pause();
                        }
                    }
                }
                catch(Exception exc) {
                    System.out.println(exc);
                }
            }
        });

        playPauseButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(musicFile != null && musicFile.isOpened()) {
                    playPauseButton.setStyle("-fx-background-color: #33cdff;");
                }
            }
        });

        playPauseButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(musicFile != null && musicFile.isOpened()) {
                    playPauseButton.setStyle("-fx-background-color: transparent;");
                }
            }
        });

        return playPauseButton;
    }

    private Button getStopButton() {
        Button stopButton = new Button();
        Image stopImage = new Image(getClass().getResourceAsStream("stop.png"));
        stopButton.setGraphic(new ImageView(stopImage));
        stopButton.setId("stopButton");
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if(musicFile != null)
                        musicFile.stop();
                }
                catch(Exception exc) {
                    System.out.println(exc);
                }
            }
        });

        stopButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(musicFile != null && musicFile.isOpened()) {
                    stopButton.setStyle("-fx-background-color: #33cdff;");
                }
            }
        });

        stopButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(musicFile != null && musicFile.isOpened()) {
                    stopButton.setStyle("-fx-background-color: transparent;");
                }
            }
        });

        return stopButton;
    }

    public void updateInfo() {
        if (musicFile != null && musicFile.isOpened()) {
            title.setText("..::" + musicFile.getArtist() + " - " + musicFile.getTitle() + "::..");
        }
    }

    public void PlayPauseStopAction() {
        if(musicFile.isOpened()) {
            if(musicFile.isPlaying()) {
                Image pauseImage = new Image(getClass().getResourceAsStream("pause.png"));
                PlayPauseButton.setGraphic(new ImageView(pauseImage));
            }

            if(musicFile.isReady() || musicFile.isPaused() || musicFile.isStopped()) {
                Image playImage = new Image(getClass().getResourceAsStream("play.png"));
                PlayPauseButton.setGraphic(new ImageView(playImage));
            }
        }
    }

    public void updateTime() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (musicFile != null && musicFile.isOpened()) {
                    double currentTime = musicFile.getCurrentTime();
                    double durationTime = musicFile.getDuration();
                    timeLabel.setText(getFormattedTime(currentTime, durationTime));
                    timeSlider.setDisable(durationTime <= 0.0);
                    if (!isSliderClicked && !timeSlider.isDisabled() ) {
                        timeSlider.setValue(currentTime / durationTime);
                    }
                }
            }
        });
    }

    public void endOfPlayingAction() {
        Image playImage = new Image(getClass().getResourceAsStream("play.png"));
        PlayPauseButton.setGraphic(new ImageView(playImage));
        musicFile.stop();
        timeSlider.setValue(0.0);
    }

    private String getFormattedTime(double elapsed, double total) {
        int elapsedMinutes = (int) Math.floor(elapsed / 60000);
        int totalMinutes = (int) Math.floor(total / 60000);
        int elapsedSeconds = ((int) Math.floor(elapsed / 1000)) % 60;
        int totalSeconds = ((int) Math.floor(total / 1000)) % 60;
        if(elapsedMinutes == totalMinutes && elapsedSeconds >= totalSeconds) {
            elapsedSeconds = totalSeconds;
        }
        return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, totalMinutes, totalSeconds);
    }

}
