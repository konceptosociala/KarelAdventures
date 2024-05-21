package org.konceptosociala.kareladventures.game.inventory;

import com.jme3.texture.Texture;
import lombok.Getter;

@Getter
public class Armor extends Item {
    private final ArmorType type;

    public Armor(ItemId id, String name, Texture itemIcon, ArmorType type) {
        super(id, name, itemIcon);
        this.type = type;
    }
}
