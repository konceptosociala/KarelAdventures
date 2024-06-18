package org.konceptosociala.kareladventures.game.farm;

import java.util.List;

import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.state.KarelFarmState;
import org.konceptosociala.kareladventures.utils.InteractableNode;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class KarelFarm extends InteractableNode {
    private static final String KAREL_FARM_MODEL_NAME = "Models/karelfarm.glb";

    private Spatial model;
    private CollisionShape collider;
    private RigidBodyControl rigidBodyControl;

    public KarelFarm(AssetManager assetManager, Vector3f position, Quaternion rotation, BulletAppState bulletAppState) {
        super("karel_farm");
        setLocalTranslation(position);
        setLocalRotation(rotation);
        this.model = assetManager.loadModel(KAREL_FARM_MODEL_NAME);
        this.collider = new BoxCollisionShape(new Vector3f(5, 5, 5));
        this.rigidBodyControl = new RigidBodyControl(collider, 1);
        this.rigidBodyControl.setKinematic(true);
        this.rigidBodyControl.setGravity(new Vector3f(0,0,0));
        this.model.addControl(rigidBodyControl);
        attachChild(model);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        bulletAppState.getPhysicsSpace().addAll(this);
    }

    @Override
    public void interact(GameState gameState, Player player) {
        boolean shouldInteract = false;

        Vector3f characterPosition = player.getCharacterControl().getPhysicsLocation();
        Vector3f rayFrom = characterPosition;
        Vector3f rayTo = characterPosition.add(player.getLocalRotation().getRotationColumn(0).mult(-2));

        List<PhysicsRayTestResult> results = gameState.getBulletAppState().getPhysicsSpace().rayTest(rayFrom, rayTo);
        for (PhysicsRayTestResult result : results) {
            if (result.getCollisionObject() == this.rigidBodyControl) {
                shouldInteract = true;
            }
        }

        if (!shouldInteract) return;

        var audio = gameState.getAudio();
        
        audio.ui1.play();

        gameState.getChaseCam().setEnabled(false);

        KarelFarmState karelFarmState = gameState.getKarelFarmState();
        karelFarmState.setEnabled(true);
    }
}
