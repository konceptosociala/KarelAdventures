package org.konceptosociala.kareladventures.utils;

public class TomlException extends Exception {
    public TomlException(String msg) {
        super("Cannot create dialog from toml: "+msg);
    }
}
