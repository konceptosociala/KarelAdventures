package org.konceptosociala.kareladventures.game.npc;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Dialog {
    private List<DialogMessage> messages;
    private Dialog nextDialog;
}
