package org.konceptosociala.kareladventures.ui.labyrinth;

import java.util.Optional;

import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.tools.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LabyrinthCell implements Cloneable {
    private final LabyrinthCellId id;
    private boolean blocked;
    private boolean karel;
    private boolean beeper;
    private KarelDirection karelDirection;

    public PanelBuilder toPanelBuilder() {
        return new PanelBuilder(id.toString()) {{
            childLayoutCenter();
            width("100%");
            height("25%");
            align(Align.Center); 

            if (blocked)
                backgroundColor(Color.BLACK);                

            if (beeper)
                backgroundImage("Interface/beeper.png");

            if (karel) {
                switch (karelDirection) {
                    case Up -> backgroundImage("Interface/karel/up.png");
                    case Down -> backgroundImage("Interface/karel/down.png");
                    case Left -> backgroundImage("Interface/karel/left.png");
                    case Right -> backgroundImage("Interface/karel/right.png");
                }
            }
        }};
    }

    @Override
    public LabyrinthCell clone() {
        return new LabyrinthCell(id, blocked, karel, beeper, karelDirection);
    }
}