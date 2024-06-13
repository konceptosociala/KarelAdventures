package org.konceptosociala.kareladventures.game.inventory;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
    
    // Armor
    public static final Item METAL_HELMET = Item.load("data/Items/Armor/metal_helmet.toml");
    public static final Item METAL_CHESTPLATE = Item.load("data/Items/Armor/metal_chestplate.toml");
    public static final Item METAL_LEGGINGS = Item.load("data/Items/Armor/metal_leggings.toml");
    public static final Item METAL_BOOTS = Item.load("data/Items/Armor/metal_boots.toml");
    public static final Item SILVER_HELMET = Item.load("data/Items/Armor/silver_helmet.toml");
    public static final Item SILVER_CHESTPLATE = Item.load("data/Items/Armor/silver_chestplate.toml");
    public static final Item SILVER_LEGGINGS = Item.load("data/Items/Armor/silver_leggings.toml");
    public static final Item SILVER_BOOTS = Item.load("data/Items/Armor/silver_boots.toml");
    public static final Item GOLDEN_HELMET = Item.load("data/Items/Armor/golden_helmet.toml");
    public static final Item GOLDEN_CHESTPLATE = Item.load("data/Items/Armor/golden_chestplate.toml");
    public static final Item GOLDEN_LEGGINGS = Item.load("data/Items/Armor/golden_leggings.toml");
    public static final Item GOLDEN_BOOTS = Item.load("data/Items/Armor/golden_boots.toml");
    public static final Item JAVA_HELMET = Item.load("data/Items/Armor/java_helmet.toml");
    public static final Item JAVA_CHESTPLATE = Item.load("data/Items/Armor/java_chestplate.toml");
    public static final Item JAVA_LEGGINGS = Item.load("data/Items/Armor/java_leggings.toml");
    public static final Item JAVA_BOOTS = Item.load("data/Items/Armor/java_boots.toml");

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
    private final Long benefit;

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
            benefit = item.getLong("benefit");
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

    public static List<Item> getAllItems(ItemRareness rareness) {
        return switch (rareness) {
            case Common -> List.of(
                METAL_BOOTS, METAL_CHESTPLATE, METAL_HELMET, METAL_LEGGINGS,
                METAL_SWORD,DARK_TINY_SWORD
            );
            case Rare -> List.of(
                DUSTCLEANER,
                FIRE_DIRK
            );
            case Silver -> List.of(
                SILVER_BOOTS, SILVER_CHESTPLATE, SILVER_HELMET, SILVER_LEGGINGS,
                DEBUGGER, LAMBDA_SAW
            );
            case Golden -> List.of(
                GOLDEN_BOOTS, GOLDEN_CHESTPLATE, GOLDEN_HELMET, GOLDEN_LEGGINGS,
                DISASSEMBLER, GLUONER, SLITHER
            );
            case Legendary -> List.of(
                JAVA_BOOTS, JAVA_CHESTPLATE, JAVA_HELMET, JAVA_LEGGINGS,
                LAVA_BLADE, PYTHON_SABER
            );
        };
    }

    public static Item regular(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness
    ){
        return new Item(id, name, description, iconPath, ItemKind.Regular, rareness, null);
    }

    public static Item helmet(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Helmet, rareness, (long)benefit);
    }

    public static Item chestplate(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Chestplate, rareness, (long)benefit);
    }

    public static Item leggings(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Leggings, rareness, (long)benefit);
    }

    public static Item boots(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Boots, rareness, (long)benefit);
    }

    public static Item weapon(
        ItemId id, 
        String name, 
        String description,
        String iconPath, 
        ItemRareness rareness, 
        int benefit
    ){
        return new Item(id, name, description, iconPath, ItemKind.Weapon, rareness, (long)benefit);
    }

    public Optional<Long> getBenefit() {
        return Optional.ofNullable(benefit);
    }
}
