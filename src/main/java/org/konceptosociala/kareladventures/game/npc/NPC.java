package org.konceptosociala.kareladventures.game.npc;

import java.util.List;

import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.state.GameState;
import org.konceptosociala.kareladventures.utils.InteractableNode;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NPC extends InteractableNode {
    private String name;
    private Spatial model; 
    private Dialog dialog;
    private CollisionShape collider;
    private RigidBodyControl rigidBodyControl;

    public NPC(
        String name, 
        String modelPath, 
        String idleAnimationName, 
        Dialog dialog, 
        AssetManager assetManager,
        BulletAppState bulletAppState
    ){
        super();
        this.model = assetManager.loadModel(modelPath);
        this.collider = new CapsuleCollisionShape(0.5f, 3f);
        this.rigidBodyControl = new RigidBodyControl(collider, 1);
        this.rigidBodyControl.setKinematic(true);
        this.rigidBodyControl.setGravity(new Vector3f(0,0,0));
        this.model.addControl(rigidBodyControl);
        attachChild(model);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        bulletAppState.getPhysicsSpace().addAll(this);

        this.name = name;
        this.dialog = dialog;
    }

    @Override
    public void interact(GameState gameState, Player player) {
        boolean shouldInteract = false;

        Vector3f characterPosition = player.getCharacterControl().getPhysicsLocation();
        Vector3f rayFrom = characterPosition;
        Vector3f rayTo = characterPosition.add(player.getLocalRotation().getRotationColumn(2).mult(2));
        List<PhysicsRayTestResult> results = gameState.getBulletAppState().getPhysicsSpace().rayTest(rayFrom, rayTo);
        for (PhysicsRayTestResult result : results) {
            if (result.getCollisionObject() == this.rigidBodyControl) {
                shouldInteract = true;
            }
        }

        if (!shouldInteract) return;

        System.exit(0);

        // DialogState dialogState = gameState.getDialogState();
        // dialogState.setDialog(Optional.of(dialog));
        // dialogState.setEnabled(true);
    }
}
