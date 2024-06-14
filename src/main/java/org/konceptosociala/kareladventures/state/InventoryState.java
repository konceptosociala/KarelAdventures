package org.konceptosociala.kareladventures.state;

import java.util.Optional;

import javax.annotation.Nonnull;

import static org.konceptosociala.kareladventures.KarelAdventures.createImage;
import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.ui.InterfaceBlur;
import org.konceptosociala.kareladventures.ui.InvalidCellIdException;
import org.konceptosociala.kareladventures.ui.inventory.*;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

public class InventoryState extends BaseAppState implements ActionListener, ScreenController {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;
    private InterfaceBlur interfaceBlur;
    private Inventory inventory;
    private Optional<InventoryCellId> selectedItemId;

    public InventoryState(Inventory inventory, InterfaceBlur blur) {
        this.inventory = inventory;
        this.selectedItemId = Optional.empty();
        this.interfaceBlur = blur;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);
        interfaceBlur.setEnabled(true);

        nifty.addScreen("inventory_screen", new ScreenBuilder("Inventory screen") {{
            controller(InventoryState.this);

            layer(new LayerBuilder("inventory_layer") {{
                childLayoutCenter();

                panel(new PanelBuilder("inventory_panel") {{
                    childLayoutCenter();

                    image(new ImageBuilder("inventory_image"){{
                        filename("Interface/UI/Transparent center/panel-transparent-center-003.png");
                        imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                        width("512px");
                        height("512px");
                    }});

                    try {
                        panel(new PanelBuilder("inventory_columns_panel"){{
                            childLayoutHorizontal();
                            
                            panel(new PanelBuilder("inv_col_0"){{
                                childLayoutVertical();

                                panel(new InventoryCell(new InventoryCellId("inv_cell_helmet"), inventory, "Interface/Inventory/helmet.png"));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_chestplate"), inventory, "Interface/Inventory/chestplate.png"));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_leggings") , inventory, "Interface/Inventory/leggings.png"));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_boots"), inventory, "Interface/Inventory/boots.png"));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_weapon"), inventory, "Interface/Inventory/sword.png"));
                            }});

                            for (int i = 1; i <= 4; i++) {
                                final int ic = i;
                                panel(new PanelBuilder("inv_col_"+i){{
                                    childLayoutVertical();
    
                                    for (int j = 1; j <= 5; j++) {
                                        panel(new InventoryCell(new InventoryCellId("inv_cell_"+ic+"_"+j), inventory, null));
                                    }
                                }});
                            }
                        }});
                    } catch (InvalidCellIdException e) {
                        e.printStackTrace();
                    }
                }});
                
            }});

            layer(new LayerBuilder("hint_layer") {{
                childLayoutAbsolute();

                panel(new PanelBuilder("hint_panel") {{
                    childLayoutVertical();
                    visible(false);
                    padding("40px,90px,40px,90px");
                    backgroundColor(new Color("#000c"));

                    text(new TextBuilder("content") {{
                        font("Interface/Fonts/Ubuntu-C.ttf");
                        text("                            ");
                        align(Align.Center);
                        valign(VAlign.Center);
                    }});
                }});
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("inventory_screen");
    }

    // UI callbacks

    public void selectCell(String cellId) {
        try {
            var id = new InventoryCellId(cellId);
            var cell = getCell(cellId);

            if (selectedItemId.isPresent()) {
                if (id.equals(selectedItemId.get())) {
                    cell.setBackgroundColor(new Color(0, 0, 0, 0));
                    selectedItemId = Optional.empty();
                } else {
                    var selectedItem = inventory.getItem(selectedItemId.get()).get();
                    var selectedItemCell = getCell(selectedItemId.get().toString());
                    var selectedItemIcon = getCellIcon(selectedItemId.get().toString());
                    var selectedItemPlaceholderIcon = getCellPlaceholderIcon(selectedItemId.get().toString());
                    var newItemIcon = getCellIcon(id.toString());
                    var newItemPlaceholderIcon = getCellPlaceholderIcon(id.toString());
                    var itemToReplace = inventory.getItem(id);
                    var namedCell = id.getNamedCell();

                    var newItemElementParams = getCellElement(id.toString())
                        .getEffects(EffectEventId.onHover, InventoryHint.class)
                        .get(0)
                        .getParameters();

                    var selectedItemParams = getCellElement(selectedItemId.get().toString())
                        .getEffects(EffectEventId.onHover, InventoryHint.class)
                        .get(0)
                        .getParameters();

                    newItemElementParams.setProperty("hasItem", "true");
                    newItemElementParams.setProperty("itemName", selectedItem.getName());
                    newItemElementParams.setProperty("itemDescription", selectedItem.getDescription());  
                    newItemElementParams.setProperty("itemRareness", selectedItem.getItemRareness().toString());   
                    newItemElementParams.setProperty("itemBenefit", 
                        selectedItem.getBenefit().isPresent()
                            ? String.valueOf(selectedItem.getBenefit().get())
                            : ""
                    );               
                    if (namedCell.isEmpty()
                        || (namedCell.isPresent() && namedCell.get().toItemKind().equals(selectedItem.getItemKind()))
                    ){
                        // Accept replace
                        if (itemToReplace.isEmpty()) {
                            inventory.setItem(selectedItem, id);
                            inventory.removeItem(selectedItemId.get());

                            selectedItemCell.setBackgroundColor(new Color(0, 0, 0, 0));
                            selectedItemIcon.setImage(createImage(nifty, "inventory_screen", InventoryCell.TRANSPARENT_ICON, true));
                            newItemIcon.setImage(createImage(nifty, "inventory_screen", selectedItem.getIconPath(), true));
                            
                            if (newItemPlaceholderIcon != null)
                                newItemPlaceholderIcon.setVisible(false);

                            if (selectedItemPlaceholderIcon != null)
                                selectedItemPlaceholderIcon.setVisible(true);

                            selectedItemId = Optional.empty();

                            selectedItemParams.setProperty("hasItem", "false");
                            selectedItemParams.setProperty("itemName", "");
                            selectedItemParams.setProperty("itemDescription", "");
                            selectedItemParams.setProperty("itemRareness", "");
                            selectedItemParams.setProperty("itemBenefit", ""); 
                        } else if (namedCell.isPresent() 
                            || (namedCell.isEmpty() && itemToReplace.get().getItemKind().equals(selectedItem.getItemKind()))
                            || (namedCell.isEmpty() && selectedItemId.get().getGridCell().isPresent())
                        ){
                            inventory.setItem(selectedItem, id);
                            inventory.setItem(itemToReplace.get(), selectedItemId.get());

                            selectedItemCell.setBackgroundColor(new Color(0, 0, 0, 0));
                            
                            selectedItemIcon.setImage(createImage(nifty, "inventory_screen", itemToReplace.get().getIconPath(), true));
                            newItemIcon.setImage(createImage(nifty, "inventory_screen", selectedItem.getIconPath(), true));

                            selectedItemId = Optional.empty();

                            selectedItemParams.setProperty("hasItem", "true");
                            selectedItemParams.setProperty("itemName", itemToReplace.get().getName());
                            selectedItemParams.setProperty("itemDescription", itemToReplace.get().getDescription());
                            selectedItemParams.setProperty("itemRareness", itemToReplace.get().getItemRareness().toString());
                            selectedItemParams.setProperty("itemBenefit", 
                                itemToReplace.get().getBenefit().isPresent()
                                    ? String.valueOf(itemToReplace.get().getBenefit().get())
                                    : ""
                            ); 
                        }
                    }
                }
            } else {
                var itemToReplace = inventory.getItem(id);
                if (itemToReplace.isEmpty())
                    return;

                cell.setBackgroundColor(new Color(1, 1, 1, 0.5f));
                selectedItemId = Optional.of(id);
            }
        } catch (InvalidCellIdException e) {
            e.printStackTrace();
        }
    }

    // Other methods

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }

    @Override
    protected void onDisable() {
        inputManager.setCursorVisible(false);
        interfaceBlur.setEnabled(false);
        nifty.gotoScreen("hud_screen");
    }

    @SuppressWarnings("null")
    private PanelRenderer getCell(String cellId) {
        var screen = nifty.getScreen("inventory_screen");
        return screen
            .findElementById(cellId+"_select")
            .getRenderer(PanelRenderer.class);
    }

    @SuppressWarnings("null")
    private Element getCellElement(String cellId) {
        return nifty
            .getScreen("inventory_screen")
            .findElementById(cellId);
    }

    @SuppressWarnings("null")
    private Element getCellPlaceholderIcon(String cellId) {
        var screen = nifty.getScreen("inventory_screen");
        return screen
            .findElementById(cellId+"_placeholder_icon");
    }

    @SuppressWarnings("null")
    private ImageRenderer getCellIcon(String cellId) {
        var screen = nifty.getScreen("inventory_screen");
        return screen
            .findElementById(cellId+"_icon")
            .getRenderer(ImageRenderer.class);
    }

    @Override
    protected void cleanup(Application app) {}

    @Override
    public void onStartScreen() {}

    @Override
    public void onEndScreen() {}

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {}
    
}
