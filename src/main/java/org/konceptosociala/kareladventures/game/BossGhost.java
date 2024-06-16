package org.konceptosociala.kareladventures.game;

import org.konceptosociala.kareladventures.game.player.Player;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import lombok.Getter;

@Getter
public class BossGhost extends Node {
    private static final String BOSS_GHOST_MODEL = "Models/boss_ghost.glb";
    private final Spatial model;

    public BossGhost(AssetManager assetManager) {
        super("Boss Ghost");
        model = assetManager.loadModel(BOSS_GHOST_MODEL);
        model.setCullHint(CullHint.Always);
        attachChild(model);
    }

    public boolean enterPlayer(Player player) {
        CollisionResults playerResults = new CollisionResults();
        player.collideWith(this.getWorldBound(), playerResults);

        CollisionResults ghostResults = new CollisionResults();
        this.collideWith(player.getWorldBound(), ghostResults);

        int mult = playerResults.size() * ghostResults.size();
        return mult > 0;
    }
}
