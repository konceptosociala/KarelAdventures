package org.konceptosociala.kareladventures.game.enemies;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import lombok.Setter;
import org.konceptosociala.kareladventures.game.player.Player;

public class BulletCollisionListener implements PhysicsCollisionListener {
    @Setter
    BulletAppState bulletAppState;
    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Get the involved objects in the collision
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();

        // Check if the collision involves the rigid body you want to destroy
        if (nodeA != null && nodeA instanceof Bullet) {
            destroyRigidBody(nodeA);
            if (nodeB instanceof Player){
                ((Player)nodeB).takeDamage(((Bullet)nodeA).getDamage());
            }
        } else if (nodeB != null && nodeB instanceof Bullet) {
            destroyRigidBody(nodeB);
            if (nodeA instanceof Player){
                ((Player)nodeA).takeDamage(((Bullet)nodeB).getDamage());
            }
        } else if ((nodeB != null && nodeB instanceof Bullet)&&(nodeA != null && nodeA instanceof Bullet)) {
        destroyRigidBody(nodeB);
        destroyRigidBody(nodeA);
        }
    }

    private void destroyRigidBody(Spatial spatial) {
        RigidBodyControl rigidBody = spatial.getControl(RigidBodyControl.class);
        if (rigidBody != null) {
            // Remove the control from the physics space
            spatial.removeControl(rigidBody);

            // Assuming you have access to the physics space
            bulletAppState.getPhysicsSpace().remove(rigidBody);

            // Detach the spatial from its parent (scene graph)
            spatial.removeFromParent();
        }
    }
}
