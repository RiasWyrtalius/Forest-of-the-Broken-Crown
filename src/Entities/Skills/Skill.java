package Entities.Skills;

import java.awt.*;

public interface Skill {
    void update();
    void render(Graphics g, int lvlOffset, int yLvlOffset);
    void activate();
    void deactivate();
}
