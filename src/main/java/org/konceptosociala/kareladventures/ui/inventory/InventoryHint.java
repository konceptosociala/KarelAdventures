package org.konceptosociala.kareladventures.ui.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.TargetElementResolver;

public class InventoryHint implements EffectImpl {

    private Nifty nifty;
    private Element targetElement;
    private String itemName;
    private String itemRareness;
    private String itemDescription;
    private String itemBenefit;
    private boolean hasItem;

    @SuppressWarnings("null")
    @Override
    public void activate(@Nonnull Nifty nifty, @Nonnull Element element, @Nonnull EffectProperties parameter) {
        this.nifty = nifty;
        this.hasItem = parameter.getProperty("hasItem").equals("true");
        this.itemName = parameter.getProperty("itemName");
        this.itemDescription = parameter.getProperty("itemDescription");
        this.itemBenefit = parameter.getProperty("itemBenefit");
        this.itemRareness = parameter.getProperty("itemRareness");

        TargetElementResolver resolver = new TargetElementResolver(nifty.getCurrentScreen(), element);
        targetElement = resolver.resolve(parameter.getProperty("targetElement"));       
    }

    @SuppressWarnings("null")
    @Override
    public void execute(@Nonnull Element element, float effectTime, @Nullable Falloff falloff, @Nonnull NiftyRenderEngine r) {
        if (targetElement != null && hasItem) {
            targetElement.getRenderer(PanelRenderer.class).setBackgroundColor(switch (itemRareness) {
                case "Common" -> new Color("#000c");
                case "Rare" -> new Color("#4a0c");
                case "Silver" -> new Color("#666c");
                case "Golden" -> new Color("#e80c");
                case "Legendary" -> new Color("#a2ac");
                default -> Color.BLACK;
            });

            targetElement.findElementById("content").getRenderer(TextRenderer.class).setText(
                itemName
                + "\nОпис: "+ itemDescription
                + (!itemBenefit.equals("") ? "\nКористь: "+ itemBenefit : "")
            );

            targetElement.setConstraintX(new SizeValue(element.getX() + element.getWidth() - 128 + "px"));
            targetElement.setConstraintY(new SizeValue(element.getY() + element.getHeight() - 128 + "px"));
            targetElement.show();

            nifty.getCurrentScreen().layoutLayers();
        }
    }

    @Override
    public void deactivate() {
        if (targetElement != null && hasItem)
            targetElement.hide();
    }

}
