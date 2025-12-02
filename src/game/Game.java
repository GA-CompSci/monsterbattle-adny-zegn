package game;
import java.util.ArrayList;

import gui.MonsterBattleGUI;

/**
 * Game - YOUR monster battle game!
 * 
 * Build your game here. Look at GameDemo.java for examples.
 * 
 * Steps:
 * 1. Fill in setupGame() - create monsters, items, set health
 * 2. Fill in the action methods - what happens when player acts?
 * 3. Customize the game loop if you want
 * 4. Add your own helper methods
 * 
 * Run this file to play YOUR game
 */
public class Game {
    
    // The GUI (I had AI build most of this)
    private MonsterBattleGUI gui;

    // Game state - YOU manage these
    private ArrayList<Monster> monsters;
    private Monster lastHit; // The monster we last attacked - retaliates if not dead
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int playerMaxHealth;
    private int playerShield;
    private int playerDamage;
    private int playerSpeed;
    private int playerSpecialMeter;
    private int playerSpecialMax;
    private int characterClass;
    private double shieldPoints;
    private String playerStatus = "Normal";


    private String[] actionButtons = {"Attack (" + playerDamage + ")", 
                                      "Defend (" + playerShield + ")", 
                                      "Special (" + playerSpecialMeter + "/" + playerSpecialMax + ")",
                                      "Use Item"};

    /**
     * Main method - start YOUR game!
     */
    public static void main(String[] args) {
        Game game = new Game(); // it instantiates a copy of this file. We're not running static
        game.play(); // this extra step is unnecessary AI stuff
    }
    
    /**
     * Play the game!
     */
    public void play() {
        setupGame();
        gameLoop();
    }
    
    /**
     * Setup - create the GUI and initial game state
     * 
     * TODO: Customize this! How many monsters? What items? How much health?
     */
    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("Monster Battle - ANDY'S GAME");

        String[] specials = {"Immobilizer",
                             "Colossus",
                             "Predator"};

        // CHOOSE DIFFICULTY 
        int numMonsters = chooseDifficulty();
        int elitePosition = (int)(Math.random() * numMonsters); // Randomly generate the position our elite will be
        monsters = new ArrayList<>();
        for (int i = 0; i < numMonsters; i++) {
            if (i == elitePosition) {
                // ADD AN ELITE MONSTER
                int eliteIndex = (int)(Math.random() * specials.length);
                monsters.add(new Monster(specials[eliteIndex]));
            } else {
                monsters.add(new Monster());
            }
        }
        gui.updateMonsters(monsters);
        
        pickCharacterBuild();

        // TODO: Create starting items
        inventory = new ArrayList<>();
        // Add items here! Look at GameDemo.java for examples
        gui.updateInventory(inventory);
        
