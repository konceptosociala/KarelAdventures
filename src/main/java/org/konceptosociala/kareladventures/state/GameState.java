package org.konceptosociala.kareladventures.state;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.tools.Color;
import lombok.Getter;

import static org.konceptosociala.kareladventures.KarelAdventures.LOG;

import java.util.HashMap;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.Sun;
import org.konceptosociala.kareladventures.game.World;
import org.konceptosociala.kareladventures.game.enemies.BulletCollisionListener;
import org.konceptosociala.kareladventures.game.npc.Dialog;
import org.konceptosociala.kareladventures.game.npc.NPC;
import org.konceptosociala.kareladventures.game.player.AttackType;
import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;
import org.konceptosociala.kareladventures.utils.InteractableNode;
import org.konceptosociala.kareladventures.utils.Level;
import org.konceptosociala.kareladventures.utils.SaveLoadException;
import org.konceptosociala.kareladventures.utils.SaveLoader;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.LightProbe;
import com.jme3.math.Quaternion;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.DefaultScreenController;

@Getter
public class GameState extends BaseAppState  {
    private KarelAdventures app;
    private AssetManager assetManager;
    private AppStateManager appStateManager;
    private BulletAppState bulletAppState;
    private InputManager inputManager;
    private Nifty nifty;
    private Node rootNode;
    
    private KarelFarmState karelFarmState;
    private PauseState pauseState;
    private DialogState dialogState;
    private InventoryState inventoryState;
    private ChaseCamera chaseCam;
    private Sun sun;
    private LightProbe probe;
    private World world;
    private Player player;
    private Node enemyRoot;
    private Node interactableRoot;
    private Level currentLevel;

