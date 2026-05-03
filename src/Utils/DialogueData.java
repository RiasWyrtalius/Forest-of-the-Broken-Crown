package Utils;

import java.util.HashMap;

import static Utils.Constants.NPCConstants.*;

public class DialogueData {
    private static HashMap<Integer, String[]> npcDialogues = new HashMap<>();

    static {
        //200 = Nino
        npcDialogues.put(NINO_ID, new String[] {
                "Greetings, traveler. Another soul cast out by the sun, seeking refuge in the shade of the Broken Crown?",
                "The path ahead is jagged. Smashing these vases might yield a scrap of life, but mind the spikes-they crave blood as much as the roots do.",
                "Whether you seek to mend the crown or wear it, the forest cares not. It only waits to bury you.",
                "Keep your wits, little one. May the spirits guide your journey... or at least make it interesting"
        });

        //201 = CHAD
        npcDialogues.put(CHAD_ID, new String[] {
                "Wait! Shhh! Do you hear that? The rhythmic... squelching? That’s the Rotfang Boar, Embryn.",
                "I was only trying to map the Den of Decay, but that beast turned my compass into a chew toy!",
                "If you’re going forward, watch the platforms. They’re as rotten as the Boar’s tusks. World 1's master is just up ahead!",
                "I’m staying right here. Denbel says he's 'guarding' me, but I think he’s just asleep."
        });

        //202 = CHARLZ
        npcDialogues.put(CHARLZ_ID, new String[] {
                "Careful now. The air is getting thin and the floor is getting... melty.",
                "The Ashbound Titan Kaelor waits in the furnace ahead. He was stone once; now he’s just rage and embers.",
                "Don't stand still for too long, or you'll bake into the floorboards. The World 2 Boss is just beyond this vent!",
                "If you see a rune crystal, hit it. It’s the only thing that cools that monster’s temper."
        });

        //203 = RILEY
        npcDialogues.put(RILEY_ID, new String[] {
                "Don't look at me like that. My brothers dared me to climb to the Crown’s Heart. I... I underestimated the climb.",
                "The Witch, Sylthra, is just ahead. She’s not like the others—she plays with the very ground you stand on.",
                "She’s got runes that’ll lock the platforms right under your feet. You've got to be faster than a mountain goat to survive this.",
                "World 3 Boss is right through those gates. Good luck—you're going to need it more than I need a new pair of horns."
        });

        //204 = DENVER
        npcDialogues.put(DENVER_ID, new String[] {
                "D-o-n-'t... l-o-o-k... u-n-d-e-r... t-h-e... s-t-i-t-c-h-e-s...",
                "I-t... g-e-t-s... s-o... l-o-n-e-l-y... i-n... t-h-i-s... f-a-b-r-i-c...",
                "Y-o-u... f-i-n-i-s-h-e-d... t-h-e... g-a-m-e... b-e-f-o-r-e... I... c-o-u-l-d... f-i-n-i-s-h... y-o-u.",
                "W-a-n-t... t-o... s-e-e... w-h-a-t's... i-n-s-i-d-e? N-o... b-e-t-t-e-r... n-o-t."
        });
    }

    //getter for dialogue but if there isn't any lines then.. gi ghost ka.. sorry na
    public static String[] getLinesFor(int id) {
        return npcDialogues.getOrDefault(id, new String[]{"... (The NPC remains silent)"});
    }
}
