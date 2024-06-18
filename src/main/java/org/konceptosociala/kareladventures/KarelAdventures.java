package org.konceptosociala.kareladventures;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.konceptosociala.kareladventures.state.IntroState;
import org.konceptosociala.kareladventures.state.MainMenuState;
import org.konceptosociala.kareladventures.utils.AudioManager;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.NiftyImage;
import javafx.application.Platform;
import lombok.Getter;

@Getter
public class KarelAdventures extends SimpleApplication {
    public static final Logger LOG = Logger.getLogger(KarelAdventures.class.getName());
    
    private Nifty nifty;
    private AppSettings appSettings = new AppSettings(true);
    private AudioManager audioManager;
    private IntroState introState;
    private MainMenuState mainMenuState;
    private FilterPostProcessor fpp;
    private boolean cheatsEnabled;

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(KarelAdventures.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        KarelAdventures app = new KarelAdventures(args);
        app.start();
    }

    public KarelAdventures(String[] args) {
        super();
        // appSettings.setWindowSize(1024, 768);
        // setShowSettings(false);
        appSettings.setSettingsDialogImage("Interface/MOSHED-2024-5-25-1-43-6.gif");
        appSettings.setMinResolution(1024, 768);
        setDisplayStatView(false);
        setDisplayFps(false);
        setSettings(appSettings);

        cheatsEnabled = args.length > 0 && args[0].equals("iamdumbass");
    }
    
    @Override
    public void simpleInitApp() {
        try {
            Platform.startup(() -> {});

            inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

            audioManager = new AudioManager(assetManager);
            mainMenuState = new MainMenuState();
            introState = new IntroState(cam, mainMenuState);
            nifty = initNifty();
            fpp = new FilterPostProcessor(assetManager);
            viewPort.addProcessor(fpp);

            var scene = guiViewPort.getScenes().get(0);
            var t = scene.getLocalTranslation();
            scene.setLocalTranslation(t.x, t.y+((cam.getHeight()-(int)(cam.getWidth()*9*1.75/16))/2), t.z);

            stateManager.attach(introState);
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    @SuppressWarnings("null")
    public static NiftyImage createImage(Nifty nifty, String screenName, String path, boolean filterLinear) {
        var screen = nifty.getScreen(screenName);
        return nifty.getRenderEngine().createImage(
            screen, 
            path, 
            filterLinear
        );
    }

    private Nifty initNifty() {
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
            assetManager, 
            inputManager,
            audioRenderer,
            guiViewPort
        );

        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        nifty.registerEffect("inventory-hint", "org.konceptosociala.kareladventures.ui.inventory.InventoryHint");

        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE);

        return nifty;
    }

    @Override
    public void stop() {
        Platform.exit();

        super.stop();
    }
}