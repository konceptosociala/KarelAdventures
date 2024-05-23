package org.konceptosociala.kareladventures.ui;

import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;

public class ImageButton extends PanelBuilder {
    public ImageButton(String id, String label, String onClick) {
        super(id+"_panel");
        childLayoutCenter();

        control(new ButtonBuilder(id+"_button") {{
            alignCenter();
            valignCenter();
            width("328px");
            height("54px");
            focusable(false);
            interactOnClick(onClick);
        }}); 

        image(new ImageBuilder(id+"_image"){{
            filename("Textures/button_idle.png");
            width("328px");
            height("54px");
        }});

        text(new TextBuilder(id+"_label"){{
            text(label);
            font("Interface/Fonts/Default.fnt");
            height("100px");
            width("100%");
        }});
    }
}
