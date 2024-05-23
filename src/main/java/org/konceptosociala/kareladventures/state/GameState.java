package org.konceptosociala.kareladventures.state;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.player.Player;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

public class GameState extends BaseAppState {    
    private KarelAdventures app;
    private AssetManager assetManager;
    private InputManager inputManager;

    private Player player;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.player = new Player(assetManager);
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(false);
        app.getRootNode().attachChild(player);
    }

    @Override
    protected void onDisable() {

    }
    
    @Override
    protected void cleanup(Application app) {

    }
}
