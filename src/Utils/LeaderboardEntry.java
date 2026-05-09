package Utils;

import java.io.Serializable;

public class LeaderboardEntry implements Serializable {
    private String name;
    private String character;
    private String time;
    private int deaths;
    private long rawTicks; // sorting

    public LeaderboardEntry(String name, String character, String time, int deaths, long rawTicks) {
        this.name = name;
        this.character = character;
        this.time = time;
        this.deaths = deaths;
        this.rawTicks = rawTicks;
    }

    public String getName() { return name; }
    public String getCharacter() { return character; }
    public String getTime() { return time; }
    public int getDeaths() { return deaths; }
    public long getRawTicks() { return rawTicks; }
}