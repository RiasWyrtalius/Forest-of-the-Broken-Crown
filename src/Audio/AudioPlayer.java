package Audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {

    public static int MENU_1 = 0;

    private Clip[] songs, effects;
    private int currentSongId;
    private float volume = 1f;

    public AudioPlayer(){
        loadSongs();
        playSong(MENU_1);
    }

    private void loadSongs(){
        String[] names = {"Bonfires of Yesterday"};
        songs = new Clip[names.length];
        for(int i = 0; i < songs.length; i++)
            songs[i] = getClip(names[i]);
    }

    private Clip getClip(String name) {
        URL url = getClass().getResource("/Audio/" + name + ".wav");
        AudioInputStream audio;

        try {
            audio = AudioSystem.getAudioInputStream(url);
            Clip c = AudioSystem.getClip();
            c.open(audio);

            return c;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }
    
    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void playSong(int song){
        if(songs[currentSongId].isActive())
            songs[currentSongId].stop();

        currentSongId = song;
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }
}
