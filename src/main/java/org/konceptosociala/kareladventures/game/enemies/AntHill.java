package org.konceptosociala.kareladventures.game.enemies;

//package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.konceptosociala.kareladventures.game.player.Health;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AntHill extends Node implements IUpdatable, IAmEnemy {
    private static final String ENEMY_MODEL_NAME = "Models/BugNest.glb";

    private BulletAppState bulletAppState;
    private GameState thisGameState;

    private Spatial model;
    private CollisionShape characterCollider;
    private RigidBodyControl characterControl;
    private Health health;
    private float movementSpeed = 2f;
    private float XZVelocityVectorToYRotation;
    private AssetManager assetManager;
    private int spawningCount = 10;
    private SmallBug[] spawnlings = new SmallBug[10];
    private float attackCooldownTime = 7f;
    private float attackCooldownTimer = 0.0f;
    private boolean attackAvailable = true;
    private float spawnRadius = 10;
    private Vector3f originPosition;
    private Vector3f target;

    public AntHill(
            Vector3f position,
            AssetManager assetManager,
            BulletAppState bulletAppState
    ){
        super();
        this.originPosition = position;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.setLocalTranslation(position);
        this.model = assetManager.loadModel(ENEMY_MODEL_NAME);
        this.model.setLocalTranslation(0,-0.5f,0);
        this.model.scale(3f);

        this.model.setName(name);
        this.attachChild(model);
        this.health = new Health(100);
        this.characterCollider = new BoxCollisionShape(new Vector3f(1.5f,1,1.5f));
        this.characterControl = new RigidBodyControl(this.characterCollider,10000f);
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

    @Override
    public void setHealth(int health) {
        this.health.setValue(health);
    }

    public boolean isAlive() {
        return health.getValue() > 0;
    }
    public void receiveDamage(int amount){
        this.health.subtract(amount);
    }

    @Override
    public void update(float tpf) {
        if(!isAlive()){
            bulletAppState.getPhysicsSpace().remove(characterControl);
            thisGameState.getEnemyRoot().detachChild(this);
            return;
        }
        setTarget();
        calculateYRotation(target);
        if (!attackAvailable) {
            attackCooldownTimer += tpf;
            if (attackCooldownTimer >= attackCooldownTime) {
                attackAvailable = true;
                attackCooldownTimer = 0.0f;
            }
        }
        performAttack();
    }
    private void setTarget(){
    }
    private void calculateYRotation(Vector3f target){
    }
    private void performAttack(){
        if(attackAvailable){
            var location = characterControl.getPhysicsLocation();
            for (int i = 0;i<spawnlings.length;i++){
                if(spawnlings[i] == null||spawnlings[i].alive == false){
                    spawnBug(i,spawnlings.length);
                    attackAvailable = false;
                    attackCooldownTimer = 0.0f;
                    break;
                }
            }
        }
    }

    private void spawnBug(int i, int length) {
        float angle = (FastMath.TWO_PI/length)*i;
        Vector3f pos = characterControl.getPhysicsLocation().add(new Vector3f(FastMath.sin(angle)*spawnRadius,0,FastMath.cos(angle)*spawnRadius));
        SmallBug s = new SmallBug(pos,assetManager,bulletAppState,30,i);
        spawnlings[i]=s;
        thisGameState.getEnemyRoot().attachChild(s);
        s.setThisGameState(thisGameState);
    }

    public void pushback(){
    }

    @Override
    public void setThisGameState(GameState gameState) {
        thisGameState = gameState;
    }
}
