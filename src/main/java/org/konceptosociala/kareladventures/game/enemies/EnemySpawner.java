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
import org.konceptosociala.kareladventures.utils.IAmEnemySpawner;
import org.konceptosociala.kareladventures.utils.IUpdatable;

import lombok.Getter;
import lombok.Setter;

import static org.konceptosociala.kareladventures.KarelAdventures.LOG;

@Getter
@Setter
public class EnemySpawner extends Node implements IUpdatable, IAmEnemySpawner {
    private static final String ENEMY_MODEL_NAME = "Models/tower.glb";//boppin_ariados.glb

    private float spawnCooldownTime = 10f;
    private float spawnCooldownTimer = 0.0f;
    private boolean spawnAvailable = false;
    private int enemyHealth;
    private BulletAppState bulletAppState;
    private Node enemy = null;
    private Node enemyRoot = null;
    private Vector3f originPosition;

    public EnemySpawner(
            Vector3f position,
            Node enemyRoot,
            Node enemy,
            BulletAppState bulletAppState
    ){
        super();
        this.bulletAppState=bulletAppState;
        this.originPosition = position;
        this.setLocalTranslation(position);
        this.enemyHealth = ((IAmEnemy)enemy).getHealth();
        this.enemy = enemy;
        this.enemyRoot = enemyRoot;
        enemyRoot.attachChild(enemy);
    }

    @Override
    public void update(float tpf) {
        if(!((IAmEnemy)enemy).isAlive()){
            spawnAvailable = true;
        }
        if (spawnAvailable) {
            spawnCooldownTimer += tpf;
            if (spawnCooldownTimer >= spawnCooldownTime) {
                spawnAvailable = false;
                spawnCooldownTimer = 0.0f;
                ((IAmEnemy)enemy).setHealth(enemyHealth);
                enemyRoot.attachChild(enemy);
                bulletAppState.getPhysicsSpace().addAll(enemy);
            }
        }
    }

    @Override
    public void setThisGameState(GameState gameState) {
        ((IAmEnemy)enemy).setThisGameState(gameState);
    }
}
