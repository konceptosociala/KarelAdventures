package org.konceptosociala.kareladventures.state;

import java.io.FileNotFoundException;
import org.konceptosociala.kareladventures.KarelAdventures;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import jme.video.player.MovieSettings;
import jme.video.player.MovieState;

public class IntroState extends MovieState {
    private static final String INTRO_PATH = "assets/Videos/Intro/Intro.mp4";

    private KarelAdventures app;
    private AppStateManager stateManager;

    public IntroState(int width, int height) throws FileNotFoundException {
        super(new MovieSettings(INTRO_PATH, width, height, 1.0f, true));
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
            stateManager.attach(app.getMainMenuState());
        }
    }
}
