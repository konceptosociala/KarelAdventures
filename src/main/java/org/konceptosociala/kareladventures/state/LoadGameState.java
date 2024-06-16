package org.konceptosociala.kareladventures.state;

import static org.konceptosociala.kareladventures.KarelAdventures.LOG;

import java.util.List;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.BossGhost;
import org.konceptosociala.kareladventures.game.BossWall;
import org.konceptosociala.kareladventures.game.Sun;
import org.konceptosociala.kareladventures.game.World;
import org.konceptosociala.kareladventures.game.enemies.AntHill;
import org.konceptosociala.kareladventures.game.enemies.Enemy;
import org.konceptosociala.kareladventures.game.enemies.EnemyTower;
import org.konceptosociala.kareladventures.game.enemies.RangedEnemy;
import org.konceptosociala.kareladventures.game.farm.KarelFarm;
import org.konceptosociala.kareladventures.game.npc.Dialog;
import org.konceptosociala.kareladventures.game.npc.DialogMessage;
import org.konceptosociala.kareladventures.game.npc.NPC;
import org.konceptosociala.kareladventures.game.player.AttackType;
import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.ui.LoadingBarBuilder;
import org.konceptosociala.kareladventures.ui.InterfaceBlur;
import org.konceptosociala.kareladventures.utils.InteractableNode;
import org.konceptosociala.kareladventures.utils.Level;
import org.konceptosociala.kareladventures.utils.SaveLoadException;
import org.konceptosociala.kareladventures.utils.SaveLoader;
import org.konceptosociala.kareladventures.utils.TomlException;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@Getter
@RequiredArgsConstructor
public class LoadGameState extends BaseAppState implements ScreenController {
    public enum LoadType {
        NewGame,
        Saving,
    }

    private SaveLoader saveLoader;

    private KarelAdventures app;
    private AssetManager assetManager;
    @Getter(AccessLevel.NONE)
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    private Camera cam;
    private Node rootNode;
    private Nifty nifty;
    private FilterPostProcessor fpp;

    private Node interactableRoot;
    private Node enemyRoot;
    private ChaseCamera chaseCam;
    private Sun sun;
    private World world;
    private Player player;
    private Level currentLevel;
    private NPC sisterOlena;
    private NPC bush;
    private Boolean bushMustMove = false;
    private BossGhost bossGhost;
    private BossWall bossWall;

