package org.konceptosociala.kareladventures.ui;

public class InvalidCellIdException extends Exception {
    public InvalidCellIdException(String id) {
        super("Invalid cell id `"+id+"`");
    }
}
