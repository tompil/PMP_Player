package music_file_pack;

import javafx.beans.value.*;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import javafx.util.Duration;

public class MP3file extends MusicFile{

    private MediaPlayer player;
    private UserMediaFileInterface userObject;
    private String artist = "Unknown arthist";
    private String title = "Unknown title";
    public MP3file(String filename) {
        super(filename);
    }

    public void open() throws MusicFileException {
        if(player == null) {
            File musicFile = new File(filename);
            if(!musicFile.exists() || !musicFile.isFile()) {
                throw new MusicFileException("File " + musicFile.getName() + " doesn't exist!");
            }
            Media media = new Media(musicFile.toURI().toString());
            player = new MediaPlayer(media);
            getMetadata(media);
            setListeners();
        }
    }

    public void play() throws MusicFileException {
        if(player == null) {
            open();
        }
        player.play();
    }

    public void pause() {
        if(player != null) {
            player.pause();
        }
    }

    public void stop() {
        if( player != null) {
            player.seek(new Duration(0.0));
            player.stop();
        }
    }
    public boolean isOpened() {
        if(player != null) {
            return true;
        }
        return false;
    }
    public boolean isPlaying() {
        return MediaPlayer.Status.PLAYING == player.getStatus();
    }

    public boolean isPaused() {
        return MediaPlayer.Status.PAUSED == player.getStatus();
    }

    public boolean isReady() {
        return MediaPlayer.Status.READY == player.getStatus();
    }

    public boolean isStopped() {
        return MediaPlayer.Status.STOPPED == player.getStatus();
    }

    public void setPlaybackTime(double millisec) {
        player.seek(new Duration(millisec));
    }

    public double getDuration() {
        return player.getTotalDuration().toMillis();
    }

    public double getCurrentTime() {
        return player.getCurrentTime().toMillis();
    }

    public void setVolume(double volume) {
        if(player != null) {
            player.setVolume(volume);
        }
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public void close() {
        if( player != null) {
            player.dispose();
            player = null;
        }
    }

    public void finalize() {
        close();
    }

    public void setUserObject(UserMediaFileInterface obj) {
        userObject = obj;
    }

    private void setListeners() {
        if(player != null && userObject != null) {
            player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue observable, Duration oldValue, Duration newValue) {
                    userObject.updateTime();
                }
            });

            player.setOnPaused(new Runnable() {
               @Override
               public void run() {
                   userObject.PlayPauseStopAction();
               }
           });

            player.setOnPlaying(new Runnable() {
                @Override
                public void run() {
                    userObject.PlayPauseStopAction();
                }
            });

            player.setOnStopped(new Runnable() {
                @Override
                public void run() {
                    userObject.PlayPauseStopAction();
                }
            });

            player.setOnReady(new Runnable() {
                @Override
                public void run() {
                    userObject.PlayPauseStopAction();
                }
            });

            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    userObject.endOfPlayingAction();
                }
            });
        }
    }

    private void getMetadata(Media media) {
        ObservableMap<String,Object> meta_data=media.getMetadata();

        meta_data.addListener(new MapChangeListener<String,Object>(){
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {

                if(ch.wasAdded()){

                    String key=ch.getKey();
                    Object value=ch.getValueAdded();

                    switch(key){
                        case "artist":
                            artist = value.toString();
                            break;
                        case "title":
                            title = value.toString();
                            break;
                    }
                    if(userObject != null) {
                        userObject.updateInfo();
                    }
                }
            }

        });
    }
}
