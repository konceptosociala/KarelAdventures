package org.konceptosociala.kareladventures.state;

import java.io.FileNotFoundException;
import com.jme3.renderer.Camera;

public class IntroState extends ExtendedMovieState {
    private static final String INTRO_PATH = "data/Videos/Intro.mp4";

    public IntroState(Camera cam, MainMenuState mainMenuState) throws FileNotFoundException {
        super(INTRO_PATH, cam, mainMenuState);
    }
}
