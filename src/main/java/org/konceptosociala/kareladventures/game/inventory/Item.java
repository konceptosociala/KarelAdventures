package org.konceptosociala.kareladventures.game.inventory;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.konceptosociala.kareladventures.utils.TomlException;

import com.moandjiezana.toml.Toml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Weapon
    public static final Item DARK_TINY_SWORD = Item.load("data/Items/Weapon/dark_tiny_sword.toml");
    public static final Item DISASSEMBLER = Item.load("data/Items/Weapon/disassembler.toml");
    public static final Item DUSTCLEANER = Item.load("data/Items/Weapon/dustcleaner.toml");
    public static final Item FIRE_DIRK = Item.load("data/Items/Weapon/fire_dirk.toml");
    public static final Item GLUONER = Item.load("data/Items/Weapon/gluoner.toml");
    public static final Item DEBUGGER = Item.load("data/Items/Weapon/debugger.toml");
    public static final Item LAMBDA_SAW = Item.load("data/Items/Weapon/lambda_saw.toml");
    public static final Item LAVA_BLADE = Item.load("data/Items/Weapon/lava_blade.toml");
    public static final Item METAL_SWORD = Item.load("data/Items/Weapon/metal_sword.toml");
    public static final Item PYTHON_SABER = Item.load("data/Items/Weapon/python_saber.toml");
    public static final Item SLITHER = Item.load("data/Items/Weapon/slither.toml");
    
    private final ItemId id;
    private final String name;
    private final String description;
    private final String iconPath;
    private final ItemKind itemKind;
    private final ItemRareness itemRareness;
    private final Optional<Long> benefit;

    public Item(String itemFile) throws TomlException {
        try {
            String tomlString = Files.readString(Path.of(itemFile));
            Toml item = new Toml().read(tomlString).getTable("item");

            id = new ItemId(item.getString("id"));
            name = item.getString("name");
            description = item.getString("description");
            iconPath = item.getString("icon_path");
            itemKind = ItemKind.valueOf(item.getString("item_kind"));
            itemRareness = ItemRareness.valueOf(item.getString("item_rareness"));
            benefit = Optional.ofNullable(item.getLong("benefit"));
        } catch (Exception e) {
            throw new TomlException(e.getMessage());
        }
    }

    public static Item load(String itemFile) {
        try {
            return new Item(itemFile);
        } catch (TomlException e) {
            e.printStackTrace();
            System.exit(-1);    
            return null;
        }
    }

    public static Item regular(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness
    ){
        return new Item(id, name, description, iconPath, ItemKind.Regular, rareness, Optional.empty());
    }

    public static Item helmet(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Helmet, rareness, Optional.of((long)benefit));
    }

    public static Item chestplate(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Chestplate, rareness, Optional.of((long)benefit));
    }

    public static Item leggings(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Leggings, rareness, Optional.of((long)benefit));
    }

    public static Item boots(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Boots, rareness, Optional.of((long)benefit));
    }

    public static Item weapon(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Weapon, rareness, Optional.of((long)benefit));
    }
}
