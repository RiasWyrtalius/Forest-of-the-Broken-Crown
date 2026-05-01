package Audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class AudioPlayer {

    public static int MENU_1 = 0;

    public static int CLICK = 0;
    public static int HOVER = 1;

    public static int CONSUME_POTION = 2;
    public static int VASE_BREAK = 3;

    private Clip[] songs, effects;
    private int currentSongId;
    private float volume = 1f;

    public AudioPlayer(){
        loadSongs();
        loadEffects();
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
            if (url == null) {
                File[] fallbackFiles = new File[] {
                    new File("Assets" + File.separator + "Audio" + File.separator + name + ".wav"),
                    new File("Forest-of-the-Broken-Crown" + File.separator + "Assets" + File.separator + "Audio" + File.separator + name + ".wav"),
                    new File(System.getProperty("user.dir") + File.separator + "Forest-of-the-Broken-Crown" + File.separator + "Assets" + File.separator + "Audio" + File.separator + name + ".wav")
                };
                for (File file : fallbackFiles) {
                    if (file.exists()) {
                        url = file.toURI().toURL();
                        break;
                    }
                }
            }

            if (url == null) {
                throw new RuntimeException("Audio file not found: /Audio/" + name + ".wav");
            }

            audio = AudioSystem.getAudioInputStream(url);
            Clip c = AudioSystem.getClip();
            c.open(audio);

            return c;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }

    public void playSong(int song){
        if(songs[currentSongId].isActive())
            songs[currentSongId].stop();

        currentSongId = song;
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void loadEffects() {
        String[] effectNames = {"button_click", "button_hover", "consume_potion", "vase_break"};
        effects = new Clip[effectNames.length];

        for(int i = 0; i < effects.length; i++) {
            effects[i] = getClip(effectNames[i]);
        }
    }

    public void playEffect(int effectID) {
        if (effectID < 0 || effectID >= effects.length) return;

        if (effects[effectID].isRunning()) {
            effects[effectID].stop();
        }

        effects[effectID].setFramePosition(0);
        effects[effectID].start();
    }
}
