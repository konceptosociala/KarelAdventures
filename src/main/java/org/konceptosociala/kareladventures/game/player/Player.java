package org.konceptosociala.kareladventures.game.player;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tweens;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Geometry;
import lombok.Setter;
import org.konceptosociala.kareladventures.game.enemies.Enemy;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;


import lombok.Getter;
import org.konceptosociala.kareladventures.state.GameState;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;
////


@Getter
public class Player extends Node implements IUpdatable {
    private static final String PLAYER_MODEL_NAME = "Models/karel.glb";

    private BulletAppState bulletAppState;
    @Setter
    private GameState thisGameState;
    private Node worldRoot;

    @Getter
    private RigidBodyControl characterCollider;

    private final Spatial model;
    //private RigidBodyControl characterCollider;
    private Health health;
    private Energy energy;
    private Inventory inventory;
    private float speed = 8f;
    private Box legs;

    AnimComposer animComposer;
    @Setter
    private int movingForward = 0;
    @Setter
    private int movingSideward = 0;
    private boolean onGround = false;
    private Node playerRoot;
    public Player(AssetManager assetManager, Vector3f position, Node worldRoot, BulletAppState state/*, GameState gs*/) {
        //thisGameState = gs;
        bulletAppState = state;
        this.worldRoot = worldRoot;
        playerRoot =new Node();
        playerRoot.setLocalTranslation(position);
        model = assetManager.loadModel(PLAYER_MODEL_NAME);
        model.setLocalTranslation(0,-1f,0);
        model.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI,new Vector3f(0,1,0)));
        playerRoot.attachChild(model);
        characterCollider = new RigidBodyControl(new CapsuleCollisionShape(0.5f,1f),1);
        characterCollider.setFriction(1);
        //characterCollider.setGravity(new Vector3f(0,-10,0));
        playerRoot.addControl(characterCollider);
        legs = new Box(1,1,1);
        health = new Health(100);
        energy = new Energy(100);
        inventory = Inventory.test();
        animComposer = model.getControl(AnimComposer.class);
        worldRoot.attachChild(playerRoot);
        bulletAppState.getPhysicsSpace().addAll(this);
    }

    
    public boolean isAlive() {
        return health.getValue() > 0;
    }

    public void update() {
        onGround=checkIfOnGround(characterCollider.getPhysicsLocation(),new Vector3f(0.01f,0.01f,0.01f));
        //rotateInMovementDirection();
        movingSideward=0;
        movingForward=0;
    }
    private boolean checkIfOnGround(Vector3f center, Vector3f extents){
        Vector3f characterPosition = characterCollider.getPhysicsLocation();
        Vector3f rayFrom = characterPosition;
        Vector3f rayTo = characterPosition.add(0, -1.5f, 0);
        List<PhysicsRayTestResult> results = bulletAppState.getPhysicsSpace().rayTest(rayFrom, rayTo);
        for (PhysicsRayTestResult result : results) {
            if (result.getCollisionObject() != characterCollider) {
                return true;
            }
        }
        return false;
    }
    private void rotateInMovementDirection(float a){
            //float XZVelocityVectorToYRotation = FastMath.atan2(characterCollider.getLinearVelocity().x,characterCollider.getLinearVelocity().z)+FastMath.HALF_PI;/**FastMath.sign(characterCollider.getLinearVelocity().x*characterCollider.getLinearVelocity().z);*/
            //float XZVelocityVectorToYRotation = a;
            characterCollider.setPhysicsRotation(characterCollider.getPhysicsRotation().slerp(characterCollider.getPhysicsRotation(),new Quaternion().fromAngleAxis(a,new Vector3f(0,1,0)),0.1f));
    }

    public void jump(){
        if(!onGround){
            return;
        }
        characterCollider.applyImpulse(new Vector3f(0,10,0),new Vector3f(0,0,0));
    }

    public void roll(){
        /*RigidBodyControl rollRigidBody = new RigidBodyControl(new SphereCollisionShape(1),1);
        //copyRigidBodyStats(rollRigidBody,characterCollider);
        model.removeControl(characterCollider);
        characterCollider.setEnabled(false);
        copyRigidBodyStats(rollRigidBody,characterCollider);
        model.addControl(rollRigidBody);*/
        Tweens.sequence(Tweens.delay(1),Tweens.callMethod(this,"jump"/*,rollRigidBody*/),Tweens.delay(1)/*,Tweens.callMethod(this,"returnToNormalCollider",rollRigidBody)*/);

    }

    private void addRollImpulse(RigidBodyControl rollRigidBody){
        rollRigidBody.applyImpulse(quaternionToDirection(rollRigidBody.getPhysicsRotation()).mult(10),new Vector3f(0,0,0));
    }

    private void returnToNormalCollider(RigidBodyControl rollRigidBody){
        rollRigidBody.setEnabled(false);
    }

    public void moveForward(float value,float rad) {
        movingForward = 1;
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI;
        rotateInMovementDirection(yGlobalMovementAngle+FastMath.HALF_PI);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        characterCollider.applyForce(rotateByYAxis(new Vector3f(0,0,appliedForce),yGlobalMovementAngle),new Vector3f(0,0,0));
    }
    public void moveSideward(float value,float rad) {
        movingSideward = 1;
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI+FastMath.HALF_PI*Math.signum(value*2-1);
        rotateInMovementDirection(yGlobalMovementAngle+FastMath.HALF_PI);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        characterCollider.applyForce(rotateByYAxis(new Vector3f(appliedForce,0,0),yGlobalMovementAngle-FastMath.HALF_PI),new Vector3f(0,0,0));
    }

    public void attack(String attackType){
        switch (attackType){
            case("melee"):
                performMeleeAttack(2,3,1);
                break;
            case ("ranged"):
                break;
            default:
                break;
        }
    }

    private void performMeleeAttack(float width, float length, float height){
        List<Enemy> enemiesToAffect = getEnemiesInBox(characterCollider.getPhysicsLocation(),new Vector3f(width,height,length),characterCollider.getPhysicsRotation());
        System.out.println(enemiesToAffect.size());
        for (Enemy i: enemiesToAffect){
            i.receiveDamage(10);
            i.pushback();
        }
    }
    public List<Enemy> getEnemiesInBox(Vector3f center, Vector3f extents, Quaternion rotation) {
        Node rootNode = worldRoot;
        List<Enemy> enemies = new ArrayList<>();

        // Create a box shape for the collider
        Box boxShape = new Box(extents.x, extents.y, extents.z);

        // Create a geometry for the collider
        Geometry collider = new Geometry("Collider", boxShape);
        collider.setLocalTranslation(center);
        collider.setLocalRotation(rotation);

        // Apply the collider's world transform to get the bounding volume in world space
        Transform transform = new Transform(center, rotation);
        BoundingBox boundingBox = new BoundingBox(center, extents.x, extents.y, extents.z);
        boundingBox.transform(transform);

        // Iterate through all children of the rootNode
        for (Spatial spatial : thisGameState.getEnemyRoot().getChildren()) {
            // Check if the spatial intersects with the bounding box
            if (boundingBox.intersects(spatial.getWorldBound())) {
                // Check if the spatial is an instance of Enemy
                if (spatial instanceof Enemy) {
                    enemies.add((Enemy)spatial);
                }
            }
        }

        // The collider is not added to the scene graph, so no need to remove it

        return enemies;
    }

    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }
    public static Vector3f quaternionToDirection(Quaternion q) {
        // Normalize the quaternion (assuming it might not be already)
        double w = q.getW();
        double x = q.getX();
        double y = q.getY();
        double z = q.getZ();
        double norm = Math.sqrt(w * w + x * x + y * y + z * z);
        w /= norm;
        x /= norm;
        y /= norm;
        z /= norm;

        // Double the first 3 components for efficiency (common factor in rotation formula)
        double x2 = 2.0 * x;
        double y2 = 2.0 * y;
        double z2 = 2.0 * z;

        // Rotation formula (assuming a unit vector as input)
        double[] direction = new double[3];
        direction[0] = 1.0 - y2 * y - z2 * z;
        direction[1] = 2.0 * (x * y + w * z);
        direction[2] = 2.0 * (x * z - w * y);

        return new Vector3f((float)direction[0],(float)direction[1],(float)direction[2]);
    }

    private static void copyRigidBodyStats(RigidBodyControl to,RigidBodyControl from){
        to.setPhysicsRotation(from.getPhysicsRotation());
        to.setAngularVelocity(from.getAngularVelocity());
        to.setGravity(from.getGravity());
        to.setFriction(from.getFriction());
        to.setPhysicsSpace(from.getPhysicsSpace());
        to.setPhysicsLocation(from.getPhysicsLocation());
    }
}
