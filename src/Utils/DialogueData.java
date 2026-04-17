package Utils;

import java.util.HashMap;

import static Utils.Constants.NPCConstants.NINO_ID;

public class DialogueData {
    private static HashMap<Integer, String[]> npcDialogues = new HashMap<>();

    static {
        //200 = Nino
        npcDialogues.put(NINO_ID, new String[] {
                "Greetings, young traveler. Welcome to the Forest of the Broken Crown.",
                "The path ahead is dangerous, filled with vases to smash and spikes to avoid.",
                "Take care, and may the spirits guide your journey. - Niño"
        });

        //201 = someone else
        npcDialogues.put(201, new String[] {
                "whats up cuh!"
        });
    }

    //getter for dialogue but if there isn't any lines then.. gi ghost ka.. sorry na
    public static String[] getLinesFor(int id) {
        return npcDialogues.getOrDefault(id, new String[]{"... (The NPC remains silent)"});
    }
}
