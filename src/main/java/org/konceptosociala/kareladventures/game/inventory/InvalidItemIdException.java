package org.konceptosociala.kareladventures.game.inventory;

public class InvalidItemIdException extends Exception {
    public InvalidItemIdException(String itemId) {
        super("Invalid item ID: " + itemId);
    }
}
