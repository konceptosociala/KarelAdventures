package org.konceptosociala.kareladventures.game.player;

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
    private BetterCharacterControl characterControl;
    private final Spatial model;

    @Getter
    @Setter
    private Node playerRoot;

    private Health health;
    private Energy energy;
    private Inventory inventory;
    public Player(AssetManager assetManager,Vector3f position) {
        playerRoot =new Node();
        model = assetManager.loadModel(PLAYER_MODEL_NAME);
        model.setLocalTranslation(10,10,10);
        characterControl = new BetterCharacterControl(1f, 1f, 1000f);
        characterControl.setJumpForce(new Vector3f(0, 10, 0));
        characterControl.setPhysicsDamping(1f);
        model.addControl(characterControl);
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
        characterControl.jump();
    }

}
