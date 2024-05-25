package org.konceptosociala.kareladventures.game.inventory;

import java.util.*;

import org.konceptosociala.kareladventures.game.inventory.armor.*;
import org.konceptosociala.kareladventures.ui.inventory_cell_id.InventoryCellId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {
    private Optional<Helmet> helmet;
    private Optional<Chestplate> chestplate;
    private Optional<Leggings> leggings;
    private Optional<Boots> boots;
    private Optional<Weapon> weapon;
    
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
            inventory.items[0][0] = new Item(new ItemId("karel.item.item1"), "Item1", "Textures/ui/items/1.png");
            inventory.items[1][1] = new Item(new ItemId("karel.item.item2"), "Item2", "Textures/ui/items/2.png");
            inventory.helmet = Optional.of(new Helmet(new ItemId("karel.item.helmet1"), "Helmet1", "Textures/ui/items/3.png"));
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
                case helmet -> helmet.map(i -> (Item) i);
                case chestplate -> chestplate.map(i -> (Item) i);
                case leggings -> leggings.map(i -> (Item) i);
                case boots -> boots.map(i -> (Item) i);
                case weapon -> weapon.map(i -> (Item) i);
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
                    var oldItem = helmet.map(i -> (Item) i);
                    helmet = Optional.of((Helmet) item);
                    return oldItem;
                }
                case chestplate: {
                    var oldItem = chestplate.map(i -> (Item) i);
                    chestplate = Optional.of((Chestplate) item);
                    return oldItem;
                }
                case leggings: {
                    var oldItem = leggings.map(i -> (Item) i);
                    leggings = Optional.of((Leggings) item);
                    return oldItem;
                }
                case boots: {
                    var oldItem = boots.map(i -> (Item) i);
                    boots = Optional.of((Boots) item);
                    return oldItem;
                }
                case weapon: {
                    var oldItem = weapon.map(i -> (Item) i);
                    weapon = Optional.of((Weapon) item);
                    return oldItem;
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
                    var oldItem = helmet.map(i -> (Item) i);
                    helmet = Optional.empty();
                    return oldItem;
                }
                case chestplate: {
                    var oldItem = chestplate.map(i -> (Item) i);
                    chestplate = Optional.empty();
                    return oldItem;
                }
                case leggings: {
                    var oldItem = leggings.map(i -> (Item) i);
                    leggings = Optional.empty();
                    return oldItem;
                }
                case boots: {
                    var oldItem = boots.map(i -> (Item) i);
                    boots = Optional.empty();
                    return oldItem;
                }
                case weapon: {
                    var oldItem = weapon.map(i -> (Item) i);
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
