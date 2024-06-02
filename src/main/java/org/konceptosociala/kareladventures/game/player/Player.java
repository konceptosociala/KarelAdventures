package org.konceptosociala.kareladventures.game.player;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tweens;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import lombok.Setter;
import org.konceptosociala.kareladventures.game.inventory.Inventory;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;


import lombok.Getter;

@Getter
public class Player implements IUpdatable {
    private static final String PLAYER_MODEL_NAME = "Models/karel.glb";

    @Getter
    private RigidBodyControl characterCollider;
    private final Spatial model;

    @Getter
    @Setter
    private Node playerRoot;

    private Health health;
    private Energy energy;
    private Inventory inventory;
    private float speed = 8f;

    AnimComposer animComposer;
    @Setter
    private int movingForward = 0;
    @Setter
    private int movingSideward = 0;
    public Player(AssetManager assetManager,Vector3f position) {
        playerRoot =new Node();
        model = assetManager.loadModel(PLAYER_MODEL_NAME);
        model.setLocalTranslation(10,10,10);
        model.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI,new Vector3f(0,1,0)));
        characterCollider = new RigidBodyControl(new CapsuleCollisionShape(1,1),1);
        characterCollider.setFriction(1);
        model.addControl(characterCollider);
        playerRoot.attachChild(model);

        health = new Health(100);
        energy = new Energy(100);
        inventory = Inventory.test();
        animComposer = model.getControl(AnimComposer.class);
    }

    
    public boolean isAlive() {
        return health.getValue() > 0;
    }

    public void update() {
        if(characterCollider.getLinearVelocity().mult(1,0,1).length()>1){
            float XZVelocityVectorToYRotation = FastMath.atan2(characterCollider.getLinearVelocity().x,characterCollider.getLinearVelocity().z);/**FastMath.sign(characterCollider.getLinearVelocity().x*characterCollider.getLinearVelocity().z);*/
            //System.out.println(XZVelocityVectorToYRotation+";"+characterCollider.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0)));
            float TORQUE = (XZVelocityVectorToYRotation+characterCollider.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0))/2);
            System.out.println(TORQUE);
            //float a = (XZVelocityVectorToYRotation+characterCollider.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0)))/2;
            characterCollider.setPhysicsRotation(new Quaternion().fromAngleAxis(TORQUE,new Vector3f(0,1,0)));
            //characterCollider.setAngularVelocity(new Vector3f(0,TORQUE,0));
        }else {
            characterCollider.setAngularVelocity(new Vector3f(0,0,0));
        }
    }
    public void jump(){
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
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        //characterCollider.setPhysicsRotation(new Quaternion().fromAngleAxis(zGlobalMovementAngle,new Vector3f(0,1,0)));
        characterCollider.applyForce(rotateByYAxis(new Vector3f(0,0,appliedForce),yGlobalMovementAngle),new Vector3f(0,0,0));
//        float a = (yGlobalMovementAngle+characterCollider.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0)))/2;
//        characterCollider.setPhysicsRotation(new Quaternion().fromAngleAxis(a,new Vector3f(0,1,0)));
        //characterCollider.applyForce(new Vector3f(1*value*speed,0,0),new Vector3f(0,0,0));
        //model.move(characterCollider.getWalkDirection());
    }
    public void moveSideward(float value,float rad) {
        movingForward = 1;
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI+FastMath.HALF_PI*Math.signum(value*2-1);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        //characterCollider.setPhysicsRotation(new Quaternion().fromAngleAxis(zGlobalMovementAngle,new Vector3f(0,1,0)));
        characterCollider.applyForce(rotateByYAxis(new Vector3f(appliedForce,0,0),yGlobalMovementAngle-FastMath.HALF_PI),new Vector3f(0,0,0));
//        float a = (yGlobalMovementAngle+characterCollider.getPhysicsRotation().toAngleAxis(new Vector3f(0,1,0)))/2;
//        characterCollider.setPhysicsRotation(new Quaternion().fromAngleAxis(a,new Vector3f(0,1,0)));
        //characterCollider.applyForce(new Vector3f(0,0,1*value*speed),new Vector3f(0,0,0));
        //model.move(characterCollider.getWalkDirection());
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
