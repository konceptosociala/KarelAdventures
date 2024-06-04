package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.konceptosociala.kareladventures.game.player.Energy;
import org.konceptosociala.kareladventures.game.player.Health;

import org.konceptosociala.kareladventures.game.player.IUpdatable;
import org.konceptosociala.kareladventures.state.GameState;

public class Enemy extends Node implements IUpdatable {
    private static final String ENEMY_MODEL_NAME = "Models/simple_bug.glb";//boppin_ariados.glb
    private final Spatial model;
    private Node worldRoot;
    private RigidBodyControl characterCollider;
    private BulletAppState bulletAppState;
    //private Node enemyRoot;
    //private String name;
    private Health health;
    private Energy energy;
    private GameState thisGameState;
    private float XZVelocityVectorToYRotation;

    public Enemy(String name, int health, AssetManager assetManager, Node worldRoot, BulletAppState state, GameState gs) {
        this.worldRoot = worldRoot;
        this.bulletAppState = state;
        thisGameState = gs;
        this.setLocalTranslation(10,10,10);
        this.rotate(FastMath.HALF_PI,0,0);
        model = assetManager.loadModel(ENEMY_MODEL_NAME);
        model.rotate(-FastMath.HALF_PI,0,0);
        model.setLocalTranslation(0,0,0.2f);

        //super(name, new Box(1, 1, 1)); // Create a box-shaped enemy
        model.setName(name);
        //model.setUserData("this_enemy",this);
        this.attachChild(model);
        this.health = new Health(health);
        energy = new Energy(10);
        characterCollider = new RigidBodyControl(new CapsuleCollisionShape(0.6f,1f),0.3f);
        characterCollider.setFriction(1);
        characterCollider.setAngularFactor(0);
        //characterCollider.setApplyPhysicsLocal(true);
        this.addControl(characterCollider);
        worldRoot.attachChild(this);
        bulletAppState.getPhysicsSpace().addAll(this);
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
    public void update() {
        //this.health.subtract(1);
        if(!isAlive()){
            bulletAppState.getPhysicsSpace().remove(characterCollider);
            thisGameState.getEnemyRoot().detachChild(this);
            return;
        }
        XZVelocityVectorToYRotation = FastMath.atan2(characterCollider.getPhysicsLocation().x-thisGameState.getPlayer().getCharacterCollider().getPhysicsLocation().x,characterCollider.getPhysicsLocation().z-thisGameState.getPlayer().getCharacterCollider().getPhysicsLocation().z);
        rotateTowardsPlayer();
        moveTowardsPlayer();
    }
    private void rotateTowardsPlayer(){
        //characterCollider.setPhysicsRotation(characterCollider.getPhysicsRotation().add(new Quaternion().fromAngleAxis(XZVelocityVectorToYRotation,Vector3f.UNIT_Y)));
        characterCollider.setPhysicsRotation(new Quaternion().fromAngles(FastMath.HALF_PI,XZVelocityVectorToYRotation+FastMath.PI,0));
    }
    private void moveTowardsPlayer(){
        characterCollider.applyForce(rotateByYAxis(new Vector3f(2f,0,0),XZVelocityVectorToYRotation+FastMath.HALF_PI),new Vector3f().zero());
    }
    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }

    public void pushback(){
        characterCollider.applyImpulse(rotateByYAxis(new Vector3f(-2f,0,0),XZVelocityVectorToYRotation+FastMath.HALF_PI),new Vector3f().zero());
    }
}
