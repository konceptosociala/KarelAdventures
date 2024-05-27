package org.konceptosociala.kareladventures.game.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
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
public class Player implements IUpdatable{
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
    private float speed = 1000f;
    public Player(AssetManager assetManager,Vector3f position) {
        playerRoot =new Node();
        model = assetManager.loadModel(PLAYER_MODEL_NAME);
        model.setLocalTranslation(10,10,10);
        //characterCollider = new BetterCharacterControl(1f, 30f, 1f);
        //characterControl.se
        //characterCollider.setJumpForce(new Vector3f(0, 100, 0));
        //characterCollider.setPhysicsDamping(1f);
        characterCollider = new RigidBodyControl(new CapsuleCollisionShape(1,1),1);
        model.addControl(characterCollider);
        playerRoot.attachChild(model);

        health = new Health(100);
        energy = new Energy(100);
        inventory = new Inventory();
    }

    
    public boolean isAlive() {
        return health.getValue() > 0;
    }

    public void update() {

    }
    public void jump(){
        characterCollider.applyImpulse(new Vector3f(0,10,0),new Vector3f(0,0,0));
        //characterCollider.jump();
    }
    public void moveForward(float value) {
        characterCollider.applyForce(new Vector3f(0,0,value*speed),new Vector3f(0,0,0));
        //model.move(characterCollider.getWalkDirection());
    }
    public void moveSideward(float value) {
        characterCollider.applyForce(new Vector3f(value*speed,0,0),new Vector3f(0,0,0));
        //model.move(characterCollider.getWalkDirection());
    }
}
