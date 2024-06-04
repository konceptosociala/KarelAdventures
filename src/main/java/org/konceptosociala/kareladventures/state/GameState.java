package org.konceptosociala.kareladventures.state;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
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

public class GameState extends BaseAppState  {
    private KarelAdventures app;
    private AssetManager assetManager;
    private AppStateManager appStateManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private InventoryState inventoryState;
    private Nifty nifty;
    private ChaseCamera chaseCam;
    private DirectionalLight sun;
    @Getter
    private Player player;
    private NavMesh navMesh;
    @Getter
    private Node enemyRoot;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.app.getViewPort().setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        this.assetManager = this.app.getAssetManager();
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.bulletAppState = this.app.getBulletAppState();
        this.nifty = this.app.getNifty();
        initPlayer();
        initEnvironment();
        initEnemies();


        inventoryState = new InventoryState(player.getInventory());
        appStateManager.attach(inventoryState);
        inventoryState.setEnabled(false);

        initKeys();
    }
    private void initEnemies(){
        enemyRoot = new Node();
        enemyRoot.setUserData("name","enemy_root");
        this.app.getRootNode().attachChild(enemyRoot);
        // Create an enemy with name "Orc" and 100 health points
        Enemy enemy = new Enemy("Bug", 100, assetManager,this.app.getRootNode(),bulletAppState,this);
        enemyRoot.attachChild(enemy);

        // Set the enemy's local translation
        //enemy.setLocalTranslation(0, 10, 0);

        // Add the enemy to the root node
        //this.app.getRootNode().attachChild(enemy);
    }
    private void initEnvironment(){
        this.sun = new DirectionalLight(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal(), ColorRGBA.White);
        this.app.getRootNode().addLight(sun);
        Spatial scene = assetManager.loadModel("Scenes/basic scene.glb");
        scene.setName("ground");
        scene.scale(5);
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape(scene);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        landscape.setKinematic(true);
        landscape.setGravity(new Vector3f(0,0,0));
        scene.addControl(landscape);
        this.app.getRootNode().attachChild(scene);
        bulletAppState.getPhysicsSpace().addAll(scene);
        Spatial navMeshSpatial = assetManager.loadModel("Scenes/navMesh.glb").scale(5);
        this.app.getRootNode().attachChild(navMeshSpatial);
        bulletAppState.getPhysicsSpace().addAll(navMeshSpatial);
        Geometry navMeshGeometry = (Geometry)((Node) navMeshSpatial).getChild(0);
        navMesh = new NavMesh(navMeshGeometry);
    }
    private void initPlayer(){
        this.player = new Player(assetManager,new Vector3f(10,100,10),this.app.getRootNode(),bulletAppState,this);
        this.app.getRootNode().attachChild(player.getPlayerRoot());
        chaseCam = initChaseCam();
        bulletAppState.getPhysicsSpace().add(player.getCharacterCollider());
        //bulletAppState.getPhysicsSpace().addAll(player.getPlayerRoot());
        player.getCharacterCollider().setGravity(new Vector3f(0,-10f,0));
        player.getCharacterCollider().setAngularFactor(0f);
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
    private void initKeys(){
        inputManager.addMapping("EXIT", new KeyTrigger(KeyInput.KEY_F4));
        inputManager.addMapping("INVENTORY", new KeyTrigger(KeyInput.KEY_E));
        //player mappings
        inputManager.addMapping("FORWARD", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("BACKWARD", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("LEFTWARD", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RIGHTWARD", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("INTERACT", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("DASH", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("ATTACK", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, new String[]{
                "EXIT","INVENTORY","JUMP","INTERACT","DASH","ATTACK"
        });
        inputManager.addListener(analogListener,new String[]{
                "FORWARD","BACKWARD","LEFTWARD","RIGHTWARD"
        });
    }
    final private ActionListener actionListener = new ActionListener() {
        public void onAction(String action, boolean isPressed, float tpf) {
            if (action.equals("EXIT") && !isPressed) {
                System.exit(0);
            }
            if (action.equals("INVENTORY") && isPressed) {
                inventoryState.setEnabled(!inventoryState.isEnabled());
            }
            if (action.equals("JUMP") && isPressed) {
                player.jump();
            }
            if (action.equals("DASH") && isPressed) {
                player.roll();
            }
            if (action.equals("ATTACK") && isPressed) {
                player.attack("melee");
            }
        }
    };
    /** Use this listener for continuous events */
    final private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String action, float value, float tpf) {
            if (action.equals("FORWARD")) {
                player.moveForward(-value,chaseCam.getHorizontalRotation());/*player.getCharacterCollider().getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0)) - FastMath.HALF_PI - chaseCam.getHorizontalRotation()*/
            }else if (action.equals("BACKWARD")) {
                player.moveForward(value,chaseCam.getHorizontalRotation());
            } else if (value == 0) {
                player.setMovingForward(0);
            }
            if (action.equals("RIGHTWARD")) {
                player.moveSideward(-value,chaseCam.getHorizontalRotation());
            }else if (action.equals("LEFTWARD")) {
                player.moveSideward(value,chaseCam.getHorizontalRotation());
            } else if (value == 0) {
                player.setMovingForward(0);
            }
        }
    };


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
            .setText("Health: "+player.getHealth().getValue());

        nifty
            .getScreen("hud_screen")
            .findElementById("energy")
            .getRenderer(TextRenderer.class)
            .setText("Energy: "+player.getEnergy().getValue());
        nifty
                .getScreen("hud_screen")
                .findElementById("data")
                .getRenderer(TextRenderer.class)
                .setText("ф: "+(int)Math.toDegrees(chaseCam.getHorizontalRotation()) + ";" + (int)Math.toDegrees(player.getCharacterCollider().getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0))));
    }

    @Override
    protected void onDisable() {

    }
    
    @Override
    protected void cleanup(Application app) {
    }

    private ChaseCamera initChaseCam() {
        var chaseCam = new ChaseCamera(this.app.getCamera(), player.getModel(), inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setDefaultDistance(3);
        return chaseCam;
    }
}
