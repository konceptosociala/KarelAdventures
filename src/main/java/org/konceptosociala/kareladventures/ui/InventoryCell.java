package org.konceptosociala.kareladventures.ui;

import java.util.Optional;
import org.konceptosociala.kareladventures.game.inventory.Item;

import de.lessvoid.nifty.builder.*;
import lombok.Getter;

@Getter
public class InventoryCell extends PanelBuilder {
    private Class<? extends Item> cellType;
    private Optional<Item> cellItem;
    private int idx;

    public InventoryCell(String id, int idx, Class<? extends Item> cellType) {
        super(id);
        this.cellItem = Optional.empty();
        this.cellType = cellType;
        this.idx = idx;

        childLayoutCenter();
        width("81px");
        height("81px");
        marginTop("2px");
        marginBottom("2px");
        marginLeft("2px");
        marginRight("2px");

        image(new ImageBuilder(id+"_icon"){{
            width("72px");
            height("72px");
        }});
    }

    public boolean putItem(Item item) {
        if (item.getClass() != cellType && item.getClass().getSuperclass() != cellType)
            return false;

        cellItem = Optional.of(item);
        return true;
    }

    public void clearItem() {
        cellItem = Optional.empty();
    }
}
