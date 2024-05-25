package org.konceptosociala.kareladventures.game.inventory;

import java.util.regex.*;

import lombok.Getter;

public class ItemId {
    public static final Pattern ITEM_ID_REGEX = Pattern.compile("karel\\.item\\.[a-z_0-9]+");

    @Getter
    private String value;

    public ItemId(String itemId) throws InvalidItemIdException {
        if (!ITEM_ID_REGEX.matcher(itemId).matches())
            throw new InvalidItemIdException(itemId);
        else 
            value = itemId;
    }
}
