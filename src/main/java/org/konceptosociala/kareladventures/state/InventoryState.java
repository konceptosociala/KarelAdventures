package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.game.inventory.Weapon;
import org.konceptosociala.kareladventures.game.inventory.armor.Boots;
import org.konceptosociala.kareladventures.game.inventory.armor.Chestplate;
import org.konceptosociala.kareladventures.game.inventory.armor.Helmet;
import org.konceptosociala.kareladventures.game.inventory.armor.Leggings;
import org.konceptosociala.kareladventures.ui.InventoryCell;

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
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class InventoryState extends BaseAppState implements ActionListener, ScreenController {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;
    private Inventory inventory;

    public InventoryState(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @SuppressWarnings("null")
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

                    panel(new PanelBuilder("inventory_columns_panel"){{
                        childLayoutHorizontal();

                        marginTop("84px");

                        panel(new PanelBuilder("inv_col_1"){{
                            childLayoutVertical();

                            panel(new InventoryCell("inv_cell_1_1", 0, Helmet.class));
                            panel(new InventoryCell("inv_cell_1_2", 1, Chestplate.class));
                            panel(new InventoryCell("inv_cell_1_3", 2, Leggings.class));
                            panel(new InventoryCell("inv_cell_1_4", 3, Boots.class));
                            panel(new InventoryCell("inv_cell_1_5", 4, Weapon.class));
                        }});

                        panel(new PanelBuilder("inv_col_2"){{
                            childLayoutVertical();

                            panel(new InventoryCell("inv_cell_2_1", 0, Item.class));
                            panel(new InventoryCell("inv_cell_2_2", 1, Item.class));
                            panel(new InventoryCell("inv_cell_2_3", 2, Item.class));
                            panel(new InventoryCell("inv_cell_2_4", 3, Item.class));
                            panel(new InventoryCell("inv_cell_2_5", 4, Item.class));
                        }});

                        panel(new PanelBuilder("inv_col_3"){{
                            childLayoutVertical();

                            panel(new InventoryCell("inv_cell_3_1", 5, Item.class));
                            panel(new InventoryCell("inv_cell_3_2", 6, Item.class));
                            panel(new InventoryCell("inv_cell_3_3", 7, Item.class));
                            panel(new InventoryCell("inv_cell_3_4", 8, Item.class));
                            panel(new InventoryCell("inv_cell_3_5", 9, Item.class));
                        }});

                        panel(new PanelBuilder("inv_col_4"){{
                            childLayoutVertical();

                            panel(new InventoryCell("inv_cell_4_1", 10, Item.class));
                            panel(new InventoryCell("inv_cell_4_2", 11, Item.class));
                            panel(new InventoryCell("inv_cell_4_3", 12, Item.class));
                            panel(new InventoryCell("inv_cell_4_4", 13, Item.class));
                            panel(new InventoryCell("inv_cell_4_5", 14, Item.class));
                        }});

                        panel(new PanelBuilder("inv_col_5"){{
                            childLayoutVertical();

                            panel(new InventoryCell("inv_cell_5_1", 15, Item.class));
                            panel(new InventoryCell("inv_cell_5_2", 16, Item.class));
                            panel(new InventoryCell("inv_cell_5_3", 17, Item.class));
                            panel(new InventoryCell("inv_cell_5_4", 18, Item.class));
                            panel(new InventoryCell("inv_cell_5_5", 19, Item.class));
                        }});
                    }});
                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("inventory_screen");

        var screen = nifty.getScreen("inventory_screen");
        screen
            .findElementById("inv_cell_1_1_icon")
            .getRenderer(ImageRenderer.class)
            .setImage(
                nifty.getRenderEngine().createImage(
                    screen, 
                    "Textures/ui/items/java.png", 
                    true
                )
            );
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
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
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }
    
}
