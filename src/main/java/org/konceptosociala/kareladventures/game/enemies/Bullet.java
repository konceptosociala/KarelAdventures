package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bullet extends Node implements IUpdatable {
    private String ENEMY_MODEL_NAME = "Models/enemy_bullet.glb";

    private BulletAppState bulletAppState;
    private GameState thisGameState;

    private Spatial model;
    private CollisionShape characterCollider;
    private RigidBodyControl characterControl;
    private float speed = 15f;
    private AssetManager assetManager;
    private int damage = 3;

    public Bullet(
            Vector3f position,
            Vector3f target,
            AssetManager assetManager,
            BulletAppState bulletAppState,
            String MODEL_NAME
    ){
        super();
        ENEMY_MODEL_NAME = MODEL_NAME;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.setLocalTranslation(position);
        //this.rotate(0,0,FastMath.HALF_PI);
        this.model = assetManager.loadModel(ENEMY_MODEL_NAME);
        //this.model.rotate(-FastMath.HALF_PI,0,0);
        this.model.setLocalTranslation(0,-6,0);

        this.model.setName(name);
        this.attachChild(model);
        this.characterCollider = new CapsuleCollisionShape(0.3f,0.8f);
        this.characterControl = new RigidBodyControl(this.characterCollider,0.1f);
        this.characterControl.setFriction(0);
        this.characterControl.setAngularFactor(0);
        //this.characterControl.setGravity(new Vector3f().zero());
        //this.characterControl.setKinematic(true);
        this.addControl(characterControl);
        this.bulletAppState.getPhysicsSpace().addAll(this);
        this.characterControl.setGravity(new Vector3f().zero());
        rotateTowardsPlayer(target);
        moveTowardsPlayer(target);
    }

    public String getName() {
        return model.getName();
    }

    @Override
    public void update(float tpf) {

    }

    private void rotateTowardsPlayer(Vector3f playerPos){
        //characterControl.setPhysicsRotation(characterControl.getPhysicsRotation().add(new Quaternion().fromAngleAxis(XZVelocityVectorToYRotation,Vector3f.UNIT_Y)));
        //characterControl.setPhysicsRotation(new Quaternion().fromAngles(FastMath.HALF_PI,XZVelocityVectorToYRotation+FastMath.PI,0));
        characterControl.setPhysicsRotation(createQuaternionFromVector(playerPos.subtract(characterControl.getPhysicsLocation())));

    }
    private void moveTowardsPlayer(Vector3f playerPos){
        characterControl.applyImpulse((playerPos.subtract(characterControl.getPhysicsLocation())).normalize().mult(speed),new Vector3f().zero());
    }
    /*private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }*/
    public static Quaternion createQuaternionFromVector(Vector3f vector) {

        Vector3f normalizedVector = vector.normalize();

        Vector3f forward = new Vector3f(0, 1, 0);

        float dot = forward.dot(normalizedVector);
        float angle = (float) Math.acos(dot);

        Vector3f axis = forward.cross(normalizedVector).normalizeLocal();

        if (axis.lengthSquared() == 0) {

            axis = new Vector3f(1, 0, 0);
        }

        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(angle, axis);

        return quaternion;
    }
}
