package org.konceptosociala.kareladventures.state;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;

import static org.konceptosociala.kareladventures.KarelAdventures.LOG;

import java.util.HashMap;
import java.util.stream.Stream;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.BossGhost;
import org.konceptosociala.kareladventures.game.BossWall;
import org.konceptosociala.kareladventures.game.Sun;
import org.konceptosociala.kareladventures.game.World;
import org.konceptosociala.kareladventures.game.enemies.BulletCollisionListener;
import org.konceptosociala.kareladventures.game.inventory.ItemRareness;
import org.konceptosociala.kareladventures.game.npc.Dialog;
import org.konceptosociala.kareladventures.game.npc.NPC;
import org.konceptosociala.kareladventures.game.player.AttackType;
import org.konceptosociala.kareladventures.game.player.Health;
import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.ui.InterfaceBlur;
import org.konceptosociala.kareladventures.utils.AudioManager;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;
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
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
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
import com.jme3.math.Vector3f;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.tools.Color;

@Getter
public class GameState extends BaseAppState  {
    private KarelAdventures app;
    private AssetManager assetManager;
    private AppStateManager appStateManager;
    private BulletAppState bulletAppState;
    private InputManager inputManager;
    private AudioManager audio;
    private Nifty nifty;
    private Node rootNode;
    private InterfaceBlur interfaceBlur;
    
    private KarelFarmState karelFarmState;
    private PauseState pauseState;
    private DialogState dialogState;
    private InventoryState inventoryState;
    private GuideBookState guideBookState;
    private ChaseCamera chaseCam;
    private Sun sun;
    private LightProbe probe;
    private World world;
    private Player player;
    private Node enemyRoot;
    private Node interactableRoot;
    private Level currentLevel;
    private NPC sisterOlena;
    private NPC bush;
    private BossGhost bossGhost;
    private BossWall bossWall;

    private boolean bushMustMove;
    private boolean closingBoss;
    private boolean gameOver = false;

