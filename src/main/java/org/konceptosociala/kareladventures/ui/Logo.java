package org.konceptosociala.kareladventures.ui;

import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;

public class Logo extends PanelBuilder {
    public Logo(String id, String path) {
        super(id);
        childLayoutCenter();
        width("100%");

        image(new ImageBuilder(id+"_image"){{
            filename(path);
        }});
    }
}
