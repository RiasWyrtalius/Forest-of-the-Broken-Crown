// - Created EnemyManager.java to handle multiple enemies.
// - Manages enemy updates, rendering, and spawning.
// - Added active check in update() and render() for performance.

package Main;

import Entities.Crabby;
import Entities.Enemy;
import java.awt.*;
import java.util.ArrayList;

public class EnemyManager {

    private Game game;
    private ArrayList<Enemy> enemies = new ArrayList<>();

    public EnemyManager(Game game) {
        this.game = game;
        addEnemies();
    }

    public void update(int[][] lvlData) {
        for (Enemy e : enemies) {
            if (e.isActive())
                e.update(lvlData);
        }
    }

    public void render(Graphics g) {
        for (Enemy e : enemies) {
            if (e.isActive())
                e.render(g);
        }
    }

    private void addEnemies() {
        enemies.add(new Crabby(200, 300));
        enemies.add(new Crabby(400, 300));
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}