package org.konceptosociala.kareladventures.game.inventory;

import org.konceptosociala.kareladventures.ui.inventory.InventoryCellId;

import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Inventory {
    private Optional<Item> helmet;
    private Optional<Item> chestplate;
    private Optional<Item> leggings;
    private Optional<Item> boots;
    private Optional<Item> weapon;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Item[][] items;

    public Inventory() {
        helmet = Optional.empty();
        chestplate = Optional.empty();
        leggings = Optional.empty();
        boots = Optional.empty();
        weapon = Optional.empty();
        items = new Item[4][5];
    }

    public static Inventory test() {
        try {
            var inventory = new Inventory();
            inventory.items[0][0] = Item.regular(
                new ItemId("karel.item.item1"), 
                "Item1", 
                "Textures/ui/items/1.png"
            );
            inventory.items[1][1] = Item.regular(
                new ItemId("karel.item.item2"), 
                "Item2", 
                "Textures/ui/items/2.png"
            );
            inventory.helmet = Optional.of(Item.helmet(
                new ItemId("karel.item.helmet1"), 
                "Helmet1", 
                "Textures/ui/items/3.png"
            ));
            return inventory;
        } catch (InvalidItemIdException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public Optional<Item> getItem(InventoryCellId id) {
        var namedCell = id.getNamedCell();
        var gridCell = id.getGridCell();

        if (namedCell.isPresent()) {
            return switch (namedCell.get()) {
                case helmet -> helmet;
                case chestplate -> chestplate;
                case leggings -> leggings;
                case boots -> boots;
                case weapon -> weapon;
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
                        helmet = Optional.of(item);
                        return oldItem;
                    } else {
                        return Optional.empty();
                    }
                }
                case chestplate: {
                    var oldItem = chestplate;
                    if (item.getItemKind().equals(ItemKind.Chestplate)) {
                        chestplate = Optional.of(item);
                        return oldItem;
                    } else {
                        return Optional.empty();
                    }
                }
                case leggings: {
                    var oldItem = leggings;
                    if (item.getItemKind().equals(ItemKind.Leggings)) {
                        leggings = Optional.of(item);
                        return oldItem;
                    } else {
                        return Optional.empty();
                    }
                }
                case boots: {
                    var oldItem = boots;
                    if (item.getItemKind().equals(ItemKind.Boots)) {
                        boots = Optional.of(item);
                        return oldItem;
                    } else {
                        return Optional.empty();
                    }
                }
                case weapon: {
                    var oldItem = weapon;
                    if (item.getItemKind().equals(ItemKind.Weapon)) {
                        weapon = Optional.of(item);
                        return oldItem;
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
                    helmet = Optional.empty();
                    return oldItem;
                }
                case chestplate: {
                    var oldItem = chestplate;
                    chestplate = Optional.empty();
                    return oldItem;
                }
                case leggings: {
                    var oldItem = leggings;
                    leggings = Optional.empty();
                    return oldItem;
                }
                case boots: {
                    var oldItem = boots;
                    boots = Optional.empty();
                    return oldItem;
                }
                case weapon: {
                    var oldItem = weapon;
                    weapon = Optional.empty();
                    return oldItem;
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
}
