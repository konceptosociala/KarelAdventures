package org.konceptosociala.kareladventures.game;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class World extends Node {
    public World(String worldPath, String colliderPath, AssetManager assetManager) {
        super();
        Spatial scene = assetManager.loadModel(worldPath);
        Spatial collisionScene = assetManager.loadModel(colliderPath);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(collisionScene);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        landscape.setKinematic(true);
        landscape.setGravity(new Vector3f(0,0,0));
        scene.addControl(landscape);
        attachChild(scene);
    }
}
