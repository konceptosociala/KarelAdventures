package org.konceptosociala.kareladventures.game.inventory;

import com.jme3.texture.Texture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Item {
    public final ItemId id;
    public final String name;
    public final Texture itemIcon;
}