    public GameState(LoadGameState loadGameState) {
        bulletAppState = loadGameState.getBulletAppState();
        assetManager = loadGameState.getAssetManager();
        chaseCam = loadGameState.getChaseCam();
        sun = loadGameState.getSun();
        world = loadGameState.getWorld();
        player = loadGameState.getPlayer();
        bossGhost = loadGameState.getBossGhost();
        bossWall = loadGameState.getBossWall();
        rootNode = loadGameState.getRootNode();
        enemyRoot = loadGameState.getEnemyRoot();
        interactableRoot = loadGameState.getInteractableRoot();
        currentLevel = loadGameState.getCurrentLevel();
        probe = loadGameState.getProbe();
        sisterOlena = loadGameState.getSisterOlena();
        bush = loadGameState.getBush();
        bushMustMove = loadGameState.getBushMustMove();
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
        this.interfaceBlur = new InterfaceBlur(this.app.getFpp());
        this.audio = this.app.getAudioManager();
        BulletCollisionListener bulletCollisionListener = new BulletCollisionListener();
        bulletCollisionListener.setBulletAppState(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(bulletCollisionListener);
        initPlayer();
        initEnemies();
        initControls();

        pauseState = new PauseState(this);
        appStateManager.attach(pauseState);
        pauseState.setEnabled(false);

        inventoryState = new InventoryState(player.getInventory(), interfaceBlur);
        appStateManager.attach(inventoryState);
        inventoryState.setEnabled(false);

        guideBookState = new GuideBookState(interfaceBlur);
        appStateManager.attach(guideBookState);
        guideBookState.setEnabled(false);

        dialogState = new DialogState(interfaceBlur);
        appStateManager.attach(dialogState);
        dialogState.setEnabled(false);

        karelFarmState = new KarelFarmState(player, interfaceBlur);
        appStateManager.attach(karelFarmState);
        karelFarmState.setEnabled(false);

        sun.getFade().fadeIn();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(false);
        bulletAppState.setEnabled(true);
        chaseCam.setEnabled(true);
        setMusicTheme();

        nifty.addScreen("hud_screen", new ScreenBuilder("HUD screen") {{
            controller(new DefaultScreenController());

            layer(new LayerBuilder("hud_layer") {{
                childLayoutVertical();
                padding("20px");

                panel(new PanelBuilder("hud_panel") {{
                    childLayoutHorizontal();

                    panel(new PanelBuilder("karel_hud") {{
                        childLayoutCenter();

                        image(new ImageBuilder("karel_hud_image"){{
                            filename("Interface/UI/KarelHUD.png");
                            width("128px");
                            height("128px");
                        }});

                        image(new ImageBuilder("karel_hud_border"){{
                            filename("Interface/UI/Border/panel-border-005.png");
                            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                            width("128px");
                            height("128px");
                        }});
                    }});

                    panel(new PanelBuilder("health_bar") {{
                        childLayoutCenter();
                        marginLeft("20px");

                        image(new ImageBuilder("karel_hud_border") {{
                            filename("Interface/UI/Transparent center/panel-transparent-center-005.png");
                            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                            width("384px");
                            height("128px");
                        }});

                        panel(new PanelBuilder("health_params") {{
                            childLayoutVertical();
                            padding("20px");

                            text(new TextBuilder("health_label") {{
                                text("Показник життя");
                                color(Color.BLACK);
                                font("Interface/Fonts/Ubuntu-C.ttf");
                                marginBottom("24px");
                            }});

                            panel(new PanelBuilder("health") {{
                                childLayoutCenter();

                                panel(new PanelBuilder("health_idle") {{
                                    backgroundColor(new Color(0.4f, 0, 0, 1));
                                    width("344px");
                                    height("28px");
                                }});
                                
                                panel(new PanelBuilder("health_active") {{
                                    backgroundColor(new Color(0.8f, 0, 0, 1));
                                    width("344px");
                                    height("28px");
                                }});

                                image(new ImageBuilder("health_border") {{
                                    filename("Interface/UI/Border/panel-border-007.png");
                                    imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                                    width("344px");
                                    height("28px");
                                }});
                            }});
                        }});
                    }});

                }});
                
                panel(new PanelBuilder("guidebook_bar") {{
                    childLayoutCenter();
                    marginTop("20px");

                    image(new ImageBuilder("guidebook_bar_border") {{
                        filename("Interface/UI/Transparent center/panel-transparent-center-005.png");
                        imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                        width("168px");
                        height("84px");
                    }});

                    panel(new PanelBuilder("guidebook_bar_icons") {{
                        childLayoutHorizontal();
                        align(Align.Center);
                        valign(VAlign.Center);
                        width("168px");
                        height("84px");

                        panel(new PanelBuilder() {{
                            childLayoutCenter();
                            width("50%");
                            height("100%");

                            image(new ImageBuilder("guidebook_book") {{
                                filename("Interface/Items/Book.png");
                            }});
                        }});

                        panel(new PanelBuilder() {{
                            childLayoutCenter();
                            width("50%");
                            height("100%");

                            image(new ImageBuilder("guidebook_key") {{
                                filename("Interface/GuideBook/Keyboard_Dark/keyboard_g_outline.png");
                            }});
                        }});
                    }});
                }});
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("hud_screen");
    }

    public void save() {
        var saveLoader = new SaveLoader(
            player.getHealth(), 
            player.getBalance(),
            player.getInventory(), 
            dialogsToMap(), 
            player.getLocalTranslation(), 
            currentLevel,
            bushMustMove
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
        if (gameOver) return;

        player.update(tpf);
        for (Spatial i : enemyRoot.getChildren()) {
            if (i instanceof IUpdatable) {
                ((IUpdatable) i).update(tpf);
            }
        }

        if (bossGhost.enterPlayer(player) && currentLevel.equals(Level.Wasteland)) {
            currentLevel = Level.Boss;
            closingBoss = true;
            bossWall.setColliderEnabled(true);
            setMusicTheme();
        }

        if (closingBoss) {
            bossWall.getModel().move(0, 0.25f, 0);
            if (bossWall.getModel().getLocalTranslation().y >= 20) {
                closingBoss = false;
            }
        }

        if (currentLevel.equals(Level.Village)) {
            if (player.getInventory().hasItem(ItemRareness.Silver)
                || player.getInventory().hasItem(ItemRareness.Golden)
                || player.getInventory().hasItem(ItemRareness.Legendary)
            ){
                try {
                    bush.setDialog(
                        new Dialog("data/Dialogs/bush_3.toml", 
                        new Dialog("data/Dialogs/bush_4.toml", null))
                    );
                } catch (TomlException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                
                currentLevel = Level.Wasteland;
                setMusicTheme();
            }
        }

        if (currentLevel.equals(Level.Wasteland) && bush.getDialog().getNextDialog() == null)
            bushMustMove = true;

        var bushRigidBody = bush.getRigidBodyControl();

        if (bushMustMove) {
            bushRigidBody.setCollisionShape(new CapsuleCollisionShape(2f, 0.5f));
            bushRigidBody.setLinearVelocity(new Vector3f(1, 0, -1)); 
            bushRigidBody.setKinematic(false);
            bushRigidBody.setFriction(0);
        }

        if (bushRigidBody.getPhysicsLocation().x > -150 || bushRigidBody.getPhysicsLocation().z < 140) {
            bushRigidBody.setLinearVelocity(Vector3f.ZERO); 
            bushRigidBody.setKinematic(true);
            bushRigidBody.setFriction(0);
        }

        nifty
            .getScreen("hud_screen")
            .findElementById("health_active")
            .setWidth(player.getHealth().getValue() * 344 / Health.HP_MAX);

        if (player.getHealth().getValue() == 0) {
            gameOver = true;
            appStateManager.attach(new GameOverState(this));
        }
    }

    @Override
    protected void cleanup(Application app) {
        if (!gameOver && !currentLevel.equals(Level.Boss))
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
        audio.village.pause();
        audio.wasteland.pause();
        audio.boss.pause();
        audio.walk.stop();
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

    private void setMusicTheme() {
        switch (currentLevel) {
            case Village -> {
                audio.village.play();
                audio.wasteland.stop();
                audio.boss.stop();
            }
            case Wasteland -> {
                audio.village.stop();
                audio.wasteland.play();
                audio.boss.stop();
            }
            case Boss -> {
                audio.village.stop();
                audio.wasteland.stop();
                audio.boss.play();
            }
        }
    }

    private void initControls() {
        inputManager.addMapping("ESCAPE", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("SAVE", new KeyTrigger(KeyInput.KEY_F6));
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
        inputManager.addMapping("GUIDEBOOK", new KeyTrigger(KeyInput.KEY_G));

        // Enable listeners
        inputManager.addListener(actionListener, new String[]{"FORWARD","BACKWARD","LEFTWARD","RIGHTWARD", "EXIT","INVENTORY","JUMP","INTERACT","DASH","ATTACK","ESCAPE", "RETURN", "GUIDEBOOK", "SAVE"});
        inputManager.addListener(analogListener, new String[]{"FORWARD","BACKWARD","LEFTWARD","RIGHTWARD"});
    }

    final private ActionListener actionListener = new ActionListener() {
        public void onAction(String action, boolean isPressed, float tpf) {
            if (gameOver) return;

            if (action.equals("ESCAPE") && isPressed) {
                if (inventoryState.isEnabled()) {
                    audio.fillUp.stop();
                    audio.fillUp.play();

                    chaseCam.setEnabled(true);
                    inventoryState.setEnabled(false);
                } else if (dialogState.isEnabled()) {
                    audio.fillUp.stop();
                    audio.fillUp.play();

                    chaseCam.setEnabled(true);
                    dialogState.setEnabled(false);
                } else if (karelFarmState.isEnabled()) {
                    audio.fillUp.stop();
                    audio.fillUp.play();

                    chaseCam.setEnabled(true);
                    karelFarmState.setEnabled(false);
                } else if (guideBookState.isEnabled()) {
                    audio.fillUp.stop();
                    audio.fillUp.play();

                    chaseCam.setEnabled(true);
                    guideBookState.setEnabled(false);
                } else if (pauseState.isEnabled()) {
                    audio.fillUp.stop();
                    audio.fillUp.play();

                    pauseState.setEnabled(false);
                    GameState.this.setEnabled(true);
                } else {
                    audio.ui1.stop();
                    audio.ui1.play();

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

            if (action.equals("SAVE") && isPressed) {
                if (!currentLevel.equals(Level.Boss)) {
                    audio.save.stop();
                    audio.save.play();

                    save();
                } else {
                    audio.uiError1.stop();
                    audio.uiError1.play();
                }
            }

            if (action.equals("INVENTORY") && isPressed) {
                audio.ui1.stop();
                audio.ui1.play();
                
                inventoryState.setEnabled(true);
                chaseCam.setEnabled(false);
            }

            if (action.equals("GUIDEBOOK") && isPressed) {
                audio.ui1.stop();
                audio.ui1.play();

                guideBookState.setEnabled(true);
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
                app.stop();
            }
            
            if (action.equals("JUMP") && isPressed) {
                player.jump();
            }
            
            if (action.equals("DASH") && isPressed) {
                player.roll();
            }

            if (action.equals("ATTACK") && isPressed) {
                player.attack(AttackType.Melee);
                audio.attack.stop();
                audio.attack.play();
            }

            if (Stream.of("FORWARD","BACKWARD","LEFTWARD","RIGHTWARD")
                .anyMatch((c) -> action.equals(c)) && !isPressed
            ) {
                audio.walk.stop();
                LOG.info(action+" released");
            }
        }
    };

    final private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String action, float value, float tpf) {
            if (gameOver) return;

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

            if (Stream.of("FORWARD","BACKWARD","LEFTWARD","RIGHTWARD")
                .anyMatch((c) -> action.equals(c)) && value > 0 && player.isOnGround()
            ) 
                audio.walk.play();
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
