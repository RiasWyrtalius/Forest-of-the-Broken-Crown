package Utils;

import java.util.HashMap;

import static Utils.Constants.NPCConstants.*;

public class DialogueData {
    private static HashMap<Integer, String[]> npcDialogues = new HashMap<>();

    static {
        //200 = Nino
        npcDialogues.put(NINO_ID, new String[] {
                "Greetings, young traveler. Welcome to the Forest of the Broken Crown.",
                "The path ahead is dangerous, filled with vases to smash and spikes to avoid.",
                "Take care, and may the spirits guide your journey."
        });

        //201 = CHAD
        npcDialogues.put(CHAD_ID, new String[] {
                "World 1 Boss up ahead!"
        });

        //202 = CHARLZ
        npcDialogues.put(CHARLZ_ID, new String[] {
                "World 2 Boss up ahead!"
        });

        //203 = RILEY
        npcDialogues.put(RILEY_ID, new String[] {
                "World 3 Boss up ahead!"
        });

        //204 = DENVER
        npcDialogues.put(DENVER_ID, new String[] {
                "Congrats mana ang duwa"
        });
    }

    //getter for dialogue but if there isn't any lines then.. gi ghost ka.. sorry na
    public static String[] getLinesFor(int id) {
        return npcDialogues.getOrDefault(id, new String[]{"... (The NPC remains silent)"});
    }
}
