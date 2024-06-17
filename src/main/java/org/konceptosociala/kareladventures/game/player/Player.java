package org.konceptosociala.kareladventures.game.player;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
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

import com.sun.media.jfxmedia.logging.Logger;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Setter;
import lombok.Getter;

import java.util.List;
import java.util.ArrayList;

import static org.konceptosociala.kareladventures.KarelAdventures.LOG;

@Getter
@Setter
public class Player extends Node implements IUpdatable {
    private static final String PLAYER_MODEL_NAME = "Models/karel.glb";

    private BulletAppState bulletAppState;
    private GameState thisGameState;

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
    public Player(AssetManager assetManager, Vector3f position, BulletAppState state, boolean cheatsEnabled) {
        super();
        bulletAppState = state;
        setLocalTranslation(position);
        model = assetManager.loadModel(PLAYER_MODEL_NAME);
        model.setLocalTranslation(0,-1f,0);
        model.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI,new Vector3f(0,1,0)));
        attachChild(model);
        animComposer = ((Node)this.model).getChild(0).getControl(AnimComposer.class);
        //animComposer.setGlobalSpeed(3);
        animComposer.setCurrentAction("idle");

        /*Action walk = animComposer.action("walk");
        Tween doneTween = Tweens.callMethod(animComposer, "setCurrentAction", "idle");
        Action walkOnce = animComposer.actionSequence("walkOnce", walk, doneTween);
        animComposer.setCurrentAction("walkOnce");*/

        characterCollider = new CapsuleCollisionShape(0.5f,1f);
        characterControl = new RigidBodyControl(characterCollider, 1);
        characterControl.setFriction(0);
        characterControl.setGravity(new Vector3f(0,-10,0));
        characterControl.setAngularFactor(0f);
        addControl(characterControl);
        legs = new Box(1,1,1);
        health = new Health(100);

        if (cheatsEnabled)
            inventory = Inventory.cheats();
        else 
            inventory = new Inventory();

        animComposer = model.getControl(AnimComposer.class);
        bulletAppState.getPhysicsSpace().add(characterControl);
        bulletAppState.getPhysicsSpace().addAll(this);
    }
    
    public boolean isAlive() {
        return health.getValue() > 0;
    }
    public void takeDamage(int amount){
        int boots = (int)(inventory.getBoots()!=null?(inventory.getBoots().getBenefit().orElse(0L)) :0L);
        int legs = (int)(inventory.getLeggings()!=null?(inventory.getBoots().getBenefit().orElse(0L)) :0L);
        int chest = (int)(inventory.getChestplate()!=null?(inventory.getBoots().getBenefit().orElse(0L)) :0L);
        int head = (int)(inventory.getHelmet()!=null?(inventory.getBoots().getBenefit().orElse(0L)) :0L);
        int a = (amount-(boots+legs+chest+head)/4);
        LOG.info(String.valueOf(a));
        health.subtract(Math.max(a, 0));
    }

    @Override
    public void update(float tpf) {
        animComposer = ((Node)this.model).getChild(0).getControl(AnimComposer.class);
        if(movingForward+movingSideward!=0){
            if(animComposer.getCurrentAction()==animComposer.getAction("walk")){

            }else{
                animComposer.setCurrentAction("walk").setSpeed(4);
            }
        }else {
            if(animComposer.getCurrentAction()==animComposer.getAction("idle")){

            }else{
                animComposer.setCurrentAction("idle");
            }
        }
        if (!isAlive()) {
            thisGameState.getAudio().death.stop();
            thisGameState.getAudio().death.play();
            bulletAppState.getPhysicsSpace().remove(characterControl);
            thisGameState.getRootNode().detachChild(this);
            return;
        }
        
        onGround=checkIfOnGround(characterControl.getPhysicsLocation(),new Vector3f(0.01f,0.01f,0.01f));
        characterControl.setLinearVelocity((forwardMovement.add(sidewardMovement)).setY(characterControl.getLinearVelocity().y));
        sidewardMovement = new Vector3f().zero();
        forwardMovement = new Vector3f().zero();
        movingSideward=0;
        movingForward=0;
    }
    private void rotateInMovementDirection(float a){
            characterControl.setPhysicsRotation(characterControl.getPhysicsRotation().slerp(characterControl.getPhysicsRotation(),new Quaternion().fromAngleAxis(a,new Vector3f(0,1,0)),0.1f));
    }

    public void jump(){
        if(!onGround){
            return;
        }

        characterControl.applyImpulse(new Vector3f(0,10,0),new Vector3f(0,0,0));
    }

    public void roll(){
        Tweens.sequence(Tweens.delay(1),Tweens.callMethod(this,"jump"/*,rollRigidBody*/),Tweens.delay(1)/*,Tweens.callMethod(this,"returnToNormalCollider",rollRigidBody)*/);

    }
    private void walk1Time(){
        animComposer = ((Node)this.model).getChild(0).getControl(AnimComposer.class);
        //animComposer.setGlobalSpeed(3);
        Action walk = animComposer.action("walk");
        //walk.setSpeed(1000);
        Tween doneTween = Tweens.callMethod(animComposer, "setCurrentAction", "idle");
        Action walkOnce = animComposer.actionSequence("walkOnce", walk, doneTween);
        animComposer.setCurrentAction("walkOnce").setSpeed(4);
        //animComposer.setGlobalSpeed(1);
    }
    public void moveForward(float value,float rad) {
        movingForward = 1;
        //walk1Time();
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI;
        rotateInMovementDirection(yGlobalMovementAngle+FastMath.HALF_PI);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
        forwardMovement = rotateByYAxis(new Vector3f(0,characterControl.getLinearVelocity().y,appliedForce),yGlobalMovementAngle);
    }

    public void moveSideward(float value,float rad) {
        movingSideward = 1;
        //walk1Time();
        float yGlobalMovementAngle = (-rad-FastMath.HALF_PI)+(Math.signum(value)+1)*FastMath.HALF_PI+FastMath.HALF_PI*Math.signum(value*2-1);
        rotateInMovementDirection(yGlobalMovementAngle+FastMath.HALF_PI);
        float appliedForce = speed/Math.max(FastMath.sqrt(movingForward+movingSideward),1);
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

    private void performMeleeAttack(float length, float width, float height){
        Vector3f attackColliderOffset = new Vector3f();
        model.localToWorld(new Vector3f(0,1,length),attackColliderOffset);
        List<Spatial> enemiesToAffect = getEnemiesInBox(attackColliderOffset,new Vector3f(length,height,width),characterControl.getPhysicsRotation());
        for (Spatial i: enemiesToAffect) {
            if(i instanceof IAmEnemy){
                int sword = (int)(inventory.getWeapon()!=null?(inventory.getWeapon().getBenefit().orElse(0L)) :0L);
                ((IAmEnemy) i).receiveDamage(10+sword*3);
                ((IAmEnemy) i).pushback();
            }
        }
    }

    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }
}
