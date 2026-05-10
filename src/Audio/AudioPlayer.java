package Audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class AudioPlayer {

    public static int MENU_1      = 0;
    public static int CUTSCENE    = 1;
    public static int WORLD1      = 2;
    public static int WORLD1_BOSS = 3;
    public static int WORLD2      = 4;
    public static int WORLD2_BOSS = 5;
    public static int WORLD3      = 6;
    public static int WORLD3_BOSS = 7;



    public static int CLICK = 0;
    public static int HOVER = 1;

    public static int CONSUME_POTION = 2;
    public static int VASE_BREAK = 3;

    public static int KAEL_HURT = 4;
    public static int EMB_HURT = 5;
    public static int SYL_HURT = 6;

    public static int KAEL_SKILL = 7;
    public static int EMB_SKILL = 8;
    public static int SYL_SKILL = 9;

    private Clip[] songs, effects;
    private int currentSongId;
    private float volume = 1f;

    public AudioPlayer(){
        loadSongs();
        loadEffects();
        setVolume(volume);
        playSong(MENU_1);
    }

    private void loadSongs(){
        String[] names = {
                "Bonfires Of Yesterday",  // 0 - main menu
                "Bonfires of Yesterday",               // 1 - cutscene
                "Bonfires of Yesterday",                 // 2 - world1 ost
                "world1bossOST",            // 3 - world1_boss ost
                "world2OST",                 // 4 - world2 ost
                "world2bossOST",            // 5 - world2_boss ost
                "world3OST",                 // 6 - world3 ost
                "world3bossOST"             // 7 - world3_boss ost
        };
        songs = new Clip[names.length];
        for(int i = 0; i < songs.length; i++)
            songs[i] = getClip(names[i]);
    }

    private void loadEffects() {
        String[] effectNames = {"button_click", "button_hover", "consume_potion", "vase_break",
                                "kaelthorn_hurt", "embjorn_hurt", "sylvara_hurt",
                                "kaelthorn_jump", "embjorn_dash", "sylvara_doublejump"};
        effects = new Clip[effectNames.length];

        for(int i = 0; i < effects.length; i++) {
            effects[i] = getClip(effectNames[i]);
        }
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
        if (song == currentSongId) return; // already playing this track

        if(songs[currentSongId].isActive())
            songs[currentSongId].stop();

        currentSongId = song;
        songs[currentSongId].setFramePosition(0);
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void playEffect(int effectID) {
        if (effectID < 0 || effectID >= effects.length) return;

        if (effects[effectID].isRunning()) {
            effects[effectID].stop();
        }

        effects[effectID].setFramePosition(0);
        effects[effectID].start();
    }

    // volume control

    public void setVolume(float volume) {
        this.volume = volume;
        updateSongVolume();
        updateEffectsVolume();
    }

    public float getVolume() { return volume; }

    private void updateSongVolume() {
        for (Clip c : songs) {
            applyVolumeToClip(c, volume);
        }
    }

    private void updateEffectsVolume() {
        for (Clip c : effects) {
            applyVolumeToClip(c, volume);
        }
    }

    private void applyVolumeToClip(Clip clip, float vol) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float dB;
            if (vol <= 0.01f) {
                // If the slider is at 0%, mute it entirely using the lowest possible Decibel value
                dB = gainControl.getMinimum();
            } else {
                // Convert linear volume (0.0 to 1.0) into logarithmic Decibels
                dB = 20f * (float) Math.log10(vol);

                dB = Math.max(dB, gainControl.getMinimum());
                dB = Math.min(dB, gainControl.getMaximum());
            }

            gainControl.setValue(dB);
        }

    }
}