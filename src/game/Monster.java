package game;
public class Monster {
    // INSTANCE VARIABLES (properties)
    private int health;
    private double damage;
    private int speed;
    private String special; 
    private String name;

    private String[] adjectives = {"Evil ", "Devious ", "Killer ", "Giant ", "Angry ", "Ferocious ", "Malevolent ", "Wicked ", "Cruel ", "Vicious ", "Dark "};
    private String[] species = {"Starfish", "Clam", "Jellyfish ", "Sea Pickle ", "Kelp", "Coral", "Sea Urchin", "Shrimp", "Eel", "Octopus", "Crab", "Stingray", "Nautilus"};

    // CONSTRUCTOR
    public Monster() {
        // randomly generate health, damage, speed
        health = (int)(Math.random() * 80 + 1) + 20;
        // random 5 - 25
        damage = (Math.random() * 21) + 5;
        // speed: random 1-10
        speed = (int)(Math.random() * 10) + 1;
        // by default, the monster doesn't have a special move
        special = "";
        name = adjectives[(int)(Math.random() * adjectives.length)] + species[(int)(Math.random() * species.length)];
    }

    // OVERLOADED CONSTRUCTOR
    public Monster(String special){
        this();
        this.special = special;
    }
    
    // ACCESSOR METHODS
    public int health() { return this.health; }
    public double damage() { return Math.round(damage * 100.0) / 100.0; }
    public int speed() { return speed; }
    public String special() { return this.special; }
    public String name() { return this.name; }

    // MUTATOR METHODS
    public void takeDamage(int dmg){
        health -= dmg;
    }
}