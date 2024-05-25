package org.konceptosociala.kareladventures.state;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.ui.InventoryCell;
import org.konceptosociala.kareladventures.ui.inventory_cell_id.InvalidCellIdException;
import org.konceptosociala.kareladventures.ui.inventory_cell_id.InventoryCellId;

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

        nifty.addScreen("inventory_screen", new ScreenBuilder("HUD screen") {{
            controller(InventoryState.this);

            layer(new LayerBuilder("inventory_layer") {{
                childLayoutCenter();

                panel(new PanelBuilder("inventory_panel") {{
                    childLayoutCenter();

                    image(new ImageBuilder("inventory_image"){{
                        filename("Textures/ui/inventory.png");
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

                            panel(new PanelBuilder("inv_col_1"){{
                                childLayoutVertical();

                                panel(new InventoryCell(new InventoryCellId("inv_cell_1_1"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_1_2"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_1_3"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_1_4"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_1_5"), inventory));
                            }});

                            panel(new PanelBuilder("inv_col_2"){{
                                childLayoutVertical();

                                panel(new InventoryCell(new InventoryCellId("inv_cell_2_1"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_2_2"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_2_3"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_2_4"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_2_5"), inventory));
                            }});

                            panel(new PanelBuilder("inv_col_3"){{
                                childLayoutVertical();

                                panel(new InventoryCell(new InventoryCellId("inv_cell_3_1"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_3_2"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_3_3"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_3_4"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_3_5"), inventory));
                            }});

                            panel(new PanelBuilder("inv_col_4"){{
                                childLayoutVertical();

                                panel(new InventoryCell(new InventoryCellId("inv_cell_4_1"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_4_2"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_4_3"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_4_4"), inventory));
                                panel(new InventoryCell(new InventoryCellId("inv_cell_4_5"), inventory));
                            }});
                        }});
                    } catch (InvalidCellIdException e) {
                        e.printStackTrace();
                    }
                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("inventory_screen");
    }

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
                    selectedItemCell.setBackgroundColor(new Color(0, 0, 0, 0));

                    var selectedItemIcon = getCellIcon(selectedItemId.get().toString());
                    var newItemIcon = getCellIcon(id.toString());

                    var itemToReplace = inventory.getItem(id);
                    if (itemToReplace.isEmpty()) {
                        inventory.setItem(selectedItem, id);
                        inventory.removeItem(selectedItemId.get());

                        selectedItemId = Optional.empty();
                    } else {
                        inventory.setItem(selectedItem, id);
                        inventory.setItem(itemToReplace.get(), selectedItemId.get());
                    }

                    selectedItemIcon.setImage(createImage(InventoryCell.TRANSPARENT_ICON));
                    newItemIcon.setImage(createImage(selectedItem.getIconPath()));
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

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

    }

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
        this.nifty = nifty;
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }

    @Override
    protected void onDisable() {
        inputManager.setCursorVisible(false);
        nifty.gotoScreen("hud_screen");
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
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
    
}
