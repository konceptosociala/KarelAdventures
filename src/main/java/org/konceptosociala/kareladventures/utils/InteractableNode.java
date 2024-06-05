package org.konceptosociala.kareladventures.utils;

import org.konceptosociala.kareladventures.game.player.Player;
import org.konceptosociala.kareladventures.state.GameState;

import com.jme3.scene.Node;

public abstract class InteractableNode extends Node {
    public abstract void interact(GameState gameState, Player player);
}
