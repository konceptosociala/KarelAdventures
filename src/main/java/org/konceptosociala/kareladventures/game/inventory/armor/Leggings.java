package org.konceptosociala.kareladventures.game.inventory.armor;

import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.game.inventory.ItemId;


import lombok.Getter;

@Getter
public class Leggings extends Item {

    public Leggings(ItemId id, String name, String iconPath) {
        super(id, name, iconPath);
    }
}
