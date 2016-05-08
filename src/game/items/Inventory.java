package game.items;

/**
 * Created by Max on 3/19/2016.
 *
 * Since we've got a world with items in it, we need to be able to pick them up and do stuff with them. A lot can
 * happen with a creature's inventory so it gets a dedicated class. Instead of using a list we'll use an array so the
 * items index doesn't change when we lose something before it. E.g. if we quaff the potion in our 'd' slot, whatever
 * was in the 'e' slot should remain there and not slide into the 'd' slot. If you want that kind of behavior then you
 * could use a List — but we don't.
 */

public class Inventory {

    private Item[] items;
    public Item[] getItems() {
        return items;
    }

    public Item get(int i) {
        return items[i];
    }

    public Inventory(int max){
        items = new Item[max];
    }

    /**
     * We need a method to add an item to the first open slot in our inventory.
     *
     * @param item
     */
    public void add(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == null){
                items[i] = item;
                break;
            }
        }
    }

    /**
     * And a way to remove an item from our inventory.
     *
     * @param item
     */
    public void remove(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == item){
                items[i] = null;
                return;
            }
        }
    }

    /**
     * We also need to know if the inventory is full and we can't carry any more.
     *
     * @return
     */
    public boolean isFull(){
        int size = 0;
        for (int i = 0; i < items.length; i++){
            if (items[i] != null)
                size++;
        }
        return size == items.length;
    }

    /**
     * Check the inventory to see if it contains an item.
     * @param item
     * @return
     */
    public boolean contains(Item item) {
        for (Item i : items){
            if (i == item)
                return true;
        }
        return false;
    }
}