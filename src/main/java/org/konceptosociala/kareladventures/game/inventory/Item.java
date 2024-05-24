package org.konceptosociala.kareladventures.game.inventory;

import de.lessvoid.nifty.render.NiftyImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Item {
    public final ItemId id;
    public final String name;
    public final NiftyImage icon;
}
