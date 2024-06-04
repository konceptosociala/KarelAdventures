package org.konceptosociala.kareladventures.ui.inventory;

import java.util.Optional;

import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.game.inventory.Item;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.tools.Color;
import lombok.Getter;

@Getter
public class InventoryCell extends PanelBuilder {
    public static final String TRANSPARENT_ICON = "Interface/transparent.png";

    private InventoryCellId cellId;
    private Optional<Item> cellItem;

    public InventoryCell(InventoryCellId id, Inventory inventory) {
        super(id.toString());
        this.cellItem = Optional.empty();

        childLayoutCenter();
        width("80px");
        height("80px");
        marginTop("2px");
        marginBottom("2px");
        marginLeft("2px");
        marginRight("2px");
        interactOnClick("selectCell("+id+")");
        backgroundColor(new Color(0, 0, 0, 0));

        image(new ImageBuilder(id+"_icon"){{
            width("64px");
            height("64px");

            var item = inventory.getItem(id);
            if (item.isPresent())
                filename(item.get().getIconPath());
            else
                filename(TRANSPARENT_ICON);
                
        }});
    }
}
