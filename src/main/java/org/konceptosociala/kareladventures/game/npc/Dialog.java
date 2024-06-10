package org.konceptosociala.kareladventures.game.npc;

import java.util.*;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Dialog implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<DialogMessage> messages;
    private Dialog nextDialog;

    /**
     * @param dialogFile path to <code>.toml</code> file, where dialog
     * @param nextDialog next dialog
     */
    public Dialog(String dialogFile, Dialog nextDialog) {
        throw new UnsupportedOperationException("not implemented: Dialog from TOML");
    }
}
