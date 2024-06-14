package org.konceptosociala.kareladventures.game.player;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tweens;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.*;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Geometry;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Setter;
import lombok.Getter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class Player extends Node implements IUpdatable {
    private static final String PLAYER_MODEL_NAME = "Models/karel.glb";

    private BulletAppState bulletAppState;
    private GameState thisGameState;
    private AssetManager thisAssetManager;

    private Spatial model;
    private CollisionShape characterCollider;
    private RigidBodyControl characterControl;
    private Box legs;
    private AnimComposer animComposer;
    private float speed = 8f;
    private int movingForward = 0;
    private int movingSideward = 0;
    private boolean onGround = false;
    private Vector3f sidewardMovement = new Vector3f().zero();
    private Vector3f forwardMovement = new Vector3f().zero();

    private Health health;
    private Inventory inventory;
    private int balance = 150;


    public Player(AssetManager assetManager, Vector3f position, BulletAppState state) {
        super();
        thisAssetManager = assetManager;
        bulletAppState = state;
        setLocalTranslation(position);
        model = assetManager.loadModel(PLAYER_MODEL_NAME);
        model.setLocalTranslation(0,-1f,0);
        model.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI,new Vector3f(0,1,0)));
        attachChild(model);
        characterCollider = new CapsuleCollisionShape(0.5f,1f);
        characterControl = new RigidBodyControl(characterCollider, 1);
        characterControl.setFriction(1);
        characterControl.setGravity(new Vector3f(0,-10,0));
        characterControl.setAngularFactor(0f);
        addControl(characterControl);
        legs = new Box(1,1,1);
        health = new Health(100);
        inventory = Inventory.test();
        animComposer = model.getControl(AnimComposer.class);
        bulletAppState.getPhysicsSpace().add(characterControl);
        bulletAppState.getPhysicsSpace().addAll(this);
    }
    
    public boolean isAlive() {
        return health.getValue() > 0;
    }
    public void takeDamage(int amount){
        health.subtract(amount);
    }

    public void update(float tpf) {
        onGround=checkIfOnGround(characterControl.getPhysicsLocation(),new Vector3f(0.01f,0.01f,0.01f));
        //rotateInMovementDirection();
        characterControl.setLinearVelocity((forwardMovement.add(sidewardMovement)).setY(characterControl.getLinearVelocity().y));
        sidewardMovement = new Vector3f().zero();
        forwardMovement = new Vector3f().zero();
        movingSideward=0;
        movingForward=0;
    }
    private void rotateInMovementDirection(float a){
            //float XZVelocityVectorToYRotation = FastMath.atan2(characterCollider.getLinearVelocity().x,characterCollider.getLinearVelocity().z)+FastMath.HALF_PI;/**FastMath.sign(characterCollider.getLinearVelocity().x*characterCollider.getLinearVelocity().z);*/
            //float XZVelocityVectorToYRotation = a;
            characterControl.setPhysicsRotation(characterControl.getPhysicsRotation().slerp(characterControl.getPhysicsRotation(),new Quaternion().fromAngleAxis(a,new Vector3f(0,1,0)),0.1f));
    }

    public void jump(){
        if(!onGround){
            return;
        }

        characterControl.applyImpulse(new Vector3f(0,10,0),new Vector3f(0,0,0));
    }

    public void roll(){
        /*RigidBodyControl rollRigidBody = new RigidBodyControl(new SphereCollisionShape(1),1);
        //copyRigidBodyStats(rollRigidBody,characterControl);
        model.removeControl(characterControl);
        characterControl.setEnabled(false);
        copyRigidBodyStats(rollRigidBody,characterControl);
        model.addControl(rollRigidBody);*/
        Tweens.sequence(Tweens.delay(1),Tweens.callMethod(this,"jump"/*,rollRigidBody*/),Tweens.delay(1)/*,Tweens.callMethod(this,"returnToNormalCollider",rollRigidBody)*/);

    }

    public void moveForward(float value,float rad) {
        movingForward = 1;
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI;
        rotateInMovementDirection(yGlobalMovementAngle+FastMath.HALF_PI);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        //characterControl.applyForce(rotateByYAxis(new Vector3f(0,0,appliedForce),yGlobalMovementAngle),new Vector3f(0,0,0));
        //characterControl.setLinearVelocity(rotateByYAxis(new Vector3f(0,characterControl.getLinearVelocity().y,appliedForce),yGlobalMovementAngle));
        forwardMovement = rotateByYAxis(new Vector3f(0,characterControl.getLinearVelocity().y,appliedForce),yGlobalMovementAngle);
    }

    public void moveSideward(float value,float rad) {
        movingSideward = 1;
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI+FastMath.HALF_PI*Math.signum(value*2-1);
        rotateInMovementDirection(yGlobalMovementAngle+FastMath.HALF_PI);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        //characterControl.applyForce(rotateByYAxis(new Vector3f(appliedForce,0,0),yGlobalMovementAngle-FastMath.HALF_PI),new Vector3f(0,0,0));
        //characterControl.setLinearVelocity(rotateByYAxis(new Vector3f(appliedForce,characterControl.getLinearVelocity().y,0),yGlobalMovementAngle-FastMath.HALF_PI));
        sidewardMovement = rotateByYAxis(new Vector3f(appliedForce,characterControl.getLinearVelocity().y,0),yGlobalMovementAngle-FastMath.HALF_PI);
    }

    public List<Spatial> getEnemiesInBox(Vector3f center, Vector3f extents, Quaternion rotation) {
        List<Spatial> enemies = new ArrayList<>();
        Box boxShape = new Box(extents.x, extents.y, extents.z);
        Geometry collider = new Geometry("Collider", boxShape);
        collider.setLocalTranslation(center);
        collider.setLocalRotation(rotation);
        Transform transform = new Transform(center, rotation);
        BoundingBox boundingBox = new BoundingBox(center, extents.x, extents.y, extents.z);
        boundingBox.transform(transform);
        // Create a material with an unshaded definition
        //Material mat = new Material(thisAssetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        // Set the material color to blue
        //mat.setColor("Color", ColorRGBA.Blue);
        //collider.setMaterial(mat);
        for (Spatial spatial : thisGameState.getEnemyRoot().getChildren()) {
            if (boundingBox.intersects(spatial.getWorldBound())) {
                enemies.add(spatial);
            }
        }
        //thisGameState.getRootNode().attachChild(collider);
        return enemies;
    }

    public void attack(AttackType attackType){
        switch (attackType){
            case Melee:
                performMeleeAttack(2,1f,0.5f);
                break;

            case Ranged:
                break;
        }
    }

    private boolean checkIfOnGround(Vector3f center, Vector3f extents){
        Vector3f characterPosition = characterControl.getPhysicsLocation();
        Vector3f rayFrom = characterPosition;
        Vector3f rayTo = characterPosition.add(0, -1.5f, 0);
        List<PhysicsRayTestResult> results = bulletAppState.getPhysicsSpace().rayTest(rayFrom, rayTo);
        for (PhysicsRayTestResult result : results) {
            if (result.getCollisionObject() != characterControl) {
                return true;
            }
        }
        return false;
    }

    // private void rotateInMovementDirection(){
    //     if(characterControl.getLinearVelocity().mult(1,0,1).length()>0.1){
    //         float XZVelocityVectorToYRotation = FastMath.atan2(characterControl.getLinearVelocity().x,characterControl.getLinearVelocity().z)+FastMath.HALF_PI;/**FastMath.sign(characterControl.getLinearVelocity().x*characterControl.getLinearVelocity().z);*/
    //         //System.out.println(XZVelocityVectorToYRotation+";"+characterControl.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0)));
    //         //float TORQUE = (XZVelocityVectorToYRotation-characterControl.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0))/2);
    //         //System.out.println(TORQUE);
    //         //float a = (XZVelocityVectorToYRotation+characterControl.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0))*2-);
    //         characterControl.setPhysicsRotation(new Quaternion().fromAngleAxis(XZVelocityVectorToYRotation,new Vector3f(0,1,0)));
    //         //characterControl.setAngularVelocity(new Vector3f(0,TORQUE,0));
    //     } else {
    //         //characterControl.setAngularVelocity(new Vector3f(0,0,0));
    //     }
    // }

    // private void addRollImpulse(RigidBodyControl rollRigidBody){
    //     rollRigidBody.applyImpulse(quaternionToDirection(rollRigidBody.getPhysicsRotation()).mult(10),new Vector3f(0,0,0));
    // }

    // private void returnToNormalCollider(RigidBodyControl rollRigidBody){
    //     rollRigidBody.setEnabled(false);
    // }

    private void performMeleeAttack(float length, float width, float height){
        Vector3f attackColliderOffset = new Vector3f();
        model.localToWorld(new Vector3f(0,1,length),attackColliderOffset);
        List<Spatial> enemiesToAffect = getEnemiesInBox(attackColliderOffset,new Vector3f(length,height,width),characterControl.getPhysicsRotation());
        for (Spatial i: enemiesToAffect) {
            if(i instanceof IAmEnemy){
                ((IAmEnemy) i).receiveDamage(10);
                ((IAmEnemy) i).pushback();
            }
        }
    }

    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }

    // private static Vector3f quaternionToDirection(Quaternion q) {
    //     // Normalize the quaternion (assuming it might not be already)
    //     double w = q.getW();
    //     double x = q.getX();
    //     double y = q.getY();
    //     double z = q.getZ();
    //     double norm = Math.sqrt(w * w + x * x + y * y + z * z);
    //     w /= norm;
    //     x /= norm;
    //     y /= norm;
    //     z /= norm;

    //     // Double the first 3 components for efficiency (common factor in rotation formula)
    //     double x2 = 2.0 * x;
    //     double y2 = 2.0 * y;
    //     double z2 = 2.0 * z;

    //     // Rotation formula (assuming a unit vector as input)
    //     double[] direction = new double[3];
    //     direction[0] = 1.0 - y2 * y - z2 * z;
    //     direction[1] = 2.0 * (x * y + w * z);
    //     direction[2] = 2.0 * (x * z - w * y);

    //     return new Vector3f((float)direction[0],(float)direction[1],(float)direction[2]);
    // }

    // private static void copyRigidBodyStats(RigidBodyControl to,RigidBodyControl from){
    //     to.setPhysicsRotation(from.getPhysicsRotation());
    //     to.setAngularVelocity(from.getAngularVelocity());
    //     to.setGravity(from.getGravity());
    //     to.setFriction(from.getFriction());
    //     to.setPhysicsSpace(from.getPhysicsSpace());
    //     to.setPhysicsLocation(from.getPhysicsLocation());
    // }
}
