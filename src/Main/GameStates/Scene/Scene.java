package Main.GameStates.Scene;

import java.awt.image.BufferedImage;

public class Scene {
    private String bgFilePath;
    private BufferedImage backgroundImage;
    private String dialogueText;

    public Scene(String bgFilePath, String dialogueText) {
        this.bgFilePath = bgFilePath;
        this.dialogueText = dialogueText;
    }

    public BufferedImage getBackgroundImage() {
        if (backgroundImage == null && bgFilePath != null) {
            backgroundImage = Utils.LoadSave.getSpriteAtlas(bgFilePath);
        }
        return backgroundImage;
    }

    public String getDialogueText() {
        return dialogueText;
    }

    public void flushMemory() {
        this.backgroundImage = null;
    }
}
