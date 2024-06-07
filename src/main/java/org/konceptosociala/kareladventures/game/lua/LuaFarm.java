package org.konceptosociala.kareladventures.game.lua;

import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.InteractableNode;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class LuaFarm extends InteractableNode {
    private static final String LUA_FARM_MODEL_NAME = "Models/luafarm.glb";

    private Spatial model;
    private CollisionShape collider;
    private RigidBodyControl rigidBodyControl;

    public LuaFarm(AssetManager assetManager, Vector3f position, BulletAppState bulletAppState) {
        super();
        this.model = assetManager.loadModel(LUA_FARM_MODEL_NAME);
        this.collider = new BoxCollisionShape(new Vector3f(5, 5, 5));
        this.rigidBodyControl = new RigidBodyControl(collider, 1);
        this.rigidBodyControl.setKinematic(true);
        this.rigidBodyControl.setGravity(new Vector3f(0,0,0));
        this.model.addControl(rigidBodyControl);
        attachChild(model);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        bulletAppState.getPhysicsSpace().addAll(this);
    }

    @Override
    public void interact(GameState gameState, Player player) {
        boolean shouldInteract = false;

        Vector3f characterPosition = player.getCharacterControl().getPhysicsLocation();
    }
}
