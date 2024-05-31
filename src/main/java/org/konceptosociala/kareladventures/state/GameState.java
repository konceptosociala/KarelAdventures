package org.konceptosociala.kareladventures.state;

import de.lessvoid.nifty.tools.Color;
import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.player.Player;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.DefaultScreenController;

public class GameState extends BaseAppState  {
    private KarelAdventures app;
    private AppStateManager appStateManager;
    private BulletAppState bulletAppState;
    private InputManager inputManager;
    private Nifty nifty;

    private ChaseCamera chaseCam;
    private Player player;

    public GameState(ChaseCamera chaseCam, Player player){
        this.chaseCam = chaseCam;
        this.player = player;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.app.getViewPort().setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.bulletAppState = this.app.getBulletAppState();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void cleanup(Application app) {
        this.app.getRootNode().detachAllChildren();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(false);
        bulletAppState.setEnabled(true);

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
                    text(new TextBuilder("data"){{
                        text("Health: ???");
                        color(Color.BLACK);
                        font("Interface/Fonts/Default.fnt");
                    }});

                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("hud_screen");
    }

    @Override
    protected void onDisable() {
        bulletAppState.setEnabled(false);
    }

    @SuppressWarnings("null")
    @Override
    public void update(float tpf) {
        nifty
            .getScreen("hud_screen")
            .findElementById("health")
            .getRenderer(TextRenderer.class)
            .setText("Health: " + player.getHealth().getValue());

        nifty
            .getScreen("hud_screen")
            .findElementById("energy")
            .getRenderer(TextRenderer.class)
            .setText("Energy: " + player.getEnergy().getValue());

        nifty
            .getScreen("hud_screen")
            .findElementById("data")
            .getRenderer(TextRenderer.class)
            .setText("ф: "+(int)Math.toDegrees(chaseCam.getHorizontalRotation()) + ";" + (int)Math.toDegrees(player.getCharacterCollider().getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0))));
    }
}
