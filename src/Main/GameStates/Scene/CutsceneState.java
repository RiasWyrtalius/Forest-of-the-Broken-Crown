package Main.GameStates.Scene;

import Main.Core.Game;
import Main.GameState;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Utils.LoadSave.*;

public class CutsceneState {
    private Game game;
    private Font customFont;
    private HashMap<String, List<Scene>> cutsceneDatabase;
    //sequence of cutscenes
    private List<Scene> scenes;
    private int currentSceneIndex = 0;

     //fade mechanics
    private float alpha = 0.0f;
    private boolean fadingIn = false;
    private boolean fadingOut = false;
    private final float FADE_SPEED = 0.02f;

    // Typewriter mechanics
    private String currentDisplayedText = "";
    private int charIndex = 0;
    private int textTick = 0;
    private final int TEXT_SPEED = 2;
    private boolean textFinished = false;

    private GameState nextState;

    public CutsceneState(Game game) {
        this.game = game;
        this.scenes = new ArrayList<>();
        this.cutsceneDatabase = new HashMap<>();

        loadAllCutscenes();
        customFont = LoadSave.getFont("Font/VCR.ttf").deriveFont(20f);
    }

    private void loadAllCutscenes() {
        List<Scene> intro = new ArrayList<>();
        intro.add(new Scene(Intro1, "The old kingdom is nothing but ash and memory. Deep within the dying woods lies the Broken Crown..."));
        intro.add(new Scene(Intro2, "They call us the Forsaken. Banished and betrayed, we march into the rot to face a choice."));
        intro.add(new Scene(Intro3, "Destroy the crown and end its blight... or claim its cursed power for ourselves."));
        cutsceneDatabase.put("INTRO", intro);

        List<Scene> outroGood = new ArrayList<>();
        outroGood.add(new Scene(GoodEnding1, "With a final, deafening crack, the crown shatters into dust. The dark pulse of the forest finally falls silent."));
        outroGood.add(new Scene(GoodEnding2, "The corruption recedes from root and stone. For the first time in centuries, light pierces through the canopy."));
        outroGood.add(new Scene(GoodEnding2, "We remain the Forsaken, and the world may never thank us... but we leave this forest free."));
        cutsceneDatabase.put("OUTRO_GOOD", outroGood);

        List<Scene> outroBad = new ArrayList<>();
        outroBad.add(new Scene(BadEnding, "The fragments knit together in my grip. The corruption doesn't burn anymore... it obeys."));
        outroBad.add(new Scene(BadEnding, "Why destroy a kingdom when you can rule the shadows? The forest kneels before its new master."));
        outroBad.add(new Scene(BadEnding, "Let the world look toward these woods and tremble. The age of the Broken Crown has only just begun."));
        cutsceneDatabase.put("OUTRO_BAD", outroBad);

        List<Scene> embjorn = new ArrayList<>();
        embjorn.add(new Scene(Emb1, "Before the shattering, my venom was a sacred mirror that showed the future. Now, it only shows the end of all things."));
        embjorn.add(new Scene(Emb2_3, "My kin banished me because they feared what I saw in my visions. They chose comforting ignorance over violent survival."));
        embjorn.add(new Scene(Emb2_3, "This forest is poisoned by the very relic they revere. I will tear the crown from its roots and force them to look upon its ugliness."));
        embjorn.addAll(intro);
        cutsceneDatabase.put("EMBJORN", embjorn);

        List<Scene> kaelthorn = new ArrayList<>();
        kaelthorn.add(new Scene(Kael1, "I swore my life to a noble king, only to watch him twist into a tyrant, hollowed out by the crown's dark whispers."));
        kaelthorn.add(new Scene(Kael2_3, "They stripped me of my title and cast me out as a traitor, but they could not strip me of my resolve."));
        kaelthorn.add(new Scene(Kael2_3, "This rusted armor is my penance. I failed to protect my liege from the curse, but I will not fail to end this blight."));
        kaelthorn.addAll(intro);
        cutsceneDatabase.put("KAELTHORN", kaelthorn);

        List<Scene> sylvara = new ArrayList<>();
        sylvara.add(new Scene(Syl1, "When the curse took root in my grove, my magic broke. My shame and fear physically manifested into the Witch that now guards the crown."));
        sylvara.add(new Scene(Syl2_3, "I was meant to protect the ancient runes. Instead, they became the very chains that bind my twisted reflection."));
        sylvara.add(new Scene(Syl2_3, "Defeating Sylthra is not just about saving the forest... it is about cutting away the piece of my soul I lost to the dark."));
        sylvara.addAll(intro);
        cutsceneDatabase.put("SYLVARA", sylvara);
    }

