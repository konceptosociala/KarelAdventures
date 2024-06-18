package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.konceptosociala.kareladventures.game.player.Health;
import org.konceptosociala.kareladventures.state.CreditsGameState;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class Boss extends Node implements IUpdatable, IAmEnemy {
    private static final String ENEMY_MODEL_NAME = "Models/boss.glb";

    private BulletAppState bulletAppState;
    private GameState thisGameState;

    private Spatial model;
    private CollisionShape characterCollider;
    private RigidBodyControl characterControl;
    private Health health;
    private float XZVelocityVectorToYRotation;
    private AssetManager assetManager;
    private int damage = 4;
    private float attackChangeCooldownTime = 5f;
    private float attackChangeCooldownTimer = 0.0f;
    private float attackCooldownTime = 100f;
    private float attackCooldownTimer = 0.0f;
    private boolean attackAvailable = false;
    private Vector3f[] teleportingPositions = new Vector3f[]{new Vector3f(-27.150446f, 2.4794924f, 157.56384f),new Vector3f(20.346401f, 2.1194615f, 186.4145f),new Vector3f(16.002697f, 2.6495476f, 221.6374f),new Vector3f(-28.506155f, 1.9535697f, 198.31865f)};
    private Vector3f target;
    private AnimComposer animComposer;
    private int wasHealth;
    private int healthThreshold = 30;
    private int pos = 0;
    private int currentAtack = 1;
    private int numberOfAttacks = 3;
    private Random rand = new Random();

    public Boss(
            Vector3f position,
            AssetManager assetManager,
            BulletAppState bulletAppState
    ){
        super();
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.setLocalTranslation(position);
        this.model = assetManager.loadModel(ENEMY_MODEL_NAME);
        this.model.setLocalTranslation(0,-3.5f,0);
        this.model.scale(1f);
        this.model.setName(name);
        this.attachChild(model);
        animComposer = ((Node)this.model).getChild(0).getControl(AnimComposer.class);
        animComposer.setCurrentAction("big_spell");

        this.health = new Health(200);
        wasHealth = 200;
        this.characterCollider = new CapsuleCollisionShape(2f,4f);
        this.characterControl = new RigidBodyControl(this.characterCollider,9999f);
        this.characterControl.setFriction(1);
        this.characterControl.setAngularFactor(0);
        this.addControl(characterControl);
        this.bulletAppState.getPhysicsSpace().addAll(this);
    }

    public String getName() {
        return model.getName();
    }

    public int getHealth() {
        return health.getValue();
    }

    public boolean isAlive() {
        return health.getValue() > 0;
    }
    public void receiveDamage(int amount){
        this.health.subtract(amount);
    }

    @Override
    public void update(float tpf) {
        target = thisGameState.getPlayer().getCharacterControl().getPhysicsLocation();
        if(!isAlive()){
            thisGameState.getAudio().insectDeath.stop();
            thisGameState.getAudio().insectDeath.play();
            //Action attack = animComposer.action("death");
            //Tween doneTween = Tweens.callMethod(animComposer.getCurrentAction(), "setSpeed", "0");
            //Action attackOnce = animComposer.actionSequence("attackOnce", attack, at);
            Action die = animComposer.action("death");
            Action dying =  animComposer.actionSequence("dying",
                    die, Tweens.callMethod(this, "onDeath"));
            animComposer.setCurrentAction("dying");
            try {
                thisGameState.getAppStateManager().attach(new CreditsGameState(thisGameState)); 
                thisGameState.getAppStateManager().detach(thisGameState);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            //bulletAppState.getPhysicsSpace().remove(characterControl);
            //thisGameState.getEnemyRoot().detachChild(this);
            return;
        }
        if(wasHealth-health.getValue()>healthThreshold){
            wasHealth = health.getValue();
            teleport();
        }
        calculateYRotation(target);
        rotateTowardsTarget();
        attackChangeCooldownTimer += tpf;
        if (attackChangeCooldownTimer >= attackChangeCooldownTime) {
            attackAvailable = true;
            attackChangeCooldownTimer = 0.0f;
            currentAtack = rand.nextInt(numberOfAttacks);
            switch (currentAtack){
                case 0:
                    attackCooldownTime = 0.1f;
                    break;
                case 1:
                    attackCooldownTime = 1f;
                    break;
                case 2:
                    attackCooldownTime = 3f;
                    break;
            }
        }
        if (!attackAvailable) {
            attackCooldownTimer += tpf;
            if (attackCooldownTimer >= attackCooldownTime) {
                attackAvailable = true;
                attackCooldownTimer = 0.0f;
                performAttack();
            }
        }
        performAttack();
    }
    public void onDeath(){
        thisGameState.getPlayer().setBalance(thisGameState.getPlayer().getBalance()+1000);
        bulletAppState.getPhysicsSpace().remove(characterControl);
        thisGameState.getEnemyRoot().detachChild(this);
    }

    private void teleport() {
        int i = rand.nextInt(teleportingPositions.length);
        Action attack = animComposer.action("jump");
        Tween doneTween = Tweens.callMethod(animComposer, "setCurrentAction", "big_spell");
        Action attackOnce = animComposer.actionSequence("attackOnce", attack, doneTween);
        animComposer.setCurrentAction("attackOnce");
        while(i == pos){
            i = rand.nextInt(teleportingPositions.length);
        }
        pos = i;
        characterControl.setPhysicsLocation(teleportingPositions[i].add(0,5,0));
    }

    private void calculateYRotation(Vector3f target){
        var location = characterControl.getPhysicsLocation();
        XZVelocityVectorToYRotation = FastMath.atan2(location.x - target.x, location.z - target.z);
    }
    private void performAttack(){
        if(attackAvailable){
            var location = characterControl.getPhysicsLocation();
            var playerLocation = thisGameState.getPlayer().getCharacterControl().getPhysicsLocation();
            //if(FastMath.sqrt(FastMath.pow(location.x - playerLocation.x,2)+FastMath.pow(location.z - playerLocation.z,2))<attackRange){
            Action attack = animComposer.action("medium_spell");
            Tween doneTween = Tweens.callMethod(animComposer, "setCurrentAction", "big_spell");
            Action attackOnce = animComposer.actionSequence("attackOnce", attack, doneTween);
            animComposer.setCurrentAction("attackOnce");
            switch (currentAtack){
                case 0:
                    nimbusAttack();
                    break;
                case 1:
                    encirclementAttack();
                    break;
                case 2:
                    machinegunAttack();
                    break;
            }
            attackAvailable = false;
            attackCooldownTimer = 0.0f;
            //}
        }
    }

    private void shoot() {
        Bullet bullet =  new Bullet(characterControl.getPhysicsLocation().add(0,5,0),thisGameState.getPlayer().getCharacterControl().getPhysicsLocation().add(0,0,0),assetManager,bulletAppState,"Models/enemy_bullet.glb",20,damage,0.3f,0.8f);
        thisGameState.getRootNode().attachChild(bullet);
    }
    @Override
    public void setHealth(int health) {
        this.health.setValue(health);
    }

    private void rotateTowardsTarget(){
        //characterControl.setPhysicsRotation(characterControl.getPhysicsRotation().add(new Quaternion().fromAngleAxis(XZVelocityVectorToYRotation,Vector3f.UNIT_Y)));
        characterControl.setPhysicsRotation(new Quaternion().fromAngles(0,XZVelocityVectorToYRotation+FastMath.PI,0));
    }
    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }

    public void pushback(){
    }

    public void nimbusAttack(){
        shoot();
    }
    public void encirclementAttack(){
        int radius = 30;
        int count = 30;
        for(int i = 0;i<count;i++){
            float angle = (FastMath.TWO_PI/count)*i;
            Bullet bullet =  new Bullet(thisGameState.getPlayer().getCharacterControl().getPhysicsLocation().add(FastMath.sin(angle)*radius,0,FastMath.cos(angle)*radius),thisGameState.getPlayer().getCharacterControl().getPhysicsLocation().add(0,0,0),assetManager,bulletAppState,"Models/enemy_bullet.glb",20,damage,0.3f,0.8f);
            thisGameState.getRootNode().attachChild(bullet);
        }
    }

    public void machinegunAttack(){
        shoot();
    }

    @Override
    public void setThisGameState(GameState gameState) {
        thisGameState = gameState;
    }
}
