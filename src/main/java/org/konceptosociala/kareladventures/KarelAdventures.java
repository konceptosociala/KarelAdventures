package org.konceptosociala.kareladventures;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.konceptosociala.kareladventures.game.inventory.Item;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.state.IntroState;
import org.konceptosociala.kareladventures.state.MainMenuState;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.NiftyImage;
import lombok.Getter;

@Getter
public class KarelAdventures extends SimpleApplication {
    public static final Logger LOG = Logger.getLogger(KarelAdventures.class.getName());
    
    private Nifty nifty;
    private AppSettings appSettings = new AppSettings(true);
    private IntroState introState;
    private MainMenuState mainMenuState;
    private FilterPostProcessor fpp;

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(KarelAdventures.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        KarelAdventures app = new KarelAdventures();
        app.start();
    }

    public KarelAdventures() {
        super();
        appSettings.setMinResolution(1024, 768);
        setDisplayStatView(false);
        setDisplayFps(false);
        setShowSettings(true);
        setSettings(appSettings);
    }
    
    @Override
    public void simpleInitApp() {
        try {
            inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

            introState = new IntroState(cam.getWidth(), cam.getHeight());
            mainMenuState = new MainMenuState();
            nifty = initNifty();
            fpp = new FilterPostProcessor(assetManager);
            viewPort.addProcessor(fpp);

            LOG.info(Item.DARK_TINY_SWORD.toString());

            stateManager.attach(introState);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
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

        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.WARNING);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.WARNING);

        return nifty;
    }

    @Override
    public void stop() {
        var gameState = stateManager.getState(GameState.class);
        if (gameState != null && gameState.isEnabled()) {
            gameState.save();
        }

        super.stop();
    }
}