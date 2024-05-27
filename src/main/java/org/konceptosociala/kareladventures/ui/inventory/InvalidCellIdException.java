package org.konceptosociala.kareladventures.ui.inventory;

public class InvalidCellIdException extends Exception {
    public InvalidCellIdException(String id) {
        super("Invalid cell id `"+id+"`");
    }
}
