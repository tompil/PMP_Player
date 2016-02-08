package music_file_pack;

abstract public class MusicFile {
    protected String filename;

    public MusicFile(String filename) {
        this.filename = filename;
    }

    abstract public void open() throws MusicFileException;
    abstract public void play() throws MusicFileException;
    abstract public void pause();
    abstract public void stop();
    abstract public boolean isOpened();
    abstract public boolean isPlaying();
    abstract public boolean isPaused();
    abstract public boolean isReady();
    abstract public boolean isStopped();
    abstract public void setPlaybackTime(double millis);
    abstract public void setVolume(double volume);
    abstract public double getDuration();
    abstract public double getCurrentTime();
    abstract public String getArtist();
    abstract public String getTitle();
    abstract public void close();
    abstract public void finalize();
    abstract public void setUserObject(UserMediaFileInterface obj);
}
