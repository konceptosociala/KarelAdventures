package org.konceptosociala.kareladventures.state;

import java.io.FileNotFoundException;

public class FirstCutSceneState extends ExtendedMovieState {
    private static final String FIRST_CUTSCENE_PATH = "data/Videos/FirstCutScene.mp4";

    public FirstCutSceneState(LoadGameState loadGameState) throws FileNotFoundException {
        super(FIRST_CUTSCENE_PATH, loadGameState.getCam(), new GameState(loadGameState));
    }
}