        // Welcome message
        gui.displayMessage("Battle Start! Have a gander.");
    }
    
    /**
     * Main game loop
     * 
     * This controls the flow: player turn → monster turn → check game over
     * You can modify this if you want!
     */
    private void gameLoop() {
        // Keep playing while monsters alive and player alive
        
        while (countLivingMonsters() > 0 && playerHealth > 0) {
            //RESET ACTION BUTTONS
            gui.setActionButtons(actionButtons);

            // PLAYER'S TURN
            if (playerStatus != null && playerStatus.equals("Immobilized") && Math.random() < 0.3) {
                gui.displayMessage(monsters.get(0).name() + " immobilizes you! You cannot move!");
                gui.pause(1500);
            } else {
                gui.displayMessage("Your turn! HP: " + playerHealth);
                int action = gui.waitForAction();  // Wait for button click (0-3)
                handlePlayerAction(action);
                gui.updateMonsters(monsters);
                gui.pause(500);
            }

            if (playerStatus != null && playerStatus.equals("Bleeding")) {
                heal(-0.05); // "heal" negative percentage points
                gui.updatePlayerHealth(playerHealth);
                gui.displayMessage("You are bleeding! you took " + (int)(0.05 * playerMaxHealth)  + " damage!");
                gui.pause(300); 
            }
            
            // MONSTER'S TURN (if any alive and player alive)
            if (countLivingMonsters() > 0 && playerHealth > 0) {
                monsterAttack();
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
        }
        
        // Game over!
        if (playerHealth <= 0) {
            gui.displayMessage("💀 DEFEAT! You have been defeated...");
        } else {
            gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
        }
    }
    
    /**
    * Let player choose difficulty (number of monsters) using the 4 buttons
    * This demonstrates using the GUI for menu choices!
    */
    private int chooseDifficulty() {
        // Set button labels to difficulty levels
        String[] difficulties = {"Easy (2-4)", "Medium (4-6)", "Hard (6-10)", "Extreme (10-15)"};
        gui.setActionButtons(difficulties);
        
        // Display choice prompt
        gui.displayMessage("---- CHOOSE DIFFICULTY ----");
        
        // Wait for player to click a button (0-3)
        int choice = gui.waitForAction();
        
        // Determine number of monsters based on choice
        int numMonsters = 0;
        switch (choice) {
            case 0: 
                numMonsters = (int)(Math.random() * (4-2+1)) + 2;
                break;
            case 1:
                numMonsters = (int)(Math.random() * (6-4+1)) + 4;
                break;
            case 2:
                numMonsters = (int)(Math.random() * (10-6+1)) + 6;
                break;
            case 3:
                numMonsters = (int)(Math.random() * (15-10+1)) + 10;
                break;
        }
        
        gui.displayMessage("Difficulty selected. You shall face " + numMonsters + " monsters! Good luck.");
        gui.pause(1500);
        
        return numMonsters;
    }
    
    /**
     * Handle player's action choice
     * 
     */
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack button
                attackMonster();
                break;
            case 1: // Defend button
                defend();
                break;
            case 2: // Special button
                special();
                break;
            case 3: // Use Item button
                useItem();
                break;
        }
    }
    
    /**
     * Let player pick their character build using the 4 buttons
     * This demonstrates using the GUI for menu choices!
     */
    private void pickCharacterBuild() {
        // Set button labels to character classes
        String[] characterClasses = {"Fish Slapper", "Turtle Master", "Algae Eater", "Dolphin Rider"};
        gui.setActionButtons(characterClasses);
        
        // Display choice prompt
        gui.displayMessage("---- PICK YOUR BUILD ----");
        
        // Wait for player to click a button (0-3)
        characterClass = gui.waitForAction();
        
        // Initialize default stats
        playerHealth = 100;
        playerMaxHealth = 100;
        playerDamage = 50;
        playerShield = 50;
        playerSpeed = 10;
        playerSpecialMeter = 0;
        playerSpecialMax = 10;
        
        // Customize stats based on character choice
        if (characterClass == 0) {
            // Fighter: high damage, low healing and shield
            gui.displayMessage("You chose Fish Slapper! Slap 'em up, but be careful...");
            playerShield -= (int)(Math.random() * 21) + 5;      // Reduce shield by 5-25
            playerSpeed = (int)(Math.random() * 6) + 5;         // Set speed to a range of 5-10
        } else if (characterClass == 1) {
            // Tank: high shield, low damage and speed
            gui.displayMessage("You chose Turtle Master! Slow and steady, can you win?");
            playerSpeed = (int)(Math.random() * 5) + 1;         // Set speed to a range of 1-5
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
        } else if (characterClass == 2) {
            // Healer: high healing, low damage and shield
            gui.displayMessage("You chose Algae Eater! Photosynthesize and devour your way to the top!");
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
            playerShield -= (int)(Math.random() * 21) + 5;      // Reduce shield by 5-25
            playerSpeed = (int)(Math.random() * 5) + 1;         // Set speed to a range of 3-8
        } else {
            // Ninja: high speed, low healing and health
            gui.displayMessage("You chose Dolphin Rider! Quick and to the point.");
            playerMaxHealth -= (int)(Math.random() * 21) + 5;      // Reduce max health by 5-25
            playerSpeed = (int)(Math.random() * 4) + 8;         // Set speed to a range of 7-11
        }
        
        // Pause to let player see their choice
        gui.setPlayerMaxHealth(playerMaxHealth);
        gui.updatePlayerHealth(playerMaxHealth);
        gui.pause(1500);
    }
   
    /**
     * Attack a monster
     */
    private void attackMonster() {

        Monster target = getFirstLivingMonster();
        lastHit = target;
        int damage = (int)(Math.random() * (playerDamage + 1));

        if (damage == 0) {
            // Critical miss - hurt yourself
            playerHealth -= 5;
            gui.displayMessage("A swing and a MISS! You hit yourself for 5 hp!");
            gui.updatePlayerHealth(playerHealth);
        } else if (damage >=  playerDamage) {
            // Critical Success
            gui.displayMessage("A critical hit! The monster was so intimidated it died on the spot!");
            target.takeDamage(target.health());
        } else {
            target.takeDamage(damage);
        }
        // Show which one we hit
        int index = monsters.indexOf(target);
        gui.highlightMonster(index);
        gui.pause(300);
        gui.highlightMonster(-1);
        gui.updateMonsters(monsters);
    }
    
    /**
     * Put up a shield that reduces damage
     */
    private void defend() {
        shieldPoints = playerShield;
        gui.displayMessage("Shield Up! Ready or not...");
    }
    
    /**
     * Use special, if possible
     */
    
    //TODO: specials
    private void special() {
        if (playerSpecialMeter >= playerSpecialMax) {
            switch (characterClass) {
                case (0): // Fish Slapper
                    gui.displayMessage("Tri-slap! ");
            }
        }
    }
    
    /**
     * Use an item from inventory
     */
    private void useItem() {
        if (inventory.isEmpty()) {
            gui.displayMessage("No items in inventory!");
            return;
        }
        
        // Use first item
        Item item = inventory.remove(0);
        gui.updateInventory(inventory);
        item.use();  // The item knows what to do!
    }
    
    private void monsterAttack() {
        // Create new ArrayList of monsters that will attack the player
        ArrayList<Monster> attackers = getSpeedyMonsters();
        if (lastHit != null && lastHit.health() > 0 && !attackers.contains(lastHit)) attackers.add(lastHit);

        for (Monster monster : attackers) {

            int damageTaken = (int)(Math.random() * monster.damage() + 1);

            // MANAGE SPECIALS
            if (monster.special().equals("Immobilizer") && playerStatus != "Immobilized" && damageTaken > 0) {
                playerStatus = "Immobilized";
                gui.displayMessage(monster.name() + " immobilizes you! 30% chance for actions to fail!");
                gui.pause(1000);
            } else if (monster.special().equals("Colossus")) {
                if (Math.random() < 0.3) {
                    gui.displayMessage(monster.name() + " heals itself for 5% of its health!");
                    monster.heal(0.05);
                    gui.pause(500);
                }
            } else if (monster.special().equals("Predator") && playerStatus != "Bleeding" && damageTaken > 0) {
                playerStatus = "Bleeding";
                gui.displayMessage(monster.name() + " cuts you deeply! You will take 5% of your max health every turn!");
                gui.pause(500);
            }

            // MANAGE SHIELD
            if (shieldPoints > 0) {
                double absorbance = Math.min(damageTaken, shieldPoints);
                damageTaken -= absorbance;
                shieldPoints -= absorbance;
                gui.displayMessage("You block for " + absorbance + " damage. You have " + shieldPoints + " shield left.");
                shieldPoints = 0;
            }

            if (damageTaken > 0) {
                playerHealth -= damageTaken;
                gui.displayMessage(monster.name() + " hits you for " + damageTaken + " damage!");
                gui.updatePlayerHealth(playerHealth);
            }
            
            int index = monsters.indexOf(monster);
            gui.highlightMonster(index);
            gui.pause(300);
            gui.highlightMonster(-1);
        }
    }
    
    // ==================== HELPER METHODS ====================
    // Add your own helper methods here!
    
    /**
     * Count how many monsters are still alive
     */
    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters) {
            if (m.health() > 0) count++;
        }
        return count;
    }
    
    /**
     * Get a random living monster
     */
    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.health() > 0) alive.add(m);
        }
        if (alive.isEmpty()) return null;
        return alive.get((int)(Math.random() * alive.size()));
    }

    /**
     * 
     * @return The first monster still living.
     */
    private Monster getFirstLivingMonster() {
        for (int i = 0; i < monsters.size(); i++) {
            if (monsters.get(i).health() > 0) return monsters.get(i);
        }
        return null;
    }
    
    /**
     * Finds all special monsters.
     * @return An ArrayList of Monsters with specials
     */
    private ArrayList<Monster> getSpecialMonsters() {
        ArrayList<Monster> elites = new ArrayList<Monster>();
        for (Monster monster : monsters) {
            if (monster.special() != null && monster.special() != "" && monster.health() > 0) {
                elites.add(monster);
            }
        }
        return elites;
    }

    /**
     * Finds all monsters with speed greater than the player's.
     * @return An ArrayList of Monsters meeting the above condition.
     */
    private ArrayList<Monster> getSpeedyMonsters() {
        ArrayList<Monster> result = new ArrayList<Monster>();
        for (Monster monster : monsters) {
            if (monster.speed() > playerSpeed && monster.health() > 0) {
                result.add(monster);
            }
        }
        return result;
    }

    public int playerMaxHealth() { return playerMaxHealth; }

    public void heal(int amountHealed) {
        playerHealth = Math.min(playerMaxHealth, playerHealth + amountHealed);
    }

    public void heal(double percent) {
        int amountHealed = (int)(percent * playerMaxHealth);
        playerHealth = Math.min(playerMaxHealth, playerHealth + amountHealed);

    }
}