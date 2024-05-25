package org.konceptosociala.kareladventures.state;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.player.Player;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.DefaultScreenController;

public class GameState extends BaseAppState implements ActionListener {    
    private KarelAdventures app;
    private AssetManager assetManager;
    private AppStateManager appStateManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private InventoryState inventoryState;
    private Nifty nifty;
    private ChaseCamera chaseCam;
    private DirectionalLight sun;
    private Player player;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.assetManager = this.app.getAssetManager();
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.bulletAppState = this.app.getBulletAppState();
        this.nifty = this.app.getNifty();
        this.player = new Player(assetManager, bulletAppState);
        this.sun = new DirectionalLight(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal(), ColorRGBA.White);
        this.chaseCam = initChaseCam();

        this.app.getRootNode().attachChild(player);
        this.app.getRootNode().addLight(sun);

        inventoryState = new InventoryState(player.getInventory());
        appStateManager.attach(inventoryState);
        inventoryState.setEnabled(false);

        inputManager.addMapping("inventory", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addListener(this, new String[]{
            "inventory",
        });
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(false);

        nifty.addScreen("hud_screen", new ScreenBuilder("HUD screen") {{
            controller(new DefaultScreenController());

            layer(new LayerBuilder("hud_layer") {{
                childLayoutHorizontal();

                panel(new PanelBuilder("hud_panel") {{
                    childLayoutVertical();

                    text(new TextBuilder("health"){{
                        text("Health: ???");
                        font("Interface/Fonts/Default.fnt");
                    }});

                    text(new TextBuilder("energy"){{
                        text("Energy: ???");
                        font("Interface/Fonts/Default.fnt");
                    }});
                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("hud_screen");
    }

    @Override
    public void onAction(String action, boolean isPressed, float tpf) {
        switch (action) {
            case "inventory":
                if (isPressed) 
                    inventoryState.setEnabled(!inventoryState.isEnabled());
                break;
        
            default:
                break;
        }
    }

    @SuppressWarnings("null")
    @Override
    public void update(float tpf) {
        nifty
            .getScreen("hud_screen")
            .findElementById("health")
            .getRenderer(TextRenderer.class)
            .setText("Health: "+player.getHealth().getValue());

        nifty
            .getScreen("hud_screen")
            .findElementById("energy")
            .getRenderer(TextRenderer.class)
            .setText("Energy: "+player.getEnergy().getValue());
    }

    @Override
    protected void onDisable() {
    }
    
    @Override
    protected void cleanup(Application app) {
    }

    private ChaseCamera initChaseCam() {
        var chaseCam = new ChaseCamera(this.app.getCamera(), player, inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setDragToRotate(false);
        return chaseCam;
    }
}
