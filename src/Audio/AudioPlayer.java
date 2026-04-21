package Audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

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
    
    public void playEffect(String effectName) {
        try {
            Clip effectClip = getClip(effectName);
            if (effectClip != null) {
                effectClip.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing effect: " + effectName + " - " + e.getMessage());
        }
    }

    /**
     * this is heavy for a game.. (wmplayer.exe)
     * */
    public void playMP3Effect(String mp3Path) {
        // Use Windows Media Player with /play and /close flags to play without opening visible window
        try {
            File audio = new File(mp3Path);
            if (!audio.exists()) {
                System.err.println("MP3 file not found: " + mp3Path);
                return;
            }

            // Use Windows Media Player with flags to play in background
            ProcessBuilder pb = new ProcessBuilder(
                "C:\\Program Files\\Windows Media Player\\wmplayer.exe",
                "/play",
                "/close",
                audio.getAbsolutePath()
            );

            // Start the process in background
            Process audioProcess = pb.start();

            // Monitor the process in a separate thread
            new Thread(() -> {
                try {
                    audioProcess.waitFor();
                } catch (InterruptedException e) {
                    audioProcess.destroy();
                }
            }).start();

            System.out.println("Playing MP3 effect: " + mp3Path);

        } catch (Exception e) {
            System.err.println("MP3 playback failed: " + e.getMessage());
            // Try PowerShell fallback
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-Command",
                    "(New-Object Media.SoundPlayer '" + mp3Path.replace("\\", "\\\\") + "').PlaySync();"
                );
                pb.start();
                System.out.println("Playing MP3 with PowerShell fallback");
            } catch (Exception e2) {
                System.err.println("All MP3 playback methods failed: " + e2.getMessage());
            }
        }
    }

    public void playSong(int song){
        if(songs[currentSongId].isActive())
            songs[currentSongId].stop();

        currentSongId = song;
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }
}
