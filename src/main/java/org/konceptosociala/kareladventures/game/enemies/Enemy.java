package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.konceptosociala.kareladventures.game.player.Energy;
import org.konceptosociala.kareladventures.game.player.Health;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Enemy extends Node implements IUpdatable {
    private static final String ENEMY_MODEL_NAME = "Models/simple_bug.glb";//boppin_ariados.glb

    private BulletAppState bulletAppState;
    private GameState thisGameState;

    private Spatial model;
    private CollisionShape characterCollider;
    private RigidBodyControl characterControl;
    private Health health;
    private Energy energy;
    private float XZVelocityVectorToYRotation;

    public Enemy(
        Vector3f position, 
        AssetManager assetManager, 
        BulletAppState bulletAppState,
        int health
    ){
        super();

        this.bulletAppState = bulletAppState;
        this.setLocalTranslation(position);
        this.rotate(FastMath.HALF_PI,0,0);
        this.model = assetManager.loadModel(ENEMY_MODEL_NAME);
        this.model.rotate(-FastMath.HALF_PI,0,0);
        this.model.setLocalTranslation(0,0,0.2f);

        this.model.setName(name);
        this.attachChild(model);
        this.health = new Health(health);
        this.energy = new Energy(10);
        this.characterCollider = new CapsuleCollisionShape(0.6f,1f);
        this.characterControl = new RigidBodyControl(this.characterCollider,0.3f);
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
    public void update() {
        if(!isAlive()){
            bulletAppState.getPhysicsSpace().remove(characterControl);
            thisGameState.getEnemyRoot().detachChild(this);
            return;
        }

        var location = characterControl.getPhysicsLocation();
        var playerLocation = thisGameState.getPlayer().getCharacterControl().getPhysicsLocation();

        XZVelocityVectorToYRotation = FastMath.atan2(location.x - playerLocation.x, location.z - playerLocation.z);
        rotateTowardsPlayer();
        moveTowardsPlayer();
    }
    private void rotateTowardsPlayer(){
        //characterControl.setPhysicsRotation(characterControl.getPhysicsRotation().add(new Quaternion().fromAngleAxis(XZVelocityVectorToYRotation,Vector3f.UNIT_Y)));
        characterControl.setPhysicsRotation(new Quaternion().fromAngles(FastMath.HALF_PI,XZVelocityVectorToYRotation+FastMath.PI,0));
    }
    private void moveTowardsPlayer(){
        characterControl.applyForce(rotateByYAxis(new Vector3f(2f,0,0),XZVelocityVectorToYRotation+FastMath.HALF_PI),new Vector3f().zero());
    }
    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }

    public void pushback(){
        characterControl.applyImpulse(rotateByYAxis(new Vector3f(-2f,0,0),XZVelocityVectorToYRotation+FastMath.HALF_PI),new Vector3f().zero());
    }
}
