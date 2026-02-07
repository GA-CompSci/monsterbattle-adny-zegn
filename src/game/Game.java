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
    private int elitePosition;
    private ArrayList<Item> inventory;

    // PLAYER ATTRIBUTES
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

    private boolean hasUsedItem = false;
    private boolean playerTurn = false;

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
     */
    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("Monster Battle - UNDER THE SEA");

        String[] specials = 
        { "Immobilizer",
          "Colossus",
          "Predator"
        };

        // CHOOSE DIFFICULTY 
        int numMonsters = chooseDifficulty();
        elitePosition = (int)(Math.random() * numMonsters); // Randomly generate the position our elite will be
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

        inventory = new ArrayList<>();
        // Add items here! Look at GameDemo.java for examples
        createTonic(50);
        createBomb(20);
        createAdrenaline(5, 1);
        createPocketSand(5, 1);
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
            // RESET BUTTONS AND MONSTERTURN
            resetActionButtons();

            // GAME EVENTS - ONLY HAPPEN ONCE PER TURN
            if (!playerTurn) {
                if (playerStatus.equals("Immobilized") && Math.random() < 0.3) {
                    gui.displayMessage(monsters.get(elitePosition).name() + " immobilizes you! You cannot move!");
                    hasUsedItem = false;
                    playerTurn = false;
                    gui.pause(1500);
                } else  {
                    playerTurn = true;
                    incrementSpecialMeter(characterClass);
                }
                if (playerStatus != null && playerStatus.equals("Bleeding")) {
                    heal(-0.05); // "heal" negative percentage points
                    gui.updatePlayerHealth(playerHealth);
                    gui.displayMessage("You are bleeding! you took " + (int)(0.05 * playerMaxHealth)  + " damage!");
                    gui.pause(300); 
                }
            }

            // PLAYER TURN
            if (playerTurn) {
                gui.displayMessage("Your turn! HP: " + playerHealth);
                int action = gui.waitForAction();  // Wait for button click (0-3)
                handlePlayerAction(action);
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
            
            // MONSTER'S TURN (if any alive and player alive)
            if (!playerTurn && countLivingMonsters() > 0 && playerHealth > 0) {
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
                if (!hasUsedItem) useItem();
                else gui.displayMessage("Only 1 item per turn!");
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
        playerSpecialMax = 8;
        
        // Customize stats based on character choice
        if (characterClass == 0) {
            // Fish Slapper: high damage, low shield
            gui.displayMessage("You chose Fish Slapper! Slap 'em up, but be careful...");
            playerShield -= (int)(Math.random() * 11) + 15;     // Reduce shield by 15-25
            playerSpeed = (int)(Math.random() * 5) + 5;         // Set speed to a range of 4-7
        } else if (characterClass == 1) {
            // Turtle Master: high shield, low damage and speed
            gui.displayMessage("You chose Turtle Master! Slow and steady, can you win?");
            playerMaxHealth += (int)(Math.random() * 11) + 15;  // Increase max health by 15-25 
            playerSpeed = (int)(Math.random() * 3) + 1;         // Set speed to a range of 1-3
            playerDamage -= (int)(Math.random() * 11) + 15;     // Reduce damage by 15-25
        } else if (characterClass == 2) {
            // Algae Eater: high health, low damage, variable speed
            gui.displayMessage("You chose Algae Eater! Photosynthesize and devour your way to the top!");
            playerMaxHealth += (int)(Math.random() * 26) + 25;  // Increase max health by 25-50
            playerDamage -= (int)(Math.random() * 11) + 15;     // Reduce damage by 15-25
            playerSpeed = (int)(Math.random() * 5) + 4;         // Set speed to a range of 3-6
        } else {
            // Dolphin Rider: high speed, low health and shield
            gui.displayMessage("You chose Dolphin Rider! Quick and to the point.");
            playerMaxHealth -= (int)(Math.random() * 11) + 25;  // Reduce max health by 25-35
            playerShield -= (int)(Math.random() * 11) + 15;     // Reduce shield by 15-25
            playerSpeed = (int)(Math.random() * 5) + 7;         // Set speed to a range of 6-9
        }
        
        // Pause to let player see their choice
        playerHealth = playerMaxHealth;
        gui.setPlayerMaxHealth(playerMaxHealth);
        gui.updatePlayerHealth(playerMaxHealth);
        gui.setPlayerSpeed(playerSpeed);
        gui.pause(1500);
    }
   
    /**
     * Attack a monster
     */
    private void attackMonster() {
        Monster target = getFirstLivingMonster();
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

        // See if we get an item
        if (inventory.size() < 4 && target.health() <= 0) {
            createRandomItem();
            gui.displayMessage("You got a(n) " + inventory.getLast().getName() + "!");
            gui.pause(500);
        } 
        hasUsedItem = false;
        playerTurn = false;
        incrementSpecialMeter(1);
    }

    /**
     * Attack a certain monster with a certain amount of damage
     * Must be above 0
     */
    private void attackMonster(Monster monster, int damage) {
        Monster target = monster;
        target.takeDamage(damage);

        // Show which one we hit
        int index = monsters.indexOf(target);
        gui.highlightMonster(index);
        gui.pause(300);
        gui.highlightMonster(-1);
        gui.updateMonsters(monsters);

        if (inventory.size() < 4 && monster.health() <= 0) {
            createRandomItem();
            gui.displayMessage("You got a(n) " + inventory.getLast().getName() + "!");
            gui.pause(500);
        }
    }

    /**
     * Put up a shield that reduces damage
     */
    private void defend() {
        shieldPoints = playerShield;
        gui.displayMessage("Shield Up! Ready or not...");
        hasUsedItem = false;
        playerTurn = false;
    }
    
    /**
     * Use special, if possible
     */
    private void special() {
        if (playerSpecialMeter >= playerSpecialMax) {
            switch (characterClass) {
                case (0): // Fish Slapper - Tri-Slap
                    gui.displayMessage("Tri-slap!");
                    gui.pause(500);
                    gui.displayMessage("SLAP!");
                    attackMonster(getFirstLivingMonster(), (int)(playerDamage * 0.60));
                    gui.displayMessage( "SLAP!");
                    attackMonster(getFirstLivingMonster(), (int)(playerDamage * 0.80));
                    gui.displayMessage("SLAP!");
                    attackMonster(getFirstLivingMonster(), (int)(playerDamage));
                    break;
                case (1): // Turtle Master - Shell Spikes
                    gui.displayMessage("Shell Spikes!");
                    playerStatus = "Shelled";
                    shieldPoints = 100;
                    break;
                case (2): // Algae Eater - Overgrow
                    gui.displayMessage("Overgrow!");
                    playerHealth = playerMaxHealth;
                    gui.updatePlayerHealth(playerHealth);
                    gui.pause(300);
                    gui.displayMessage("Status ailments cleared!");
                    playerStatus = "Normal";
                    break;
                default: // Dolphin Rider - Accelerate
                    gui.displayMessage("Accelerate!");
                    gui.pause(300);
                    playerSpeed++;
                    gui.displayMessage("Speed UP!");
                    gui.setPlayerSpeed(playerSpeed);
                    gui.pause(300);
                    playerDamage += 10;
                    gui.displayMessage("Damage UP!");
            }
            playerSpecialMeter = 0;
            hasUsedItem = false;
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

        // DISPLAY ITEMS IN INVENTORY - ALLOW PLAYER CHOICE
        String[] items = new String[4];
        for (int i = 0; i < 4; i++) {
            if (i < inventory.size()) items[i] = inventory.get(i).getName();
            else items[i] = "Empty";
        }

        gui.setActionButtons(items);
        int selection = gui.waitForAction();
        while (selection >= inventory.size()) {
            gui.setActionButtons(items);
            selection = gui.waitForAction();
        }
        Item item = inventory.remove(selection);
        gui.updateInventory(inventory);
        item.use();  // The item knows what to do!
        hasUsedItem = true;
    }
    
    private void monsterAttack() {
        // Create new ArrayList of monsters that will attack the player
        ArrayList<Monster> attackers = getSpeedyMonsters();
        attackers.add(getFirstLivingMonster());

        for (Monster monster : attackers) {

            int damageTaken = (int)(Math.random() * monster.damage() + 1);

            // MANAGE SHIELD
            if (shieldPoints > 0) {
                double absorbance = Math.min(damageTaken, shieldPoints);
                damageTaken -= absorbance;
                shieldPoints -= absorbance;
                gui.displayMessage("You block for " + absorbance + " damage. You have " + shieldPoints + " shield left.");
                if (playerStatus.equals("Shelled")) {
                    gui.displayMessage(monster.name() + " suffers thorns damage!");
                    attackMonster(monster, (int)(Math.random() * playerDamage * 0.4) + 5);
                }
            }

            if (damageTaken > 0) {
                playerHealth -= damageTaken;
                gui.displayMessage(monster.name() + " hits you for " + damageTaken + " damage!");
                gui.updatePlayerHealth(playerHealth);
            }

            int index = monsters.indexOf(monster);
            gui.highlightMonster(index);
            // MANAGE MONSTER SPECIALS
            if (monster.special().equals("Immobilizer") && playerStatus != "Immobilized" && damageTaken > 0) {
                playerStatus = "Immobilized";
                gui.displayMessage(monster.name() + " ensnares you! 30% chance for actions to fail!");
            } else if (monster.special().equals("Colossus")) {
                if (Math.random() < 0.3) {
                    gui.displayMessage(monster.name() + " heals itself for 5% of its health!");
                    monster.heal(0.05);
                }
            } else if (monster.special().equals("Predator") && playerStatus != "Bleeding" && damageTaken > 0) {
                playerStatus = "Bleeding";
                gui.displayMessage(monster.name() + " cuts you deeply! You will take 5% of your max health every turn!");
            }
            gui.pause(300);
            gui.highlightMonster(-1);
        }
        if (playerStatus.equals("Shelled")) playerStatus = "Normal";
        shieldPoints = 0;
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

    public void heal(int amountHealed) {
        playerHealth = Math.min(playerMaxHealth, playerHealth + amountHealed);
    }

    public void heal(double percent) {
        int amountHealed = (int)(percent * playerMaxHealth);
        playerHealth = Math.min(playerMaxHealth, playerHealth + amountHealed);

    }

    public void resetActionButtons() {
        String[] actionButtons = 
        { "Attack (" + playerDamage + ")", 
          "Defend (" + playerShield + ")", 
          "Special (" + playerSpecialMeter + "/" + playerSpecialMax + ")",
          "Use Item"
        };
        gui.setActionButtons(actionButtons);
    }

    public void incrementSpecialMeter(int increase) {
        playerSpecialMeter = Math.min(playerSpecialMax, playerSpecialMeter + increase);
        if (playerSpecialMeter == playerSpecialMax) gui.displayMessage("Special meter at MAX!");
        else gui.displayMessage("Special meter UP!");
        resetActionButtons();
        gui.pause(300);
    }

    // =========== ITEMS ============
    public void createTonic(int healAmount) {
        inventory.add(new Item("Tonic", () -> {
            int trueHeal = Math.min(healAmount, playerMaxHealth - playerHealth);
            playerHealth += trueHeal;
            playerStatus = "Normal";
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("Healed " + trueHeal + " hp! All status ailments cleared!");
        }));
    }

    public void createBomb(int damage) { // A bomb? Underwater? Don't question it.
        inventory.add(new Item("Bomb", () -> {
            gui.displayMessage("BOOM!");
            gui.pause(200);
            for (Monster monster : monsters) {
                if (monster.health() > 0) {
                    gui.displayMessage(monster.name() + " takes " + damage + " damage!");
                    gui.pause(100);
                    attackMonster(monster, damage);
                }
            }
        }));
    }

    public void createAdrenaline(int statBoost, int specialBoost) {
        inventory.add(new Item("Adrenaline", () -> {
            playerDamage += statBoost;
            gui.displayMessage("Damage increased by " + statBoost + "!");
            gui.pause(300);
            playerShield += statBoost;
            gui.displayMessage("Shield increased by " + statBoost + "!");
            gui.pause(300);
            incrementSpecialMeter(specialBoost);
            gui.displayMessage("Special meter increased by " + specialBoost + "!");
            gui.pause(300);
        }));
    }

    public void createPocketSand(int debuff, int slowdown) { // Don't question why the pocket sand hits every monster, either.
        inventory.add(new Item("Pocket Sand", () -> {
            gui.displayMessage("All monsters weakened and slowed!");
            for (Monster monster : monsters) {
                if (monster.health() > 0 ) {
                    monster.weakenDamage(debuff);
                    monster.lowerSpeed(slowdown);
                }
            }
            gui.updateMonsters(monsters);
        }));
    }

    public void createRandomItem() {
        int index = (int)(Math.random() * 4);
        switch (index) {
            case (0): 
                createTonic(50);
                break;
            case (1): 
                createBomb(20);
                break;
            case (2):
                createAdrenaline(5, 1);
                break;
            default:
                createPocketSand(5, 1);
        }
        gui.updateInventory(inventory);
    }
}