package org.konceptosociala.kareladventures.ui;

import javax.annotation.Nonnull;

import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.tools.Color;

public class FadePanel extends PanelBuilder {
    public FadePanel(@Nonnull String id) {
        super(id);
        
        width("100%");
        height("100%");
        backgroundColor(Color.BLACK);
    }
}
