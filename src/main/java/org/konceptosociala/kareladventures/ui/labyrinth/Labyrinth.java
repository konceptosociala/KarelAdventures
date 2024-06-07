package org.konceptosociala.kareladventures.ui.labyrinth;

import java.util.random.RandomGenerator;

import org.konceptosociala.kareladventures.ui.InvalidCellIdException;

import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.tools.Color;

public class Labyrinth extends PanelBuilder {
    RandomGenerator rgen = RandomGenerator.getDefault();

    public Labyrinth(String id) {
        super(id);
        childLayoutCenter();
        width("100%h");
        height("85%h");
        align(Align.Center);

        image(new ImageBuilder(id+"_bg") {{
            filename("Interface/UI/Panel/panel-027.png");
            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
            width("100%");
            height("100%");
        }});

        panel(new PanelBuilder(id+"_inner") {{
            childLayoutHorizontal();
            backgroundColor(Color.WHITE);
            width("90%");
            height("90%");

            try {
                for (int i = 1; i <= 4; i++) {
                    final int ic = i;
                    panel(new PanelBuilder("inv_col_"+i){{
                        childLayoutVertical();
        
                        for (int j = 1; j <= 4; j++) {
                            panel(new LabyrinthCell(new LabyrinthCellId("lab_cell_"+ic+"_"+j), rgen.nextBoolean()));
                        }
                    }});
                }
            } catch (InvalidCellIdException e) {
                e.printStackTrace();
            }
        }});
    }
}
