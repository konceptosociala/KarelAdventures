package org.konceptosociala.kareladventures.game.npc;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Dialog {
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
