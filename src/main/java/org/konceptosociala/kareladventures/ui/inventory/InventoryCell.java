package org.konceptosociala.kareladventures.ui.inventory;

import java.util.Optional;

import javax.annotation.Nullable;

import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.game.inventory.Item;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.tools.Color;
import lombok.Getter;

@Getter
public class InventoryCell extends PanelBuilder {
    public static final String TRANSPARENT_ICON = "Interface/UI/transparent.png";

    private final InventoryCellId cellId;
    private Optional<Item> cellItem;

    public InventoryCell(InventoryCellId id, Inventory inventory, @Nullable String placeholderIcon) {
        super(id.toString());
        this.cellId = id;
        this.cellItem = Optional.empty();

        childLayoutCenter();
        width("80px");
        height("80px");
        marginTop("2px");
        marginBottom("2px");
        marginLeft("2px");
        marginRight("2px");
        interactOnClick("selectCell("+id+")");
        
        image(new ImageBuilder(id+"_placeholder") {{
            width("75px");
            height("75px");
            filename("Interface/UI/Transparent center/panel-transparent-center-005.png");
            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
        }});

        var item = inventory.getItem(id);

        if (placeholderIcon != null) {
            image(new ImageBuilder(id+"_placeholder_icon") {{
                filename(placeholderIcon);
                width("48px");
                height("48px");

                if (item.isPresent())
                    visible(false);
            }});
        }

        panel(new PanelBuilder(id+"_select") {{
            backgroundColor(Color.NONE);
        }});

        image(new ImageBuilder(id+"_icon"){{
            width("64px");
            height("64px");

            if (item.isPresent())
                filename(item.get().getIconPath());
            else
                filename(TRANSPARENT_ICON);
        }});

        onHoverEffect(new HoverEffectBuilder("inventory-hint") {{
            effectParameter("targetElement", "hint_panel");
            effectParameter("hasItem", String.valueOf(item.isPresent()));
            effectParameter("itemName", item.isPresent() ? item.get().getName() : "");
            effectParameter("itemDescription", item.isPresent() ? item.get().getDescription() : "");
            effectParameter("itemRareness", 
                item.isPresent()
                    ? item.get().getItemRareness().toString()
                    : ""
            );
            effectParameter("itemBenefit", 
                item.isPresent() && item.get().getBenefit().isPresent()
                    ? String.valueOf(item.get().getBenefit().get())
                    : ""
            );
        }});
    }
}
