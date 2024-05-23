package org.konceptosociala.kareladventures;

import java.awt.*;
import org.konceptosociala.kareladventures.state.IntroState;
import org.konceptosociala.kareladventures.state.MainMenuState;
import org.konceptosociala.kareladventures.ui.UIInitializer;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import lombok.Getter;

@Getter
public class KarelAdventures extends SimpleApplication {
    private Nifty nifty;
    private AppSettings appSettings = new AppSettings(true);
    private IntroState introState;
    private MainMenuState mainMenuState;

    public static void main(String[] args) {
        KarelAdventures app = new KarelAdventures();
        app.start();
    }

    public KarelAdventures() {
        super();
        setFullscreen();
        setDisplayStatView(false);
        setDisplayFps(false);
        setShowSettings(false);
        setSettings(appSettings);
    }
    
    @Override
    public void simpleInitApp() {
        try {
            flyCam.setEnabled(false);

            introState = new IntroState(cam.getWidth(), cam.getHeight());
            mainMenuState = new MainMenuState();
            nifty = initNifty();
            UIInitializer.initUI(this);

            stateManager.attach(introState);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private void setFullscreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        int i = 0;
        appSettings.setResolution(modes[i].getWidth(), modes[i].getHeight());
        appSettings.setFrequency(modes[i].getRefreshRate());
        appSettings.setBitsPerPixel(modes[i].getBitDepth());
        appSettings.setFullscreen(device.isFullScreenSupported());
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

        return nifty;
    }
}