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
        try {
            var inventory = new Inventory();
            inventory.items[0][0] = Item.regular(
                new ItemId("karel.item.item1"), 
                "Item1", 
                "Interface/items/1.png"
            );
            inventory.items[1][1] = Item.regular(
                new ItemId("karel.item.item2"), 
                "Item2", 
                "Interface/items/2.png"
            );
            inventory.chestplate = Item.chestplate(
                new ItemId("karel.item.chestplate1"), 
                "Chestplate1", 
                "Interface/items/23.png"
            );
            return inventory;
        } catch (InvalidItemIdException e) {
            e.printStackTrace();
            return null;
        }
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
