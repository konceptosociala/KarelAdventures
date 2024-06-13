package org.konceptosociala.kareladventures.utils;

public class SaveLoadException extends Exception {
    public SaveLoadException(String msg) {
        super("Cannot perform save/load operation: " + msg);
    }
}
