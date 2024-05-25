package org.konceptosociala.kareladventures.ui.inventory_cell_id;

public class InvalidCellIdException extends Exception {
    public InvalidCellIdException(String id) {
        super("Invalid cell id `"+id+"`");
    }
}
