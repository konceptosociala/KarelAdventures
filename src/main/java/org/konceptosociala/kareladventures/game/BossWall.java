package org.konceptosociala.kareladventures.game;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import lombok.Getter;

@Getter
public class BossWall extends Node {
    private static final String BOSS_WALL_MODEL = "Models/boss_wall.glb";
    private static final String BOSS_WALL_COLLIDER = "Models/boss_wall_collider.glb";
    private Spatial model;
    private CollisionShape collider;
    private RigidBodyControl rigidBodyControl;

    public BossWall(AssetManager assetManager) {
        super();
        model = assetManager.loadModel(BOSS_WALL_MODEL);
        Spatial collisionScene = assetManager.loadModel(BOSS_WALL_COLLIDER);
        collider = CollisionShapeFactory.createMeshShape(collisionScene);
        rigidBodyControl = new RigidBodyControl(collider, 0);
        rigidBodyControl.setKinematic(true);
        rigidBodyControl.setGravity(new Vector3f(0,0,0));
        addControl(rigidBodyControl);
        attachChild(model);
        setColliderEnabled(false);
    }

    public void setColliderEnabled(boolean enabled) {
        if (enabled)
            rigidBodyControl.setCollisionShape(collider);
        else 
            rigidBodyControl.setCollisionShape(new CapsuleCollisionShape(0, 0));
    }
}
