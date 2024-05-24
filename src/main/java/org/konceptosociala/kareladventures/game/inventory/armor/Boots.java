package org.konceptosociala.kareladventures.game.inventory.armor;

import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.game.inventory.ItemId;

import de.lessvoid.nifty.render.NiftyImage;
import lombok.Getter;

@Getter
public class Boots extends Item {

    public Boots(ItemId id, String name, NiftyImage icon) {
        super(id, name, icon);
    }
}
