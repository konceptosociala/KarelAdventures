package org.konceptosociala.kareladventures.game.player;

import org.konceptosociala.kareladventures.game.inventory.Inventory;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;

@Getter
public class Player extends Node {
    private static final ModelKey PLAYER_MODEL_KEY = new ModelKey("Models/karel.glb");

    private BetterCharacterControl characterControl;
    private Node model;

    private Health health;
    private Energy energy;
    private Inventory inventory;

    public Player(AssetManager assetManager) {
        model = (Node)assetManager.loadModel(PLAYER_MODEL_KEY);
        Node karel = (Node)model.getChild("Karel_Armature");
        Geometry geom = (Geometry)karel.getChild("Karel");
        geom.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md"));
        attachChild(model);

        characterControl = new BetterCharacterControl(1.5f, 6f, 1f);
        characterControl.setJumpForce(new Vector3f(0, 10, 10));
        addControl(characterControl);

        health = new Health(100);
        energy = new Energy(100);
        inventory = new Inventory();
    }
    
    public boolean isAlive() {
        return health.getValue() > 0;
    }
}
