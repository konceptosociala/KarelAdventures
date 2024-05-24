package org.konceptosociala.kareladventures.game.inventory.armor;

import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.game.inventory.ItemId;

import de.lessvoid.nifty.render.NiftyImage;
import lombok.Getter;

@Getter
public class Helmet extends Item {

    public Helmet(ItemId id, String name, NiftyImage icon) {
        super(id, name, icon);
    }
}