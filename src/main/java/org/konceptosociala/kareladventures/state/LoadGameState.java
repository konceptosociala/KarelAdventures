package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.Sun;
import org.konceptosociala.kareladventures.game.World;
import org.konceptosociala.kareladventures.game.enemies.Enemy;
import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.ui.LoadingBarBuilder;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class LoadGameState extends BaseAppState implements ScreenController {
    public enum LoadType {
        NewGame,
        Saving,
    }

    private KarelAdventures app;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    private Node rootNode;
    private Node enemyRoot;
    private Nifty nifty;

    private InventoryState inventoryState;
    private ChaseCamera chaseCam;
    private Sun sun;
    private World world;
    private Player player;

    private final LoadType loadType;
    private Element progressBarElement;
    private TextRenderer textRenderer;
    private LightProbe probe;
    private float frameCount = 0;
    private boolean lightsPrepared = false;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.app.getViewPort().setBackgroundColor(ColorRGBA.Black);
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.bulletAppState = this.app.getBulletAppState();
        this.rootNode = this.app.getRootNode();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(false);

        nifty.addScreen("load_game_screen", new ScreenBuilder("Load game screen") {{
            controller(LoadGameState.this);

            layer(new LayerBuilder("load_game_layer") {{
                childLayoutCenter();

                image(new ImageBuilder("load_game_bg") {{
                    width("100%");
                    height("100%");
                    filename("Interface/load_bg.png");
                }});
                
                panel(new PanelBuilder("load_game_panel"){{
                    childLayoutVertical();
                    align(Align.Center);
                    valign(VAlign.Center);
                    height("32px");
                    width("70%");

                    control(new LoadingBarBuilder("loading_bar"){{
                        align(Align.Center);
                        valign(VAlign.Center);
                        width("100%");
                        height("100%");
                    }});

                    control(new LabelBuilder("loading_text"){{
                        align(Align.Center);
                        text("                                                  ");
                    }});
                }});
            }});

        }}.build(nifty));

        nifty.gotoScreen("load_game_screen");
    }

    @SuppressWarnings("null")
    @Override
    public void update(float tpf) {
        if (frameCount == 1) {
            textRenderer = nifty
                .getScreen("load_game_screen")
                .findElementById("loading_text")
                .getRenderer(TextRenderer.class);

            setProgress(0.0f, "Loading player...");
        } else if (frameCount == 2) {
            initPlayer();
            initEnemies();

            setProgress(0.2f, "Loading environment...");
        } else if (frameCount == 3) {
            switch (loadType) {
                case NewGame -> initEnvironment();
                case Saving -> initEnvironmentFromFile();
            }

            setProgress(0.6f, "Initializing sky...");
        } else if (frameCount == 4) {
            Spatial sky = SkyFactory.createSky(assetManager, "Textures/sky.jpg", SkyFactory.EnvMapType.EquirectMap);
            sky.setShadowMode(ShadowMode.Off);
            rootNode.attachChild(sky);
            stateManager.attach(new EnvironmentCamera(256, Vector3f.ZERO));

            setProgress(0.6f, "Initializing controls...");
        } else if (frameCount == 5) {
            initKeys();
            
            inventoryState = new InventoryState(player.getInventory());
            stateManager.attach(inventoryState);
            inventoryState.setEnabled(false);

            setProgress(1.0f, "Setting up lighting...");
        } else if (frameCount == 6) {
            EnvironmentCamera environmentCamera = stateManager.getState(EnvironmentCamera.class);
            probe = LightProbeFactory.makeProbe(
                environmentCamera,
                rootNode,
                EnvMapUtils.GenerationType.Fast,
                new JobProgressAdapter<LightProbe>() {
                    @Override
                    public void done(LightProbe result) {
                        System.err.println("Done rendering env maps");
                        lightsPrepared = true;
                    }
                }
            );
            probe.getArea().setRadius(200);
            rootNode.addLight(probe);

            rootNode.setShadowMode(ShadowMode.CastAndReceive);
            sun = new Sun(assetManager, viewPort);
            rootNode.addLight(sun);
        }
        
        if (lightsPrepared == true) {
            setProgress(1.0f, "Finished.");
            stateManager.attach(new GameState(player, enemyRoot));
            this.setEnabled(false);
            stateManager.detach(this);
        }

        frameCount++;
    }

    private void initPlayer(){
        player = new Player(assetManager,new Vector3f(0,10,0),this.app.getRootNode(),bulletAppState);
        rootNode.attachChild(player);
        bulletAppState.getPhysicsSpace().add(player.getCharacterCollider());
        bulletAppState.getPhysicsSpace().addAll(player);
        player.getCharacterCollider().setGravity(new Vector3f(0,-3,0));
        player.getCharacterCollider().setAngularFactor(0f);
        chaseCam = initChaseCam();
    }
    private void initEnemies(){
        enemyRoot = new Node();
        enemyRoot.setUserData("name","enemy_root");
        rootNode.attachChild(enemyRoot);

        Enemy enemy = new Enemy("Bug",new Vector3f(20,100,20), assetManager,this.app.getRootNode(),bulletAppState,100);
        enemyRoot.attachChild(enemy);
    }

    private void initEnvironment(){
        world = new World("Scenes/scene.glb", assetManager);
        rootNode.attachChild(world);
        bulletAppState.getPhysicsSpace().addAll(world);
    }

    private void initEnvironmentFromFile() {
        throw new UnsupportedOperationException("not implemented `saving/loading`");
    }

    private ChaseCamera initChaseCam() {
        var chaseCam = new ChaseCamera(this.app.getCamera(), player.getModel(), inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setDefaultDistance(3);
        return chaseCam;
    }

    private void initKeys(){
        inputManager.addMapping("EXIT", new KeyTrigger(KeyInput.KEY_F4));
        inputManager.addMapping("INVENTORY", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("INTERACT", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("FORWARD", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("BACKWARD", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("LEFTWARD", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RIGHTWARD", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("INTERACT", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("DASH", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("ATTACK", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // Enable listeners
        inputManager.addListener(actionListener, new String[]{"EXIT","INVENTORY","JUMP","INTERACT","DASH","ATTACK"});
        inputManager.addListener(analogListener, new String[]{"FORWARD","BACKWARD","LEFTWARD","RIGHTWARD"});
    }

    private void setProgress(final float progress, String loadingText) {
        final int MIN_WIDTH = 32;
        int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
        progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        progressBarElement.getParent().layoutElements();

        textRenderer.setText(loadingText);
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

            if (action.equals("INTERACT") && isPressed) {

            }

            if (action.equals("ATTACK") && isPressed) {
                player.attack("melee");
            }
        }
    };

    final private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String action, float value, float tpf) {
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

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        progressBarElement = screen.findElementById("progress_bar");
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
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

    @Override
    protected void onDisable() {
    }
    
}
