package org.konceptosociala.kareladventures.state;

import java.io.FileNotFoundException;
import org.konceptosociala.kareladventures.KarelAdventures;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;

import jme.video.player.MovieSettings;
import jme.video.player.MovieState;
import lombok.Getter;

public class ExtendedMovieState extends MovieState {
    private KarelAdventures app;
    private AppStateManager stateManager;
    @Getter
    private BaseAppState nextState;
    @Getter
    private Camera cam;
    @Getter
    private int movieWidth;
    @Getter
    private int movieHeight;

    public ExtendedMovieState(String path, Camera cam, BaseAppState nextState) throws FileNotFoundException {
        super(new MovieSettings(path, getMovieWidth(cam), getMovieHeight(cam), 1.0f, true));
        this.cam = cam;
        this.movieWidth = getMovieWidth(cam);
        this.movieHeight = getMovieHeight(cam);
        this.nextState = nextState;
    }

    private static int getMovieHeight(Camera cam) {
        return (int)(cam.getWidth()*9*1.75/16);
    }

    private static int getMovieWidth(Camera cam) {
        return cam.getWidth();
    }

    @Override
    protected void initialize(Application app) {
        super.initialize(app);
        this.app = (KarelAdventures) app;
        this.stateManager = getStateManager();
        this.app.getFlyByCamera().setEnabled(false);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (isStopped()) {
            stateManager.detach(this);
            stateManager.attach(nextState);
        }
    }
}

