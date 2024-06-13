package org.konceptosociala.kareladventures.game.npc;

import java.util.*;

import org.konceptosociala.kareladventures.utils.TomlException;

import com.moandjiezana.toml.Toml;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Dialog implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<DialogMessage> messages;
    private Dialog nextDialog;

    /**
     * @param dialogFile path to <code>.toml</code> file, where dialog is stored
     * @param nextDialog next dialog
     */
    public Dialog(String dialogFile, Dialog nextDialog) throws TomlException {
        try {
            this.messages = new ArrayList<>();
            this.nextDialog = nextDialog;

            String tomlString = Files.readString(Path.of(dialogFile));
            Toml toml = new Toml().read(tomlString);

            toml
                .getTable("dialog")
                .getList("messages", new ArrayList<HashMap<String, String>>())
                .forEach((m) -> {
                    this.messages.add(new DialogMessage(
                        m.get("author"), 
                        m.get("message")
                    ));
                });
            
        } catch (Exception e) {
            throw new TomlException(e.getMessage());
        }
    }
}
