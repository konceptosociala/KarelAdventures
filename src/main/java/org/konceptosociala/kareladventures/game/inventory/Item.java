package org.konceptosociala.kareladventures.game.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Item {
    private final ItemId id;
    private final String name;
    private final String iconPath;
}
