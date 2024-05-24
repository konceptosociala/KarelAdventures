package org.konceptosociala.kareladventures.game.inventory;

import de.lessvoid.nifty.render.NiftyImage;
import lombok.Getter;

@Getter
public class Weapon extends Item {

    public Weapon(ItemId id, String name, NiftyImage icon) {
        super(id, name, icon);
    }
}
