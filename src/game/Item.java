package game;
/**
 * Item - Represents a consumable item in the game
 * 
 * Each item has:
 * - A name (what it's called)
 * - An icon (emoji or symbol to display)
 * - A usage effect (what happens when used)
 * 
 * This teaches students about:
 * - Encapsulation (bundling related data)
 * - Callbacks (storing behavior to execute later)
 * - Separation of concerns (item knows what it does)
 */


 public class Item {
    
    private String name;
    private Runnable onUse;  // The function to call when item is used
    
    /**
     * Constructor - Create an item with a usage effect
     * @param name The item's name
     * @param icon The emoji/symbol to display (e.g., "🧪", "💣", "⚔️")
     * @param onUse The code to run when this item is used
     */
    public Item(String name, Runnable onUse) {
        this.name = name;
        this.onUse = onUse;
    }
    
    /**
     * Get the item's name
     * @return The name
     */
    public String getName() {
        return name;
    }
    
    
    /**
     * Use this item (executes the onUse function)
     */
    public void use() {
        if (onUse != null) {
            onUse.run();
        }
    }
    
    /**
     * Check if this item has a usage effect defined
     * @return true if the item can be used
     */
    public boolean hasEffect() {
        return onUse != null;
    }
}