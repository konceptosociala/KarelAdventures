package org.konceptosociala.kareladventures.state;

import java.io.FileNotFoundException;

public class CreditsGameState extends ExtendedMovieState {
    private static final String CREDITS_PATH = "data/Videos/Credits.m4v";

    public CreditsGameState(GameState gameState) throws FileNotFoundException {
        super(CREDITS_PATH, gameState.getApp().getCamera(), gameState.getApp().getMainMenuState());
        gameState.cleanup();
    }
}
