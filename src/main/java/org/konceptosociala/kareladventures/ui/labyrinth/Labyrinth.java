package org.konceptosociala.kareladventures.ui.labyrinth;

import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.tools.Color;

public class Labyrinth extends PanelBuilder {

    public Labyrinth(String id, LabyrinthCell[][] cells) {
        super(id);
        childLayoutHorizontal();
        backgroundColor(Color.WHITE);
        width("90%");
        height("90%");

        for (int i = 0; i < 4; i++) {
            final int ic = i;
            panel(new PanelBuilder("inv_col_"+i){{
                childLayoutVertical();

                for (int j = 0; j < 4; j++) {
                    panel(cells[ic][j].toPanelBuilder());
                }
            }});
        }
    }  
}
