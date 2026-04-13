# VLCJ Integration for Death Screen Video Playback

## Overview

The death screen now uses VLCJ (VLC for Java) to play the actual "YOU DIED (HD).mp4" video file when the player dies, instead of showing a placeholder.

## Requirements

1. **VLC Media Player** - Must be installed on the system
   - Download from: https://www.videolan.org/
   - Install the 64-bit version

2. **VLCJ Library** - Already included in `lib/vlcj-4.8.2.jar`

## How It Works

1. When player dies, `GameState` changes to `DEATH`
2. Death screen automatically opens a full-screen video window
3. VLC plays the "YOU DIED (HD).mp4" video
4. When video finishes, the window closes and shows restart/main menu options

## Running the Game

Use the provided batch file:

```
run_with_vlcj.bat
```

Or run manually:

```bash
java -cp "bin;lib/vlcj-4.8.2.jar" Main.Main
```

## Troubleshooting

- **Video doesn't play**: Ensure VLC is installed and the video file exists at `src/Video/YOU DIED (HD).mp4`
- **Black screen**: VLC might not be found - check that VLC is in your system PATH
- **Errors**: Check console output for VLCJ initialization errors

## Fallback

If VLC is not available, the system will automatically fall back to the placeholder implementation.
