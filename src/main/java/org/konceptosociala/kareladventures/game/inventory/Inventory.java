package org.konceptosociala.kareladventures.game.inventory;

import java.util.*;

import org.konceptosociala.kareladventures.game.inventory.armor.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {
    private static final int INVENTORY_CAPACITY = 5 * 4;

    private Optional<Helmet> helmet;
    private Optional<Chestplate> chestplate;
    private Optional<Leggings> leggings;
    private Optional<Boots> boots;
    private Optional<Weapon> weapon;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Item> items;

    public Inventory() {
        helmet = Optional.empty();
        chestplate = Optional.empty();
        leggings = Optional.empty();
        boots = Optional.empty();
        weapon = Optional.empty();
        items = new ArrayList<>(INVENTORY_CAPACITY);
    }

    public Optional<Item> getItem(int idx) {
        Item item = items.get(idx);
        if (item != null)
            return Optional.of(item);
        else
            return Optional.empty();
    }

    public void setItem(Item item, int idx) {
        items.set(idx, item);
    }
}
