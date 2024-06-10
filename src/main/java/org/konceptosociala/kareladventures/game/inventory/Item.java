package org.konceptosociala.kareladventures.game.inventory;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final ItemId id;
    private final String name;
    private final String iconPath;
    private final ItemKind itemKind;

    public static Item regular(ItemId id, String name, String iconPath) {
        return new Item(id, name, iconPath, ItemKind.Regular);
    }

    public static Item helmet(ItemId id, String name, String iconPath) {
        return new Item(id, name, iconPath, ItemKind.Helmet);
    }

    public static Item chestplate(ItemId id, String name, String iconPath) {
        return new Item(id, name, iconPath, ItemKind.Chestplate);
    }

    public static Item leggings(ItemId id, String name, String iconPath) {
        return new Item(id, name, iconPath, ItemKind.Leggings);
    }

    public static Item boots(ItemId id, String name, String iconPath) {
        return new Item(id, name, iconPath, ItemKind.Boots);
    }

    public static Item weapon(ItemId id, String name, String iconPath) {
        return new Item(id, name, iconPath, ItemKind.Weapon);
    }
}
