package item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public List<Item> items;
    public int selectedIndex = 0;  // For tracking the currently selected item

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(Item newItem) {
        // For consumable items, stack them if the same id already exists.
        if(newItem.type == ItemType.CONSUMABLE) {
            for(Item item : items) {
                if(item.id == newItem.id) {
                    item.quantity += newItem.quantity;
                    return;
                }
            }
        }
        items.add(newItem);
    }

    public int size() {
        return items.size();
    }

    public Item get(int index) {
        return items.get(index);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public void clear() {
        items.clear();
    }
}
