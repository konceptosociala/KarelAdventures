package org.konceptosociala.kareladventures.game.inventory;

import org.konceptosociala.kareladventures.ui.inventory.InventoryCellId;

import java.io.Serializable;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Item helmet;
    private Item chestplate;
    private Item leggings;
    private Item boots;
    private Item weapon;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Item[][] items;

    public Inventory() {
        helmet = null;
        chestplate = null;
        leggings = null;
        boots = null;
        weapon = null;
        items = new Item[4][5];
    }

    public static Inventory test() {
            var inventory = new Inventory();
            inventory.items[0][0] = Item.METAL_HELMET;
            inventory.items[0][1] = Item.METAL_CHESTPLATE;
            inventory.items[0][2] = Item.METAL_LEGGINGS;
            inventory.items[0][3] = Item.METAL_BOOTS;
            inventory.items[0][4] = Item.SILVER_HELMET;
            inventory.items[1][0] = Item.SILVER_CHESTPLATE;
            inventory.items[1][1] = Item.SILVER_LEGGINGS;
            inventory.items[1][2] = Item.SILVER_BOOTS;
            inventory.items[1][3] = Item.GOLDEN_HELMET;
            inventory.items[1][4] = Item.GOLDEN_CHESTPLATE;
            inventory.items[2][0] = Item.GOLDEN_LEGGINGS;
            inventory.items[2][1] = Item.GOLDEN_BOOTS;
            inventory.items[2][2] = Item.LAVA_BLADE;
            inventory.items[2][3] = Item.METAL_SWORD;
            inventory.items[2][4] = Item.PYTHON_SABER;
            inventory.weapon = Item.SLITHER;
            inventory.helmet = Item.JAVA_HELMET;
            inventory.chestplate = Item.JAVA_CHESTPLATE;
            inventory.leggings = Item.JAVA_LEGGINGS;
            inventory.boots = Item.JAVA_BOOTS;
            return inventory;
    }

    public boolean isFull() {
        for (var col : items) {
            for (var item : col) {
                if (item == null)
                    return false;
            }
        }

        return true;
    }

    public boolean addItem(Item item) {
        for (int i = 0; i < items.length; i++) {
            for (int j = 0; j < items[i].length; j++) {
                if (items[i][j] == null) {
                    items[i][j] = item;
                    return true;
                }
            }
        }

        return false;
    }

    public Optional<Item> getItem(InventoryCellId id) {
        var namedCell = id.getNamedCell();
        var gridCell = id.getGridCell();

        if (namedCell.isPresent()) {
            return switch (namedCell.get()) {
                case helmet -> optional(helmet);
                case chestplate -> optional(chestplate);
                case leggings -> optional(leggings);
                case boots -> optional(boots);
                case weapon -> optional(weapon);
            };
        } else {
            int col = gridCell.get().getColumn();
            int row = gridCell.get().getRow();

            Item gridItem = items[col-1][row-1];
            if (gridItem != null)
                return Optional.of(gridItem);
            else
                return Optional.empty();
        }
    }

    /**
     * 
     * @param item
     * @param id
     * @return item, which (may) be located on given `id`
     */
    public Optional<Item> setItem(Item item, InventoryCellId id) {
        var namedCell = id.getNamedCell();
        var gridCell = id.getGridCell();

        if (namedCell.isPresent()) {
            switch (namedCell.get()) {
                case helmet: {
                    var oldItem = helmet;
                    if (item.getItemKind().equals(ItemKind.Helmet)) {
                        helmet = item;
                        return optional(oldItem);
                    } else {
                        return Optional.empty();
                    }
                }
                case chestplate: {
                    var oldItem = chestplate;
                    if (item.getItemKind().equals(ItemKind.Chestplate)) {
                        chestplate = item;
                        return optional(oldItem);
                    } else {
                        return Optional.empty();
                    }
                }
                case leggings: {
                    var oldItem = leggings;
                    if (item.getItemKind().equals(ItemKind.Leggings)) {
                        leggings = item;
                        return optional(oldItem);
                    } else {
                        return Optional.empty();
                    }
                }
                case boots: {
                    var oldItem = boots;
                    if (item.getItemKind().equals(ItemKind.Boots)) {
                        boots = item;
                        return optional(oldItem);
                    } else {
                        return Optional.empty();
                    }
                }
                case weapon: {
                    var oldItem = weapon;
                    if (item.getItemKind().equals(ItemKind.Weapon)) {
                        weapon = item;
                        return optional(oldItem);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } else {
            int col = gridCell.get().getColumn();
            int row = gridCell.get().getRow();

            Item gridItem = items[col-1][row-1];
            items[col-1][row-1] = item;

            if (gridItem != null)
                return Optional.of(gridItem);
            else
                return Optional.empty();
        }

        return null;
    }

    public Optional<Item> removeItem(InventoryCellId id) {
        var namedCell = id.getNamedCell();
        var gridCell = id.getGridCell();

        if (namedCell.isPresent()) {
            switch (namedCell.get()) {
                case helmet: {
                    var oldItem = helmet;
                    helmet = null;
                    return optional(oldItem);
                }
                case chestplate: {
                    var oldItem = chestplate;
                    chestplate = null;
                    return optional(oldItem);
                }
                case leggings: {
                    var oldItem = leggings;
                    leggings = null;
                    return optional(oldItem);
                }
                case boots: {
                    var oldItem = boots;
                    boots = null;
                    return optional(oldItem);
                }
                case weapon: {
                    var oldItem = weapon;
                    weapon = null;
                    return optional(oldItem);
                }
            }
        } else {
            int col = gridCell.get().getColumn();
            int row = gridCell.get().getRow();

            Item gridItem = items[col-1][row-1];
            items[col-1][row-1] = null;

            if (gridItem != null)
                return Optional.of(gridItem);
            else
                return Optional.empty();
        }

        return null;
    }

    private <T> Optional<T> optional(T nullable) {
        if (nullable != null) 
            return Optional.of(nullable);
        else 
            return Optional.empty();
    }
}
