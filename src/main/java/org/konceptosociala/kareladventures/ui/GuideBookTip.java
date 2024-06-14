package org.konceptosociala.kareladventures.ui;

import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.tools.Color;

public class GuideBookTip extends PanelBuilder {
    public GuideBookTip(String id, String tip, String keyCode) {
        super(id);
        childLayoutHorizontal();
        align(Align.Left);
        valign(VAlign.Center);

        image(new ImageBuilder(id+"_guidebook_key") {{
            filename("Interface/GuideBook/Keyboard_Dark/"+keyCode+".png");
            marginRight("20px");
            valign(VAlign.Center);
        }});

        text(new TextBuilder(id+"_guidebook_tip") {{
            text("- "+tip);
            font("Interface/Fonts/Ubuntu-C.ttf");
            color(Color.BLACK);
            valign(VAlign.Center);
        }});
    }
}
