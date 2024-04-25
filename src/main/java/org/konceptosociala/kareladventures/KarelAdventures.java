package org.konceptosociala.kareladventures;

import java.awt.*;

import org.konceptosociala.kareladventures.states.MainMenuState;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

import jme.video.player.MovieSettings;
import jme.video.player.MovieState;

public class KarelAdventures extends SimpleApplication {
    private AppSettings appSettings = new AppSettings(true);
    private MovieState movieState;

    public KarelAdventures() {
        super();
        setFullscreen();
        setDisplayStatView(false);
        setDisplayFps(false);
        setShowSettings(false);
        setSettings(appSettings);
    }

    public static void main(String[] args) {
        KarelAdventures app = new KarelAdventures();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        try {
            String path = "bin/main/Videos/Intro/mp4/acm.mp4";
            MovieSettings movie = new MovieSettings(path, cam.getWidth(), cam.getHeight(), 1.0f, true);
            movieState = new MovieState(movie);
            stateManager.attach(movieState);

            flyCam.setEnabled(false);
            inputManager.setCursorVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (movieState.isStopped()) {
            stateManager.detach(movieState);

            flyCam.setMoveSpeed(50);
            flyCam.setEnabled(true);
            inputManager.setCursorVisible(false);

            stateManager.attach(new MainMenuState());
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {

    }

    public AppSettings getSettings() {
        return appSettings;
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
}
