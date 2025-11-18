package game;
public class Monster {
    // INSTANCE VARIABLES (properties)
    private int health;
    private int maxHealth;
    private int damage; // There's no reason for damage to be a double
    private int speed; 
    private String name;
    private String special;

    private String[] adjectives = {"Evil ", "Devious ", "Killer ", "Giant ", "Angry ", "Ferocious ", "Malevolent ", "Wicked ", "Cruel ", "Vicious ", "Dark "};
    private String[] species = {"Starfish", "Clam", "Jellyfish ", "Sea Pickle ", "Kelp", "Coral", "Sea Urchin", "Shrimp", "Eel", "Octopus", "Crab", "Stingray", "Nautilus"};

    // CONSTRUCTOR
    public Monster() {
        // randomly generate health, damage, speed
        maxHealth = (int)(Math.random() * 80 + 1) + 20;
        // random 5 - 25
        damage = (int)(Math.random() * 21) + 5;
        // speed: random 1-10
        speed = (int)(Math.random() * 10) + 1;
        // by default, the monster doesn't have a special move
        special = "";
        name = adjectives[(int)(Math.random() * adjectives.length)] 
             + species[(int)(Math.random() * species.length)];
        health = maxHealth;
    }

    // OVERLOADED CONSTRUCTOR
    public Monster(String special) {
        this.special = special;
        if (special.equals("Immobilizer")) {
            name = "Kraken";
            maxHealth = 100;
            speed = 5;
            damage = 25;
        } 
        else if (special.equals("Colossus")) {
            name = "Great Whale";
            maxHealth = 250;
            speed = -1; // Only retaliates
            damage = 10;
        }
        else if (special.equals("Predator")) {
            name = "Hammerhead Shark";
            maxHealth = 50;
            speed = 11;
            damage = 35;
        }
        health = maxHealth;
    }
    
    // ACCESSOR METHODS
    public int health() { return health; }
    public int maxHealth() { return maxHealth; }
    public int damage() { return damage; }
    public int speed() { return speed; }
    public String special() { return special; }
    public String name() { return name; }

    // MUTATOR METHODS
    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg);
    }

    /**
     * Heals the monster by a certain percentage of its max health.
     * @param percent A number from 0.0 to 1.0.
     */
    public void heal(double percent) {
        int amountHealed = (int)(percent * maxHealth);
        health = Math.min(maxHealth, health + amountHealed);
    }

    /**
     * Heals the monster by a certain amount of hp.
     * @param amountHealed The amount to be healed.
     */
    public void heal(int amountHealed) {
        health = Math.min(maxHealth, health + amountHealed);
    }
}