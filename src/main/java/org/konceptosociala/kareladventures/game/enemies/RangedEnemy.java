package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.konceptosociala.kareladventures.game.player.Energy;
import org.konceptosociala.kareladventures.game.player.Health;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.IAmEnemy;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Getter;
import lombok.Setter;

import static org.konceptosociala.kareladventures.KarelAdventures.LOG;

@Getter
@Setter
public class RangedEnemy extends Node implements IUpdatable, IAmEnemy {
    private static final String ENEMY_MODEL_NAME = "Models/simple_bug.glb";//boppin_ariados.glb

    private BulletAppState bulletAppState;
    private GameState thisGameState;

    private Spatial model;
    private CollisionShape characterCollider;
    private RigidBodyControl characterControl;
    private Health health;
    private Energy energy;
    private float movementSpeed = 2f;
    private float XZVelocityVectorToYRotation;
    private AssetManager assetManager;
    private int damage = 6;
    private float attackCooldownTime = 1f;
    private float attackCooldownTimer = 0.0f;
    private boolean attackAvailable = true;
    private float agroRange = 50f;
    private float attackRange = 30f;
    private Vector3f originPosition;
    private Vector3f target;

    public RangedEnemy(
            Vector3f position,
            AssetManager assetManager,
            BulletAppState bulletAppState,
            int health
    ){
        super();
        this.originPosition = position;
        this.assetManager = assetManager;
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
    public void update(float tpf) {
        if(!isAlive()){
            bulletAppState.getPhysicsSpace().remove(characterControl);
            thisGameState.getEnemyRoot().detachChild(this);
            return;
        }
        setTarget();
        calculateYRotation(target);
        rotateTowardsTarget();
        moveTowardsTarget();
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
        var playerLocation = thisGameState.getPlayer().getCharacterControl().getPhysicsLocation();
        if(FastMath.sqrt(FastMath.pow(originPosition.x - playerLocation.x,2)+FastMath.pow(originPosition.z - playerLocation.z,2))>agroRange){
            target=originPosition;
        }else{
            target=playerLocation;
        }
    }
    private void calculateYRotation(Vector3f target){
        var location = characterControl.getPhysicsLocation();
        XZVelocityVectorToYRotation = FastMath.atan2(location.x - target.x, location.z - target.z);
    }
    /*private void performMeleeAttack(){
        Vector3f attackColliderOffset = new Vector3f();
        model.localToWorld(new Vector3f(0,0.5f,2),attackColliderOffset);
        var player = getPlayerInBox(attackColliderOffset,new Vector3f(0.5f,0.5f,0.2f),characterControl.getPhysicsRotation());
        //LOG.info("a");
        if(player.isPresent()&&attackAvailable){
            attackAvailable = false;
            player.get().takeDamage(damage);
            LOG.info(String.valueOf(player.get().getHealth().getValue()));
        }
    }*/

    private void performAttack(){
        if(attackAvailable){
            var location = characterControl.getPhysicsLocation();
            var playerLocation = thisGameState.getPlayer().getCharacterControl().getPhysicsLocation();
            if(FastMath.sqrt(FastMath.pow(location.x - playerLocation.x,2)+FastMath.pow(location.z - playerLocation.z,2))<attackRange){
                LOG.info("a");
                shoot();
                attackAvailable = false;
                attackCooldownTimer = 0.0f;

            }
        }
    }

    private void shoot() {
        Bullet bullet =  new Bullet(characterControl.getPhysicsLocation().add(0,1,0),thisGameState.getPlayer().getCharacterControl().getPhysicsLocation().add(0,0,0),assetManager,bulletAppState,"Models/enemy_bullet.glb");
        thisGameState.getRootNode().attachChild(bullet);
    }

    // private Optional<Player> getPlayerInBox(Vector3f center, Vector3f extents, Quaternion rotation) {
    //     //List<Enemy> enemies = new ArrayList<>();
    //     //Player player = null;
    //     Optional<Player> pl = Optional.empty();
    //     Box boxShape = new Box(extents.x, extents.y, extents.z);
    //     Geometry collider = new Geometry("Collider", boxShape);
    //     collider.setLocalTranslation(center);
    //     collider.setLocalRotation(rotation);
    //     Transform transform = new Transform(center, rotation);
    //     BoundingBox boundingBox = new BoundingBox(center, extents.x, extents.y, extents.z);
    //     boundingBox.transform(transform);
    //     // Create a material with an unshaded definition
    //     //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

    //     // Set the material color to blue
    //     //mat.setColor("Color", ColorRGBA.Blue);
    //     //collider.setMaterial(mat);
    //     if (boundingBox.intersects(thisGameState.getPlayer().getWorldBound())) {
    //         pl = Optional.of(thisGameState.getPlayer());
    //     }
    //     //thisGameState.getRootNode().attachChild(collider);
    //     return pl;
    // }

    private void rotateTowardsTarget(){
        //characterControl.setPhysicsRotation(characterControl.getPhysicsRotation().add(new Quaternion().fromAngleAxis(XZVelocityVectorToYRotation,Vector3f.UNIT_Y)));
        characterControl.setPhysicsRotation(new Quaternion().fromAngles(FastMath.HALF_PI,XZVelocityVectorToYRotation+FastMath.PI,0));
    }
    private void moveTowardsTarget(){
        var location = characterControl.getPhysicsLocation();
        var playerLocation = thisGameState.getPlayer().getCharacterControl().getPhysicsLocation();
        if(FastMath.sqrt(FastMath.pow(location.x - playerLocation.x,2)+FastMath.pow(location.z - playerLocation.z,2))>attackRange){
            characterControl.applyForce(rotateByYAxis(new Vector3f(movementSpeed,0,0),XZVelocityVectorToYRotation+FastMath.HALF_PI),new Vector3f().zero());
        }else{
            characterControl.setLinearVelocity(new Vector3f().zero().setY(characterControl.getLinearVelocity().y));
        }
    }
    private static Vector3f rotateByYAxis(Vector3f vec, float rad){
        return new Vector3f((float)(vec.x*Math.cos(rad)+vec.z*Math.sin(rad)), vec.y, (float)(-vec.x*Math.sin(rad)+vec.z*Math.cos(rad)));
    }

    public void pushback(){
        characterControl.applyImpulse(rotateByYAxis(new Vector3f(-2f,0,0),XZVelocityVectorToYRotation+FastMath.HALF_PI),new Vector3f().zero());
    }

    @Override
    public void setThisGameState(GameState gameState) {
        thisGameState = gameState;
    }
}