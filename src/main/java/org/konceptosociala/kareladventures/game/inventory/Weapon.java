package org.konceptosociala.kareladventures.game.inventory;


import lombok.Getter;

@Getter
public class Weapon extends Item {

    public Weapon(ItemId id, String name, String iconPath) {
        super(id, name, iconPath);
    }
}