    public GameState(LoadGameState loadGameState) {
        bulletAppState = loadGameState.getBulletAppState();
        assetManager = loadGameState.getAssetManager();
        dialogState = loadGameState.getDialogState();
        inventoryState = loadGameState.getInventoryState();
        karelFarmState = loadGameState.getKarelFarmState();
        chaseCam = loadGameState.getChaseCam();
        sun = loadGameState.getSun();
        world = loadGameState.getWorld();
        player = loadGameState.getPlayer();
        rootNode = loadGameState.getRootNode();
        enemyRoot = loadGameState.getEnemyRoot();
        interactableRoot = loadGameState.getInteractableRoot();
        currentLevel = loadGameState.getCurrentLevel();
        probe = loadGameState.getProbe();
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
        BulletCollisionListener bulletCollisionListener = new BulletCollisionListener();
        bulletCollisionListener.setBulletAppState(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(bulletCollisionListener);
        initPlayer();
        initEnemies();
        initControls();

        pauseState = new PauseState(this);
        appStateManager.attach(pauseState);
        pauseState.setEnabled(false);
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(false);
        bulletAppState.setEnabled(true);
        chaseCam.setEnabled(true);

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

    public void save() {
        var saveLoader = new SaveLoader(
            player.getHealth(), 
            player.getEnergy(), 
            player.getInventory(), 
            dialogsToMap(), 
            player.getLocalTranslation(), 
            currentLevel
        );

        try {
            saveLoader.save("data/Saves/karel.sav");
        } catch (SaveLoadException e) {
            LOG.severe("Cannot save game: "+e.getMessage());
        }

        LOG.info("Game saved.");
    }

    @SuppressWarnings("null")
    @Override
    public void update(float tpf) {
        player.update(tpf);
        for (Spatial i : enemyRoot.getChildren()) {
            if (i instanceof IUpdatable) {
                ((IUpdatable) i).update(tpf);
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

    @Override
    protected void cleanup(Application app) {
        save();

        bulletAppState.cleanup();
        inputManager.clearMappings();
        appStateManager.detach(karelFarmState);
        appStateManager.detach(pauseState);
        appStateManager.detach(dialogState);
        appStateManager.detach(inventoryState);
        appStateManager.detach(appStateManager.getState(EnvironmentCamera.class));
        chaseCam.cleanupWithInput(inputManager);
        rootNode.removeLight(probe);
        rootNode.removeLight(sun);
        sun.cleanup();
        
        rootNode.detachAllChildren();
        app.getCamera().setRotation(new Quaternion());
    }

    @Override
    protected void onDisable() {
        bulletAppState.setEnabled(false);
    }

    private void initPlayer(){
        this.player.setThisGameState(this);
    }

    private void initEnemies(){
        for (Spatial i : enemyRoot.getChildren()){
            if(i instanceof IAmEnemy){
                ((IAmEnemy) i).setThisGameState(this);
            }
            /*if(i instanceof EnemyTower){
                ((EnemyTower) i).setThisGameState(this);
            }*/
        }
    }

    private void initControls() {
        inputManager.addMapping("ESCAPE", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("EXIT", new KeyTrigger(KeyInput.KEY_F4));
        inputManager.addMapping("INVENTORY", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("INTERACT", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("FORWARD", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("BACKWARD", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("LEFTWARD", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RIGHTWARD", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("DASH", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("ATTACK", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("RETURN", new KeyTrigger(KeyInput.KEY_RETURN));

        // Enable listeners
        inputManager.addListener(actionListener, new String[]{"EXIT","INVENTORY","JUMP","INTERACT","DASH","ATTACK","ESCAPE", "RETURN"});
        inputManager.addListener(analogListener, new String[]{"FORWARD","BACKWARD","LEFTWARD","RIGHTWARD"});
    }

    final private ActionListener actionListener = new ActionListener() {
        public void onAction(String action, boolean isPressed, float tpf) {
            if (action.equals("ESCAPE") && isPressed) {
                if (inventoryState.isEnabled()) {
                    chaseCam.setEnabled(true);
                    inventoryState.setEnabled(false);
                } else if (dialogState.isEnabled()) {
                    chaseCam.setEnabled(true);
                    dialogState.setEnabled(false);
                } else if (karelFarmState.isEnabled()) {
                    chaseCam.setEnabled(true);
                    karelFarmState.setEnabled(false);
                } else if (pauseState.isEnabled()) {
                    pauseState.setEnabled(false);
                    GameState.this.setEnabled(true);
                } else {
                    chaseCam.setEnabled(false);
                    pauseState.setEnabled(true);
                    GameState.this.setEnabled(false);
                }
            }

            if (action.equals("RETURN") && isPressed) {
                if (dialogState.isEnabled()) {
                    dialogState.nextMessage();
    
                    if (!dialogState.isEnabled())
                        chaseCam.setEnabled(true);
                }

                if (karelFarmState.isEnabled()) {
                    karelFarmState.enterCommand();
                }
            }

            if (inventoryState.isEnabled() 
                || dialogState.isEnabled()
                || pauseState.isEnabled()
                || karelFarmState.isEnabled())
                return;

            if (action.equals("INVENTORY") && isPressed) {
                inventoryState.setEnabled(true);
                chaseCam.setEnabled(false);
            }

            if (action.equals("INTERACT") && isPressed) {
                for (Spatial i : interactableRoot.getChildren()) {
                    if (i instanceof InteractableNode) {
                        ((InteractableNode) i).interact(GameState.this, player);
                    }
                }
            }

            if (action.equals("EXIT") && !isPressed) {
                System.exit(0);
            }
            
            if (action.equals("JUMP") && isPressed) {
                player.jump();
            }
            
            if (action.equals("DASH") && isPressed) {
                player.roll();
            }

            if (action.equals("ATTACK") && isPressed) {
                player.attack(AttackType.Melee);
            }
        }
    };

    final private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String action, float value, float tpf) {
            if (inventoryState.isEnabled() 
                || dialogState.isEnabled() 
                || pauseState.isEnabled()
                || karelFarmState.isEnabled())
                return;

            if (action.equals("FORWARD") && value>0) {
                player.moveForward(-value,chaseCam.getHorizontalRotation());
            } else if (action.equals("BACKWARD") && value>0) {
                player.moveForward(value,chaseCam.getHorizontalRotation());
            }

            if (action.equals("RIGHTWARD") && value>0) {
                player.moveSideward(-value,chaseCam.getHorizontalRotation());
            } else if (action.equals("LEFTWARD") && value>0) {
                player.moveSideward(value,chaseCam.getHorizontalRotation());
            }
        }
    };

    private HashMap<String, Dialog> dialogsToMap() {
        var dialogsMap = new HashMap<String, Dialog>();

        for (var spatial : interactableRoot.getChildren()) {
            if (spatial instanceof NPC) {
                var npc = (NPC) spatial;
                dialogsMap.put(npc.getName(), npc.getDialog());
            }
        }

        return dialogsMap;
    }
}
