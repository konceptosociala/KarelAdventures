package org.konceptosociala.kareladventures.ui;

import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;

public class LoadingBarBuilder extends ControlBuilder {
    public LoadingBarBuilder(String name) {
        super(name);
        image(new ImageBuilder(){{
            filename("Interface/border1.png");
            childLayoutAbsolute();
            imageMode("resize:15,2,15,15,15,2,15,2,15,2,15,15");

            image(new ImageBuilder("progress_bar"){{
                x("0");
                y("0");
                filename("Interface/inner1.png");
                width("32px");
                height("100%");
                imageMode("resize:15,2,15,15,15,2,15,2,15,2,15,15");
            }});
        }});
    }
}
