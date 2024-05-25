package org.konceptosociala.kareladventures.game.inventory.armor;

import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.game.inventory.ItemId;


import lombok.Getter;

@Getter
public class Boots extends Item {

    public Boots(ItemId id, String name, String iconPath) {
        super(id, name, iconPath);
    }
}