    private final LoadType loadType;
    private Element progressBarElement;
    private TextRenderer textRenderer;
    private LightProbe probe;
    private int frameCount = 0;
    private boolean lightsPrepared = false;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.app.getViewPort().setBackgroundColor(ColorRGBA.Black);
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.fpp = this.app.getFpp();
        this.viewPort = this.app.getViewPort();
        this.rootNode = this.app.getRootNode();
        this.nifty = this.app.getNifty();
        this.cam = this.app.getCamera();
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
                    filename("Interface/UI/load_bg.png");
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
                        font("Interface/Fonts/Ubuntu-C.ttf");
                        text("                                                  ");
                    }});
                }});
            }});

        }}.build(nifty));

        nifty.gotoScreen("load_game_screen");
    }

    @Override
    public void update(float tpf) {
        switch (frameCount) {
            case 1 -> setProgress(0.0f, "Завантаження фізики...",       this::loadTextRenderer);
            case 2 -> setProgress(0.1f, "Завантаження гравця...",       this::loadPhysics);
            case 3 -> setProgress(0.2f, "Завантаження НІПів...",        this::loadPlayer);
            case 4 -> setProgress(0.3f, "Завантаження ворогів...",      this::loadNPC);
            case 5 -> setProgress(0.4f, "Завантаження середовища...",   this::loadEnemies);
            case 6 -> setProgress(0.6f, "Ініціалізація неба...",        this::loadEnvironment);
            case 7 -> setProgress(0.9f, "Налаштування світла...",       this::loadSky); 
            case 8 -> loadLighting();
        }
      
        if (lightsPrepared == true) {
            setProgress(1.0f, "Finished.", null);
            
            try {
                stateManager.attach(switch (loadType) {
                    case NewGame -> new FirstCutSceneState(this);
                    case Saving -> new GameState(this);
                });
            } catch (Exception e) {
                e.printStackTrace();
                app.stop();
            }

            this.setEnabled(false);
            stateManager.detach(this);
        }

        frameCount++;
    }

    private void setProgress(final float progress, String loadingText, Runnable loadMethod) {
        if (loadMethod != null)
            loadMethod.run();

        final int MIN_WIDTH = 32;
        int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
        progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        progressBarElement.getParent().layoutElements();

        textRenderer.setText(loadingText);
    }

    private void loadPhysics() {
        bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);
        bulletAppState.setEnabled(false);
    }

    private void loadLighting() {
        EnvironmentCamera environmentCamera = stateManager.getState(EnvironmentCamera.class);
        probe = LightProbeFactory.makeProbe(
            environmentCamera,
            rootNode,
            EnvMapUtils.GenerationType.Fast,
            new JobProgressAdapter<LightProbe>() {
                @Override
                public void done(LightProbe result) {
                    LOG.info("Done rendering environment maps");
                    lightsPrepared = true;
                }
            }
        );
        probe.getArea().setRadius(1000);
        rootNode.addLight(probe);

        rootNode.setShadowMode(ShadowMode.CastAndReceive);
        sun = new Sun(assetManager, fpp);
        rootNode.addLight(sun);
    }

    private void loadSky() {
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/sky.jpg", SkyFactory.EnvMapType.EquirectMap);
        sky.setShadowMode(ShadowMode.Off);
        rootNode.attachChild(sky);
        stateManager.attach(new EnvironmentCamera(256, Vector3f.ZERO));
    }

    private void loadEnvironment() {
        world = new World("Scenes/scene.glb", "Scenes/scene_collider.glb", assetManager);
        rootNode.attachChild(world);
        bulletAppState.getPhysicsSpace().addAll(world);
    }

    private void loadEnemies() {
        enemyRoot = new Node();
        enemyRoot.setUserData("name", "enemy_root");
        rootNode.attachChild(enemyRoot);

        Enemy e1 = new Enemy(new Vector3f(-45, 5, 5), assetManager, bulletAppState, 100);
        RangedEnemy re1 = new RangedEnemy(new Vector3f(-80, 5, -8), assetManager, bulletAppState, 100);
        EnemyTower et1 = new EnemyTower(new Vector3f(-90, 5, 10), assetManager, bulletAppState, 500);
        AntHill ah1 = new AntHill(new Vector3f(-90, 5, 20), assetManager, bulletAppState);
        enemyRoot.attachChild(e1);
        enemyRoot.attachChild(re1);
        enemyRoot.attachChild(et1);
        enemyRoot.attachChild(ah1);
    }

    private void loadPlayer() {
        if (loadType == LoadType.Saving) {
            try {
                saveLoader = SaveLoader.load("data/Saves/karel.sav");
            } catch (SaveLoadException e) {
                e.printStackTrace();
                app.stop();
            }

            player = new Player(assetManager, saveLoader.getPlayerPosition(), bulletAppState);

            player.setHealth(saveLoader.getPlayerHealth());
            player.setInventory(saveLoader.getPlayerInventory());
            player.setBalance(saveLoader.getBalance());

            currentLevel = saveLoader.getCurrentLevel();
        } else {
            player = new Player(assetManager, new Vector3f(0,1,0), bulletAppState);
            currentLevel = Level.Village;
        }

        rootNode.attachChild(player);

        chaseCam = initChaseCam();

        bossGhost = new BossGhost(assetManager);
        rootNode.attachChild(bossGhost);

        bossWall = new BossWall(assetManager);
        rootNode.attachChild(bossWall);
        bulletAppState.getPhysicsSpace().addAll(bossWall);

        new Enemy(Vector3f.ZERO, assetManager, bulletAppState, frameCount);
        new Enemy(Vector3f.ZERO, assetManager, bulletAppState, frameCount);
        new Enemy(Vector3f.ZERO, assetManager, bulletAppState, frameCount);
        new Enemy(Vector3f.ZERO, assetManager, bulletAppState, frameCount);
        new Enemy(Vector3f.ZERO, assetManager, bulletAppState, frameCount);
    }

    private void loadTextRenderer() {
        var screen = nifty
            .getScreen("load_game_screen");
            
        if (screen == null)
            return;

        var element = screen.findElementById("loading_text");

        if (element == null)
            return;

        textRenderer = element.getRenderer(TextRenderer.class);
    }

    private void loadNPC() {
        interactableRoot = new Node();
        interactableRoot.setUserData("name", "interactable_root");
        rootNode.attachChild(interactableRoot);

        try {
            sisterOlena = new NPC(
                "Сестра Олена",
                "Models/pechkurova.glb",
                "Pechkurova_idle",

                    new Dialog("data/Dialogs/sister_olena_1.toml", 
                    new Dialog("data/Dialogs/sister_olena_2.toml", 
                    new Dialog("data/Dialogs/sister_olena_3.toml", null))),

                new CapsuleCollisionShape(0.8f, 5f),
                new Vector3f(9.7f, -0.2f, 3.5f),
                new Quaternion().fromAngleAxis(-(float)Math.PI/2, Vector3f.UNIT_Y),
                assetManager,
                bulletAppState
            );
            interactableRoot.attachChild(sisterOlena);
            sisterOlena.getLastDialog().setNextDialog(sisterOlena.getDialog());

            bush = new NPC(
                "Мудрий Кущ",
                "Models/bush.glb",
                null,

                    new Dialog("data/Dialogs/bush_1.toml", 
                    new Dialog("data/Dialogs/bush_2.toml", null)),

                new CapsuleCollisionShape(4f, 3f),
                new Vector3f(-161, 2, 145),
                Quaternion.IDENTITY,
                assetManager,
                bulletAppState
            );
            interactableRoot.attachChild(bush);
        } catch (TomlException e) {
            e.printStackTrace();
            app.stop();
        }

        // KarelFarm karelFarm = new KarelFarm(assetManager, new Vector3f(10, 0, 0), bulletAppState);
        // interactableRoot.attachChild(karelFarm);

        if (loadType == LoadType.Saving) {
            for (var entry : saveLoader.getDialogs().entrySet()) {
                var npc = interactableRoot.getChild(entry.getKey());
                if (npc != null && npc instanceof NPC) {
                    ((NPC) npc).setDialog(entry.getValue());
                }
            }
            bushMustMove = saveLoader.getBushMustMove();
        }
    }

    private ChaseCamera initChaseCam() {
        var chaseCam = new ChaseCamera(this.app.getCamera(), player.getModel(), inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setDefaultDistance(3);
        return chaseCam;
    }

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