    public void draw(Graphics g) {
        if (scenes == null || scenes.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        Scene currentScene = scenes.get(currentSceneIndex);

        //fade
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        if (currentScene.getBackgroundImage() != null) {
            g.drawImage(currentScene.getBackgroundImage(), 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        }

        //dialogue box
        if (currentScene.getDialogueText() != null && !currentScene.getDialogueText().isEmpty() && alpha > 0.2f) {
            drawDialogueBox(g);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // skip
        g.setColor(Color.WHITE);
        g.setFont(customFont);
        g.drawString("Press ENTER | Left-Click to Skip", Game.GAME_WIDTH - 380, 30);
        g.drawString("Press ESC to skip cutscene", Game.GAME_WIDTH - 380, 50);
    }

    private void drawDialogueBox(Graphics g) {
        int boxWidth = Game.GAME_WIDTH - 200;
        int boxHeight = 150;
        int x = 100;
        int y = Game.GAME_HEIGHT - boxHeight - 40;

        // Semi-transparent black box
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x, y, boxWidth, boxHeight, 20, 20);

        // White Border
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, boxWidth, boxHeight, 20, 20);

        // Font
        g.setFont(customFont);
        g.setColor(Color.WHITE);

        // Text
        drawWrappedText(g, currentDisplayedText, x + 40, y + 60, boxWidth - 80);
    }

    public void startCutscene(String cutsceneID, GameState nextState) {
        if (!cutsceneDatabase.containsKey(cutsceneID)) {
            System.out.println("Error: Cutscene ID '" + cutsceneID + "' not found!");
            GameState.state = nextState;
            return;
        }

        this.scenes = cutsceneDatabase.get(cutsceneID);
        this.nextState = nextState;
        this.currentSceneIndex = 0;

        resetSceneVariables();
        fadingIn = true;
    }

    private void resetSceneVariables() {
        alpha = 0.0f;
        charIndex = 0;
        currentDisplayedText = "";
        textFinished = false;
        fadingIn = false;
        fadingOut = false;
    }

    public void update() {
        if (scenes == null || scenes.isEmpty()) return;

        if (fadingIn) {
            alpha += FADE_SPEED;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadingIn = false;
            }
        }
        else if (fadingOut) {
            alpha -= FADE_SPEED;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                nextScene();
            }
        }
        // typewriter
        else if (!textFinished) {
            textTick++;
            if (textTick >= TEXT_SPEED) {
                textTick = 0;
                String fullText = scenes.get(currentSceneIndex).getDialogueText();
                if (charIndex < fullText.length()) {
                    charIndex++;
                    currentDisplayedText = fullText.substring(0, charIndex);
                } else {
                    textFinished = true;
                }
            }
        }
    }

    private void nextScene() {
        currentSceneIndex++;
        if (currentSceneIndex >= scenes.size()) {
            endCutscene();
        } else {
            resetSceneVariables();
            fadingIn = true; // Start fading in the next scene
        }
    }

    private void endCutscene() {
        for (Scene scene : scenes) {
            scene.flushMemory();
        }

        scenes = null;
        GameState.state = nextState;
    }

    public void keyPressed(KeyEvent e) {
        if (scenes == null || scenes.isEmpty()) return;

        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
            // If currently typing, double-tap to instantly finish the text
            if (!textFinished && !fadingIn && !fadingOut) {
                currentDisplayedText = scenes.get(currentSceneIndex).getDialogueText();
                charIndex = currentDisplayedText.length();
                textFinished = true;
            }

            // If text is fully typed, start fading to the next scene
            else if (textFinished && !fadingOut) {
                fadingOut = true;
            }
        }

        //skip
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            endCutscene();
        }
    }

    public void mousePressed(MouseEvent e) {
        if (scenes == null || scenes.isEmpty()) return;

        //left click to skip
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (!textFinished && !fadingIn && !fadingOut) {
                currentDisplayedText = scenes.get(currentSceneIndex).getDialogueText();
                charIndex = currentDisplayedText.length();
                textFinished = true;
            } else if (textFinished && !fadingOut) {
                fadingOut = true;
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        mousePressed(e);
    }

    // Helper method to handle multiple lines
    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int curX = x;
        int curY = y;

        String[] words = text.split(" ");

        for (String word : words) {
            int wordWidth = fm.stringWidth(word + " ");

            // If the word exceeds the box width, move to the next line
            if (curX + wordWidth > x + maxWidth) {
                curX = x;
                curY += lineHeight;
            }

            g.drawString(word + " ", curX, curY);
            curX += wordWidth;
        }
    }
}
