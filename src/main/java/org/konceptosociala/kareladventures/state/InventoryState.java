package org.konceptosociala.kareladventures.state;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
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
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

public class InventoryState extends BaseAppState implements ActionListener, ScreenController {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;
    private Inventory inventory;
    private Optional<InventoryCellId> selectedItemId;

    public InventoryState(Inventory inventory) {
        this.inventory = inventory;
        this.selectedItemId = Optional.empty();
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

        nifty.addScreen("inventory_screen", new ScreenBuilder("Inventory screen") {{
            controller(InventoryState.this);

            layer(new LayerBuilder("inventory_layer") {{
                childLayoutCenter();

                panel(new PanelBuilder("inventory_panel") {{
                    childLayoutCenter();

                    image(new ImageBuilder("inventory_image"){{
                        filename("Interface/inventory.png");
                        width("512px");
                        height("800px");
                    }});

                    try {
                        panel(new PanelBuilder("inventory_columns_panel"){{
                            childLayoutHorizontal();

                            marginTop("84px");

                            panel(new PanelBuilder("inv_col_0"){{
                                childLayoutVertical();

                                panel(new InventoryCell(new InventoryCellId("inv_cell_helmet"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_chestplate"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_leggings"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_boots"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_weapon"), inventory));
                            }});

                            for (int i = 1; i <= 4; i++) {
                                final int ic = i;
                                panel(new PanelBuilder("inv_col_"+i){{
                                    childLayoutVertical();
    
                                    for (int j = 1; j <= 5; j++) {
                                        panel(new InventoryCell(new InventoryCellId("inv_cell_"+ic+"_"+j), inventory));
                                    }
                                }});
                            }
                        }});
                    } catch (InvalidCellIdException e) {
                        e.printStackTrace();
                    }
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
                    var newItemIcon = getCellIcon(id.toString());
                    var itemToReplace = inventory.getItem(id);
                    var namedCell = id.getNamedCell();
                    
                    if (namedCell.isEmpty()
                        || (namedCell.isPresent() && namedCell.get().toItemKind().equals(selectedItem.getItemKind()))
                    ){
                        // Accept replace
                        if (itemToReplace.isEmpty()) {
                            inventory.setItem(selectedItem, id);
                            inventory.removeItem(selectedItemId.get());

                            selectedItemCell.setBackgroundColor(new Color(0, 0, 0, 0));
                            selectedItemIcon.setImage(createImage(InventoryCell.TRANSPARENT_ICON));
                            newItemIcon.setImage(createImage(selectedItem.getIconPath()));

                            selectedItemId = Optional.empty();
                        } else if (namedCell.isPresent() 
                            || (namedCell.isEmpty() && itemToReplace.get().getItemKind().equals(selectedItem.getItemKind()))
                            || (namedCell.isEmpty() && selectedItemId.get().getGridCell().isPresent())
                        ){
                            inventory.setItem(selectedItem, id);
                            inventory.setItem(itemToReplace.get(), selectedItemId.get());

                            selectedItemCell.setBackgroundColor(new Color(0, 0, 0, 0));
                            selectedItemIcon.setImage(createImage(itemToReplace.get().getIconPath()));
                            newItemIcon.setImage(createImage(selectedItem.getIconPath()));

                            selectedItemId = Optional.empty();
                        }
                    }
                }
            } else {
                var itemToReplace = inventory.getItem(id);
                if (itemToReplace.isEmpty())
                    return;

                cell.setBackgroundColor(new Color(1, 1, 1, 0.1f));
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
        nifty.gotoScreen("hud_screen");
    }

    @SuppressWarnings("null")
    private PanelRenderer getCell(String cellId) {
        var screen = nifty.getScreen("inventory_screen");
        return screen
            .findElementById(cellId)
            .getRenderer(PanelRenderer.class);
    }

    @SuppressWarnings("null")
    private ImageRenderer getCellIcon(String cellId) {
        var screen = nifty.getScreen("inventory_screen");
        return screen
            .findElementById(cellId+"_icon")
            .getRenderer(ImageRenderer.class);
    }

    @SuppressWarnings("null")
    private NiftyImage createImage(String path) {
        var screen = nifty.getScreen("inventory_screen");
        return nifty.getRenderEngine().createImage(
            screen, 
            path, 
            true
        );
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
