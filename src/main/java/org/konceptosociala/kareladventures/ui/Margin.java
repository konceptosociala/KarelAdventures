package org.konceptosociala.kareladventures.ui;

import de.lessvoid.nifty.builder.PanelBuilder;

public class Margin extends PanelBuilder {
    public Margin(String width, String height) {
        width(width);
        height(height);
    }

    public static Margin vertical(String height) {
        return new Margin("0px", height);
    }

    public static Margin horizontal(String width) {
        return new Margin(width, "0px");
    }
}
