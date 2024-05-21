package org.konceptosociala.kareladventures.game.player;

import org.konceptosociala.kareladventures.game.inventory.Inventory;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;

@Getter
public class Player extends Node {
    private static final String MODEL_PATH = "";

    private BetterCharacterControl characterControl;
    private Spatial model;

    private Health health;
    private Energy energy;
    private Inventory inventory;

    Player(AssetManager assetManager) {
        model = assetManager.loadModel(MODEL_PATH);
        characterControl = new BetterCharacterControl(1.5f, 6f, 1f);

        attachChild(model);

        health = new Health(100);
        energy = new Energy(100);
        inventory = new Inventory();
    }
    
    public boolean isAlive() {
        return health.getValue() > 0;
    }
}
