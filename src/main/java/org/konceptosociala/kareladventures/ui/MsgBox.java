package org.konceptosociala.kareladventures.ui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.Size;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.tools.Color;

public class MsgBox extends LayerBuilder {
    public MsgBox(String id, String hideCallback) {
        super(id);
        childLayoutCenter();
        width("100%");
        height("100%");
        visible(false);

        panel(new PanelBuilder(id+"_bg") {{
            childLayoutCenter();
            width("100%");
            height("100%");
            backgroundColor(new Color(0, 0, 0, 0.75f));
        }});

        image(new ImageBuilder(id+"_image") {{
            filename("Interface/UI/Transparent center/panel-transparent-center-001.png");
            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
            width("400px");
            height("300px");
        }});

        panel(new PanelBuilder(id+"_panel") {{
            childLayoutVertical();
            width("400px");
            height("300px");
            padding("20px");

            text(new TextBuilder(id+"_title") {{
                align(Align.Center);
                textHAlign(Align.Center);
                width("400px");
                height("25px");
                text("                                                                ");
                font("Interface/Fonts/Ubuntu-C.ttf");
                color(Color.BLACK);
            }});

            text(new TextBuilder(id+"_message") {{
                align(Align.Left);
                textHAlign(Align.Left);
                width("400px");
                height("275px");
                text("                                                            ");
                font("Interface/Fonts/Ubuntu-C.ttf");
                color(Color.BLACK);
                wrap(true);
            }});

            panel(new ImageButton(id+"_okay", "Okay", new Size(150, 50), hideCallback));
        }});
    }

    @SuppressWarnings("null")
    public static void setData(Nifty nifty, String id, String title, String message) {
        nifty
            .getCurrentScreen()
            .findElementById(id+"_title")
            .getRenderer(TextRenderer.class)
            .setText(title);

        nifty
            .getCurrentScreen()
            .findElementById(id+"_message")
            .getRenderer(TextRenderer.class)
            .setText(message);
    }
}
