package org.konceptosociala.kareladventures.game.inventory;

import java.util.*;

public class Inventory {
    private static final int INVENTORY_CAPACITY = 5 * 3;

    private List<Item> items;
    private List<Armor> armor;

    public Inventory() {
        items = new ArrayList<>(INVENTORY_CAPACITY);
        armor = new ArrayList<>(4);
    }
}
