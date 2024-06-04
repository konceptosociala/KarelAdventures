package org.konceptosociala.kareladventures.state;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.tools.Color;
import lombok.Getter;
import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.enemies.Enemy;
import org.konceptosociala.kareladventures.game.enemies.NavMesh;
import org.konceptosociala.kareladventures.game.player.IUpdatable;
import org.konceptosociala.kareladventures.game.player.Player;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.DefaultScreenController;

@SuppressWarnings("unused")
public class GameState extends BaseAppState  {
    private KarelAdventures app;
    private AppStateManager appStateManager;
    private BulletAppState bulletAppState;
    private InputManager inputManager;
    private Nifty nifty;
    @Getter
    private Player player;
    private NavMesh navMesh;
    @Getter
    private Node enemyRoot;

    public GameState(Player player,Node enemyRoot){
        this.player = player;
        this.enemyRoot = enemyRoot;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.app.getViewPort().setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.bulletAppState = this.app.getBulletAppState();
        this.nifty = this.app.getNifty();
        initPlayer();
        initEnemies();
    }
    private void initEnemies(){
        for (Spatial i: enemyRoot.getChildren()){
            if(i instanceof Enemy){
                ((Enemy) i).setThisGameState(this);
            }
        }
    }

    private void initPlayer(){
        this.player.setThisGameState(this);
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
        player.update();
        for(Spatial i: enemyRoot.getChildren()){
            if(i instanceof IUpdatable){
                ((IUpdatable) i).update();
            }
        }
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
    }
}
