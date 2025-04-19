package item;

import java.awt.image.BufferedImage;

public class Item {
    public int id;
    public String name;
    public String description;
    public ItemType type;
    public int quantity;       // For consumables
    public int hpBoost;        // For consumables: how much HP is recovered
    public int attackBoost;    // For equipment: additional attack
    public int defenseBoost;   // For equipment: additional defense
    public int price;
    public BufferedImage icon; // The item’s icon (for drawing)

    public Item(int id, String name, String description, ItemType type,
                int quantity, int hpBoost, int attackBoost, int defenseBoost, int price, BufferedImage icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.quantity = quantity;
        this.hpBoost = hpBoost;
        this.attackBoost = attackBoost;
        this.defenseBoost = defenseBoost;
        this.price = price;
        this.icon = icon;
    }
}
