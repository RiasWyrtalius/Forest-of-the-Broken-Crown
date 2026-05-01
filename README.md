# Forest of the Broken Crown

---

## Overview
**Forest of the Broken Crown** is a 2D side-scrolling action-platformer developed in **Java** using the **Swing** library. 

In a kingdom where a shattered crown seeps corruption into the roots of the world, you do not play as noble heroes. You play as the **Forsaken**: the banished, the deceiver, and the broken. Your goal is to trek through treacherous environments, defeat corrupted guardians, and decide the fate of the crown: will you destroy the blight or claim its power to become the next Dark Lord?

---

## Key Features
* **Scalable NPC System:** Interact with unique characters like **Chadon the Panda** and **Denbel the Sloth** using a data-driven system that supports typewriter-style dialogue.
* **Resource Economy:**
    * **Hearts:** Limited health that does **not** regenerate; players must rely on healing items found throughout battle.
    * **Mana:** Regenerates at a rate of **1 bottle every 5 seconds**, powering unique character abilities.
* **Dynamic Boss Encounters:** Battle massive entities like **Embryn the Rotfang Boar** in arenas where the terrain literally breaks beneath your feet.
  
## The Forsaken Heroes
These are not noble heroes, but individuals desperate for meaning in a world that cast them aside.

| Character | Mechanics | Sprite Preview |
| :--- | :--- | :--- |
| **Embjorn, The Snake** | **Venom Dash (Skill):** A quick horizontal dash to slip past hazards. <br> **Slither Step (Passive):** Moves faster on narrow platforms. | <img width="150" alt="Embjorn" src="https://github.com/user-attachments/assets/2f1ee59a-7fb1-4a5d-8463-ac79d02eb962" /> |
| **Kaelthorn, The Wolf Knight** | **Steady Step (Skill):** Controlled jumps make landing easier. <br> **Feral Mending (Passive):** A long vertical jump for reaching far platforms. | <img width="150" alt="Kaelthorn" src="https://github.com/user-attachments/assets/db10596f-a273-4f09-b80a-938f39f654ed" /> |
| **Sylvara, The Owl Mage** | **Gust (Skill):** A long-range magical wind attack. <br> **Flap and Float (Passive):** Double jumps and slow-descent gliding. | <img width="150" alt="Sylvara" src="https://github.com/user-attachments/assets/47827a27-399e-4c5a-a199-1fdd6f64cab2" /> |

---

## 🛠 Project Structure
The project is organized into a clean, modular package system:
* `Main.Core`: Entry points and the core game loop.
* `Entities`: Character logic for the Player, Bosses, and NPCs.
* `Objects`: Management of interactable items (Vases, Spikes) and NPC spawning.
* `Levels`: Tile mapping, background rendering, and level data handling.
* `Utils`: Essential helpers for collision detection, constants, and asset loading.
* `Main.UI`: Overlays for health, mana bottles, and typewriter dialogue boxes.

---

## Controls
| Action | Key / Input |
| :--- | :--- |
| **Move** | WASD or Arrow Keys |
| **Jump** | Space |
| **Interact** | Enter / Left Click |
| **Attack/Ability** | Character-specific binds |
| **Pause** | P / Esc |

---

## Technical Implementation Highlights
* **RGB-Based Map Spawning:** NPCs and objects are "painted" onto the level map using specific RGB values. The **Blue channel** acts as a type indicator, while the **Green channel** acts as a unique ID to link characters to their specific dialogue.
* **Typewriter Dialogue:** Text is revealed character-by-character using a timer-based `visibleTextIndex` system to create a retro RPG feel.
* **Hybrid Entity Logic:** NPCs extend the base `Entity` class to inherit idle animations and hitboxes, but are managed by the `ObjectManager` for efficient proximity checks and rendering.
* **Mana Regeneration Logic:** Implements a "Classic Battery" model where the system tracks time intervals based on the game's UPS (Updates Per Second) to grant 1 mana bottle every 600 ticks (at 120 UPS).

---

## Asset Credits
* **Engine:** Custom Java Swing Engine.
* **Art Style:** 2D Pixel Art.
* **Sound:** Integrated `AudioPlayer` for environmental music and combat effects.

> *"The crown is both their doom and their salvation..."*
