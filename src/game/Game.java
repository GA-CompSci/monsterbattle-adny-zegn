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
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int playerShield;
    private int playerDamage;
    private int playerHeal;
    private int playerSpeed;

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

        // CHOOSE DIFFICULTY 
        int numMonsters = chooseDifficulty();
        monsters = new ArrayList<>();
        for (int i = 0; i < numMonsters; i++) {
            if (i == 0) {
                monsters.add(new Monster("Giant Squid"));
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
        
        // TODO: Customize button labels
        String[] buttons = {"Attack (" + playerDamage + ")", 
                            "Defend (" + playerShield + ")", 
                            "Heal (" + playerHeal +")", 
                            "Use Item"};
        gui.setActionButtons(buttons);
        
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
            
            // PLAYER'S TURN
            gui.displayMessage("Your turn! HP: " + playerHealth);
            int action = gui.waitForAction();  // Wait for button click (0-3)
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);
            
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
        
        gui.displayMessage("Difficulty selected. You shall face " + difficulties[choice] + " monsters! Good luck.");
        gui.pause(1500);
        
        return numMonsters;
    }
    
    /**
     * Handle player's action choice
     * 
     * TODO: What happens for each action?
     */
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack button
                attackMonster();
                break;
            case 1: // Defend button
                defend();
                break;
            case 2: // Heal button
                heal();
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
        int choice = gui.waitForAction();
        
        // Initialize default stats
        playerHealth = 100;
        playerDamage = 50;
        playerShield = 50;
        playerHeal = 50;
        playerSpeed = 10;
        
        // Customize stats based on character choice
        if (choice == 0) {
            // Fighter: high damage, low healing and shield
            gui.displayMessage("You chose Fish Slapper! Slap 'em up, but be careful...");
            playerShield -= (int)(Math.random() * 21) + 5;      // Reduce shield by 5-25
            playerHeal -= (int)(Math.random() * 21) + 5;        // Reduce heal by 5-25
        } else if (choice == 1) {
            // Tank: high shield, low damage and speed
            gui.displayMessage("You chose Turtle Master! Slow and steady, can you win?");
            playerSpeed -= (int)(Math.random() * 9) + 1;        // Reduce speed by 1-9
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
        } else if (choice == 2) {
            // Healer: high healing, low damage and shield
            gui.displayMessage("You chose Algae Eater! Photosynthesize and devour your way to the top!");
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
            playerShield -= (int)(Math.random() * 21) + 5;      // Reduce shield by 5-25
        } else {
            // Ninja: high speed, low healing and health
            gui.displayMessage("You chose Dolphin Rider! Quick and to the point.");
            playerHeal -= (int)(Math.random() * 21) + 5;        // Reduce heal by 5-25
            playerHealth -= (int)(Math.random() * 21) + 5;      // Reduce max health by 5-25
        }
        
        // Pause to let player see their choice
        gui.pause(1500);
    }
   
    /**
     * Attack a monster
     * 
     * TODO: How does attacking work in your game?
     * - How much damage?
     * - Which monster gets hit?
     * - Special effects?
     */
    private void attackMonster() {
        // TODO: Implement your attack!
        // Hint: Look at GameDemo.java for an example
        Monster target = getRandomLivingMonster();
        int damage = (int)(Math.random() * (playerDamage + 1));
        if (damage == 0) {
            // Critical miss - hurt yourself
            playerHealth -= 5;
            gui.displayMessage("A swing and a MISS! You hit yourself for 5 hp!");
            gui.updatePlayerHealth(damage);
        } else if (damage ==  playerDamage) {
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
     * Defend
     * 
     * TODO: What does defending do?
     * - Reduce damage?
     * - Block next attack?
     * - Something else?
     */
    private void defend() {
        // TODO: Implement your defend!
        
        gui.displayMessage("TODO: Implement defend!");
    }
    
    /**
     * Heal yourself
     * 
     * TODO: How does healing work?
     * - How much HP?
     * - Any limits?
     */
    private void heal() {
        // TODO: Implement your heal!
        
        gui.displayMessage("TODO: Implement heal!");
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
    
    /**
     * Monster attacks player
     * 
     * TODO: Customize how monsters attack!
     * - How much damage?
     * - Which monster attacks?
     * - Special abilities?
     */
    private void monsterAttack() {
        // TODO: Implement monster attacks!
        // Hint: Look at GameDemo.java for an example
        
        gui.displayMessage("TODO: Implement monster attack!");
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
     * Finds all special monsters.
     * @return An ArrayList of Monsters with specials
     */
    private ArrayList<Monster> getSpecialMonsters() {
        ArrayList<Monster> elites = new ArrayList<Monster>();
        for (Monster monster : monsters) {
            if (monster.special() != null && !monster.special().equals("") && monster.health() > 0) {
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

    // TODO: Add more helper methods as you need them!
    // Examples:
    // - Method to find the strongest monster
    // - Method to check if player has a specific item
    // - Method to add special effects
    // - etc.
}