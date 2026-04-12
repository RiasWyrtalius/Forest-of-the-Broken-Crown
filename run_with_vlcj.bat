@echo off
echo Starting Forest of the Broken Crown with VLCJ support...
echo.
echo Note: VLC must be installed for video playback to work.
echo If video doesn't play, install VLC from: https://www.videolan.org/
echo.
cd /d "%~dp0"
java -cp "bin;lib/vlcj-4.8.2.jar" Main.Main
pause