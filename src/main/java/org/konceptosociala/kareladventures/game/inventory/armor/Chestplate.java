package org.konceptosociala.kareladventures.game.inventory.armor;

import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.game.inventory.ItemId;


import lombok.Getter;

@Getter
public class Chestplate extends Item {

    public Chestplate(ItemId id, String name, String iconPath) {
        super(id, name, iconPath);
    }
}
