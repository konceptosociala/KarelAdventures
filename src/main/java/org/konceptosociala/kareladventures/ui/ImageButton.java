package org.konceptosociala.kareladventures.ui;

import javax.annotation.Nullable;

import de.lessvoid.nifty.Size;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.tools.Color;

public class ImageButton extends PanelBuilder {
    public ImageButton(String id, String label, @Nullable Size size, String onClick) {
        super(id+"_panel");
        childLayoutCenter();

        image(new ImageBuilder(id+"_image"){{
            filename("Interface/UI/Transparent border/panel-transparent-border-008.png");
            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
            
            if (size != null) {
                width(size.getWidth()+"px");
                height(size.getHeight()+"px");
            } else {
                width("328px");
                height("54px");
            }

            focusable(false);
            interactOnClick(onClick);
            visibleToMouse(true);

            onHoverEffect(new HoverEffectBuilder("changeImage") {{
                effectParameter("active", "Interface/UI/Panel/panel-008.png");
                effectParameter("inactive", "Interface/UI/Transparent border/panel-transparent-border-008.png");
                effectParameter("imageMode", "resize:16,16,16,16,16,16,16,16,16,16,16,16");
            }});
        }});

        text(new TextBuilder(id+"_label"){{
            text(label);
            font("Interface/Fonts/Ubuntu-C.ttf");
            height("80px");
            width("100%");
            color(Color.BLACK);
        }});
    }
}
