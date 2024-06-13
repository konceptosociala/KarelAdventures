package org.konceptosociala.kareladventures.game.npc;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DialogMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String author;
    private String message;
}
