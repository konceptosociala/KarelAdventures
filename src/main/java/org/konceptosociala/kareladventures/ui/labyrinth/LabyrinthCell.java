package org.konceptosociala.kareladventures.ui.labyrinth;

import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.tools.Color;

public class LabyrinthCell extends PanelBuilder {
    private boolean blocked;

    public LabyrinthCell(LabyrinthCellId id, boolean blocked) {
        super(id.toString());
        childLayoutCenter();
        width("100%");
        height("25%");
        align(Align.Center); 

        this.blocked = blocked;
        if (blocked)
            backgroundColor(Color.BLACK);
        else
            backgroundColor(Color.WHITE); 
    }
}